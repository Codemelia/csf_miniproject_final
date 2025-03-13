import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TipService } from '../../services/tip.service';
import { loadStripe, Stripe, StripeCardElement, StripeCardElementChangeEvent } from '@stripe/stripe-js';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TipRequest, TipResponse } from '../../models/app.models';
import { catchError, map, of, Subscription, switchMap } from 'rxjs';
import { InvalidTokenError, jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { environment } from '../../environments/environment';

@Component({
  selector: 'app-tip-coupon',
  standalone: false,
  templateUrl: './tip-coupon.component.html',
  styleUrl: './tip-coupon.component.scss'
})
export class TipCouponComponent implements OnInit, OnDestroy {

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
  tipperName: string = 'Anonymous'
  tipperEmail: string = ''

  // for stripe token generation
  stripe: Stripe | null = null
  card!: StripeCardElement
  cardComplete: boolean = false

  // feedback messages for view
  errorMsg: string | null = null
  successMsg: string | null = null

  // for sending tip request to server
  protected request!: TipRequest
  protected tipSub!: Subscription

  // token key and extracted userId
  tokenKey: string = 'auth_token'
  protected token: string = ''
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

    // take in form values
    this.artisteStageName = this.form.value.artisteStageName
    this.amount = this.form.value.amount
    if (this.form.value.tipperName) {
      this.tipperName = this.form.value.tipperName
    }
    if (this.form.value.tipperEmail) {
      this.tipperEmail = this.form.value.tipperEmail
    }

    // if tipper id invalid, request user to login again and navigate to login page
    if (this.tipperId == null) {
      this.errorMsg = 'Invalid token. Please log in again.'
      this.authSvc.logout()
      return
    } else

    // if either field does not exist, return error message for display
    if (!this.artisteStageName || this.amount == 0) {
      this.errorMsg = 'Please ensure all fields are filled.'
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

    // build tip request to server backend
    this.request = {
      tipperId: this.tipperId,
      artisteStageName: this.artisteStageName,
      amount: this.amount
    }
    console.log('>>> Tip request built: ', this.request)

    // send tip to server
    // subscribe to the svc and return response/error accordingly
    this.tipSub = this.tipSvc.processTip(this.request).pipe(
      switchMap(async (response: TipResponse) => {
        // extract the client secret from the response
        const clientSecret = response.clientSecret
        console.log('>>> Received client secret: ', clientSecret)

        // use Stripe.js to confirm the payment
        const confirmResult = await this.stripe!.confirmCardPayment(clientSecret, {
          payment_method: {
            card: this.card,
            billing_details: { name: this.tipperName, email: this.tipperEmail }
          }
        })

        // check if payment confirmation failed or succeeded
        if (confirmResult.error && confirmResult.error.message) {
          this.errorMsg = confirmResult.error.message
          this.successMsg = null
          console.error('>>> Payment confirmation error: ', confirmResult.error);
        } else if (confirmResult.paymentIntent && confirmResult.paymentIntent.status === 'succeeded') {
          this.successMsg = 'Tip successful!'
          this.errorMsg = null
          console.log('>>> Payment succeeded: ', confirmResult.paymentIntent)
        }

        // send update request to backend
        if (confirmResult.paymentIntent) {
          this.tipSvc.confirmTip(confirmResult.paymentIntent)
        }

      }),
      catchError(error => {
        this.errorMsg = 'Payment failed. Please try again.'
        this.successMsg = null
        console.error('>>> Tip failed: ', error)
        return of(null)
      })
    ).subscribe()

  }

  // unsub from svc on destroy
  ngOnDestroy(): void {
    if (this.tipSub) {
      this.tipSub.unsubscribe()
    }
  }

}