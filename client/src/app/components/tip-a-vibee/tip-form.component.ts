import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TipService } from '../../services/tip.service';
import { loadStripe, Stripe, StripeCardElement, StripeCardElementChangeEvent } from '@stripe/stripe-js';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ApiError, Tip, TipResponse } from '../../models/app.models';
import { catchError, map, of, Subscription, switchMap, tap } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../environments/environment';
import { emailOrEmptyValidator } from '../../validatorfns/email-or-empty.validator';
import { MatDialog } from '@angular/material/dialog';
import { SuccessPopupComponent } from './success-popup.component';

@Component({
  selector: 'app-tip',
  standalone: false,
  templateUrl: './tip-form.component.html',
  styleUrl: './tip-form.component.scss'
})
export class TipFormComponent implements OnInit, OnDestroy {

  // form init
  private fb = inject(FormBuilder)
  protected form!: FormGroup

  // tip svc injection
  private tipSvc = inject(TipService)
  private authSvc = inject(AuthService)
  private route = inject(ActivatedRoute)

  // receiving tip state
  amount: number = 0
  artisteStageName: string | null = null
  tipperName: string |null = null
  tipperMessage: string | null = null
  tipperEmail: string | null = null

  // for stripe token generation
  stripe: Stripe | null = null
  card!: StripeCardElement
  cardComplete: boolean = false

  // feedback messages for view
  error!: ApiError
  private dialog = inject(MatDialog)

  // for sending tip request to server
  protected unconfirmRequest!: Tip
  protected confirmRequest!: Tip

  // subscriptions
  private clientSecretSub!: Subscription
  private saveTipSub!: Subscription
  private pathVarSub!: Subscription

  // userid from token
  protected tipperId: string | null = null

  async ngOnInit() {

    // create form on init
    this.form = this.createTipFrom()

    // assign artisteStageName field to path variable passed in
    // if not null, set path var to artiste stage name form field
    this.pathVarSub = this.route.paramMap.subscribe(
      params => {
        this.artisteStageName = params.get('artisteStageName')
        if (this.artisteStageName) {
          this.form.patchValue({ artisteStageName: this.artisteStageName })
        }
      }
    )

    // load stripe with public key
    this.stripe = await loadStripe(environment.stripePublicKey)

    // validate stripe
    if (!this.stripe) {
      this.error = {
        timestamp: new Date(),
        status: 500,
        error: 'Stripe Load Error',
        message: 'Payment system failed to initialise. Please refresh the page.'
      }
      return
    }

    // mount card element
    this.mountCardElement(this.stripe)

  }

  // create form method
  // card validation handled separately
  createTipFrom() {
    return this.fb.group({
      tipperName: this.fb.control<string | null>(null,
        [ Validators.maxLength(100)]
      ),
      tipperMessage: this.fb.control<string | null>(null,
        [ Validators.maxLength(255) ]
      ),
      tipperEmail: this.fb.control<string | null>(null,
        [ emailOrEmptyValidator(), Validators.maxLength(254) ] // custom validator to allow empty input
      ), 
      amount: this.fb.control<number>(0,
        [ Validators.required, Validators.min(1) ]),
      artisteStageName: this.fb.control<string>('', 
        [ Validators.required ])
    })
  }

  // mount stripe card
  mountCardElement(stripe: Stripe) {
    // destroy the old card element before creating a new one
    if (this.card) {
      this.card.destroy(); 
    } 

    // create and mount stripe card element
    this.card = stripe.elements().create('card')
    this.card.mount('#card-element')

    // card validation on change
    this.card.on('change', (event: StripeCardElementChangeEvent) => {
      this.cardComplete = event.complete // set completion status
      console.log('>>> Card completed: ', this.cardComplete)
      if (event.error) {
        this.error = {
          timestamp: new Date(),
          status: 500,
          error: 'Stripe Card Error',
          message: event.error.message
        }
      }
    })
  }

  // tip artiste method
  async tipArtiste() {

    // get token from local storage
    // extract user id as tipper id
    // allow null tipper id for guests
    this.tipperId = this.authSvc.extractUIDFromToken()

    // validate card and stripe
    if (!this.card || !this.stripe) {
      this.error = {
        timestamp: new Date(),
        status: 500,
        error: 'Stripe Load Error',
        message: 'Payment system failed to initialise. Please refresh the page.'
      }
      return
    } else

    // validate card completion - setting this custom since card validation is not on form
    if (!this.cardComplete) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Invalid Card Error',
        message: 'Please complete your card details.'
      }
      return
    }

    // take in form values
    this.artisteStageName = this.form.value.artisteStageName
    this.amount = this.form.value.amount
    if (this.form.value.tipperName) {
      this.tipperName = this.form.value.tipperName
    }
    if (this.form.value.tipperMessage) {
      this.tipperMessage = this.form.value.tipperMessage
    }
    if (this.form.value.tipperEmail) {
      this.tipperEmail = this.form.value.tipperEmail
    }

    // build tip request to server backend
    this.unconfirmRequest = this.buildUnconfirmRequest(
      this.tipperName, this.tipperMessage, this.tipperEmail,
      this.tipperId, this.artisteStageName, this.amount)

    // send tip to server
    // subscribe to the svc and return response/error accordingly
    this.clientSecretSub = this.tipSvc.getPaymentIntentClientSecret(this.unconfirmRequest).pipe(
      switchMap(async (response) => {
        // extract the client secret from the response
        const tipResponse: TipResponse = response
        console.log('>>> Received tip response: ', tipResponse)

        if (!tipResponse.tipperId || !tipResponse.clientSecret) {
          this.error = {
            timestamp: new Date(),
            status: 500,
            error: 'Internal Server Error',
            message: 'There was an error processing your payment. Please try again.'
          }
          return
        }

        this.tipperId = tipResponse.tipperId // assign tipper id from tip response

        // use Stripe.js to confirm the payment
        const confirmResult = await this.stripe!.confirmCardPayment(tipResponse.clientSecret, {
          payment_method: {
            card: this.card,
            billing_details: { name: this.tipperName, email: this.tipperEmail }
          }
        })

        // if payment intent was confirmed, process tip
        if (confirmResult.paymentIntent && confirmResult.paymentIntent.status === 'succeeded') {
          // build confirm request to save tip on server
          this.confirmRequest = this.buildConfirmRequest(
            this.tipperName, this.tipperMessage, this.tipperEmail,
            this.tipperId, this.artisteStageName!, this.amount,
              confirmResult.paymentIntent.id, confirmResult.paymentIntent.status
          )

          // save tip on server
          this.saveTipSub = this.tipSvc.saveTip(this.confirmRequest).pipe(
            tap(thankYouMessage => { // method returns artiste's thank you message as response
              if (thankYouMessage != null && thankYouMessage.length > 0) { // validate ty response
                console.log('>>> Payment successful')
                this.form.reset() // reset form when payment goes through
                this.onPaymentSuccess(thankYouMessage) // send artiste ty message to dialog and open
                if (this.stripe) this.mountCardElement(this.stripe) // remount card element
              } else {
                this.error = {
                  timestamp: new Date(),
                  status: 500,
                  error: 'Internal Server Error',
                  message: 'There was an error processing your payment. Please try again.'
                }
                console.log('>>> Payment failed')
              }
            }),
            catchError(error => {
              this.error = error.error
              console.error('Tip save error:', error)
              return of(null)
            })
          ).subscribe()

        // if payment intent not confirmed, show error message
        } else if (confirmResult.error && confirmResult.error.message) {
          this.error = {
            timestamp: new Date(),
            status: 400,
            error: 'Stripe Payment Error',
            message: confirmResult.error.message
          }
          console.error('>>> Payment confirmation error: ', confirmResult.error);
        } 

      }),
      catchError(error => {
        this.error = error.error
        console.error('>>> Tip failed: ', error)
        return of(null)
      })
    ).subscribe()

  }

  // build unconfirmed request: sends info to server backend
  // for server to generate client secret and pass back to frontend
  buildUnconfirmRequest(tipperName: string | null, tipperMessage: string | null, tipperEmail: string | null, 
    tipperId: string | null, artisteStageName: string | null, amount: number): Tip {
    const unconfirmRequest: Tip = {
      tipId: null,
      tipperName: tipperName,
      tipperMessage: tipperMessage,
      tipperEmail: tipperEmail,
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

  // build confirmed request: sends data from confirmed payment to server backend
  // for server to save data into db 
  buildConfirmRequest(tipperName: string | null, tipperMessage: string | null, tipperEmail: string | null,
    tipperId: string, artisteStageName: string, amount: number, paymentIntentId: string, paymentStatus: string
  ): Tip {
    const confirmRequest: Tip = {
      tipId: null,
      tipperName: tipperName,
      tipperMessage: tipperMessage,
      tipperEmail: tipperEmail,
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
  
  // on payment success, popup window displaying thank you message
  onPaymentSuccess(artisteThankYouMessage: string): void {

    // get dialog data
    const dialogRef = this.dialog.open(SuccessPopupComponent, {
      width: '400px',
      data: { artisteThankYouMessage: artisteThankYouMessage }
    })
  }

  // unsub from svc on destroy
  ngOnDestroy(): void {
    if (this.clientSecretSub) this.clientSecretSub.unsubscribe()
    if (this.saveTipSub) this.saveTipSub.unsubscribe()
    if (this.pathVarSub) this.pathVarSub.unsubscribe()
  }

}