import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TipService } from '../../services/tip.service';
import { loadStripe, Stripe, StripeCardElement, StripeCardElementChangeEvent } from '@stripe/stripe-js';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiError, Tip } from '../../models/app.models';
import { catchError, map, of, Subscription, switchMap, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-tip',
  standalone: false,
  templateUrl: './tip.component.html',
  styleUrl: './tip.component.scss'
})
export class TipComponent implements OnInit, OnDestroy {

  // form init
  private fb = inject(FormBuilder)
  protected form!: FormGroup

  // tip svc injection
  private tipSvc = inject(TipService)
  private authSvc = inject(AuthService)
  private router = inject(Router)

  // receiving tip state
  amount: number = 0
  artisteStageName: string | null = null
  tipperName: string = ''
  tipperEmail: string = ''

  // for stripe token generation
  stripe: Stripe | null = null
  card!: StripeCardElement
  cardComplete: boolean = false

  // feedback messages for view
  errorMsg: string | null = null
  successMsg: string | null = null

  // for sending tip request to server
  protected unconfirmRequest!: Tip
  protected confirmRequest!: Tip

  protected clientSecretSub!: Subscription
  protected saveTipSub!: Subscription

  // userid from token
  protected tipperId: string | null = null

  async ngOnInit() {

    // create form on init
    this.form = this.createTipFrom()

    // load stripe with public key
    this.stripe = await loadStripe(environment.stripePublicKey)

    // validate stripe
    if (!this.stripe) {
      this.errorMsg = 'Failed to load Stripe. Please refresh the page.'
      return
    }

    // destroy the old card element before creating a new one
    if (this.card) {
      this.card.destroy(); 
    } 

    // create and mount stripe card element
    this.card = this.stripe.elements().create('card')
    this.card.mount('#card-element')

    // card validation on change
    this.card.on('change', (event: StripeCardElementChangeEvent) => {
      this.cardComplete = event.complete // set completion status
      console.log('>>> Card completed: ', this.cardComplete)
      if (event.error) {
        this.errorMsg = event.error.message // if complete but with error
      } else if (!event.complete) {
        this.errorMsg = null // if incomplete but no specific error
      }
    })

  }

  // create form method
  // card validation handled separately
  createTipFrom() {
    return this.fb.group({
      tipperName: this.fb.control<string>(''),
      tipperEmail: this.fb.control<string>(''), 
      amount: this.fb.control<number>(0,
        [ Validators.required, Validators.min(1) ]),
      artisteStageName: this.fb.control<string>('', 
        [ Validators.required ])
    })
  }

  // tip artiste method
  async tipArtiste() {

    // get token from local storage
    // extract user id as tipper id
    this.tipperId = this.authSvc.extractUIDFromToken()

    // if tipper id invalid, request user to login again and navigate to login page
    if (this.tipperId == null) {
      this.errorMsg = 'Invalid token. Please log in again.'
      this.authSvc.logout()
      return
    } else

    // validate card and stripe
    if (!this.card || !this.stripe) {
      this.errorMsg = 'Payment system not initialized. Please refresh the page.'
      return
    } else

    // validate card completion - setting this custom since card validation is not on form
    if (!this.cardComplete) {
      this.errorMsg = 'Please complete your card details.'
      return
    }

    // take in form values
    this.artisteStageName = this.form.value.artisteStageName
    this.amount = this.form.value.amount
    if (this.form.value.tipperName) {
      this.tipperName = this.form.value.tipperName
    }
    if (this.form.value.tipperEmail) {
      this.tipperEmail = this.form.value.tipperEmail
    }

    // if either mandatory field does not exist, return error message for display
    if (!this.artisteStageName || this.amount == 0) {
      this.errorMsg = 'Please ensure all fields are filled.'
      return
    } 

    // build tip request to server backend
    this.unconfirmRequest = this.buildUnconfirmRequest(
      this.tipperId, this.artisteStageName, this.amount)

    // send tip to server
    // subscribe to the svc and return response/error accordingly
    this.clientSecretSub = this.tipSvc.getPaymentIntentClientSecret(this.unconfirmRequest).pipe(
      switchMap(async (response) => {
        // extract the client secret from the response
        const clientSecret = response
        console.log('>>> Received client secret: ', clientSecret)

        // use Stripe.js to confirm the payment
        const confirmResult = await this.stripe!.confirmCardPayment(clientSecret, {
          payment_method: {
            card: this.card,
            billing_details: { name: this.tipperName, email: this.tipperEmail }
          }
        })

        // if payment intent was confirmed, process tip
        if (confirmResult.paymentIntent && confirmResult.paymentIntent.status === 'succeeded') {
          // build confirm request to save tip on server
          this.confirmRequest = this.buildConfirmRequest(
            this.tipperId!, this.artisteStageName!, this.amount,
              confirmResult.paymentIntent.id, confirmResult.paymentIntent.status
          )

          // save tip on server
          this.saveTipSub = this.tipSvc.saveTip(this.confirmRequest).pipe(
            tap(response => {
              if (response != null && response > 0) { // tip id will always be > 0
                this.successMsg = 'Payment was successful! Thank you for supporting our Vibees.'
                this.errorMsg = null
                console.log('>>> Payment successful')
              } else {
                this.successMsg = null
                this.errorMsg = 'There was an error processing your payment. Please contact support.'
                console.log('>>> Payment failed')
              }
            }),
            catchError((err: ApiError) => {
              this.successMsg = null
              this.errorMsg = err.message
              console.error('Tip save error:', err)
              return of(null)
            })
          ).subscribe()

        // if payment intent not confirmed, show error message
        } else if (confirmResult.error && confirmResult.error.message) {
          this.errorMsg = confirmResult.error.message
          this.successMsg = null
          console.error('>>> Payment confirmation error: ', confirmResult.error);
        } 

      }),
      catchError(error => {
        this.errorMsg = 'There was an error processing your payment. Please try again.'
        this.successMsg = null
        console.error('>>> Tip failed: ', error)
        return of(null)
      })
    ).subscribe()

  }

  // build unconfirmed request
  buildUnconfirmRequest(tipperId: string, artisteStageName: string, amount: number): Tip {
    // build tip request to server backend
    const unconfirmRequest: Tip = {
      tipId: null,
      tipperId: tipperId,
      artisteId: null,
      amount: amount,
      paymentIntentId: null,
      paymentStatus: null,
      createdAt: null,
      updatedAt: null,
      stageName: artisteStageName
    }

    console.log('>>> Tip request for client secret built: ', unconfirmRequest)
    return unconfirmRequest
  }

  // build confirmed request
  buildConfirmRequest(tipperId: string, artisteStageName: string, amount: number,
    paymentIntentId: string, paymentStatus: string
  ): Tip {
    const confirmRequest: Tip = {
      tipId: null,
      tipperId: tipperId,
      artisteId: null,
      amount: amount,
      paymentIntentId: paymentIntentId,
      paymentStatus: paymentStatus,
      createdAt: null,
      updatedAt: null,
      stageName: artisteStageName
    }
    console.log('>>> Tip request for saving built: ', confirmRequest)
    return confirmRequest
  }

  // unsub from svc on destroy
  ngOnDestroy(): void {
    if (this.clientSecretSub) { this.clientSecretSub.unsubscribe() }
    if (this.saveTipSub) { this.saveTipSub.unsubscribe() }
  }

}