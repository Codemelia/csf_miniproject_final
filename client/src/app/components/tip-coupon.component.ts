import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { TipService } from '../services/tip.service';
import { loadStripe, Stripe, StripeCardElement, StripeCardElementChangeEvent } from '@stripe/stripe-js';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TipRequest } from '../models/app.models';
import { catchError, map, of, Subscription } from 'rxjs';
import { InvalidTokenError, jwtDecode } from 'jwt-decode';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

// stripe PUBLIC key
const environment = {
  stripePublicKey: 'pk_test_51QzyksD0mkd7E2ujATCwVlmQrp1bQNxCyycEugk9gbgDW7qwMwrTok9F4ZqY6P7filFLV0Pv93YDehpUUo0KQD6800YZCLTVqX'
}

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
  musicianId: number | null = null

  // for stripe token generation
  stripe: Stripe | null = null
  card: StripeCardElement | null = null
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
  protected tipperId: number | null = null

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
      amount: this.fb.control<number>(0,
        [ Validators.required, Validators.min(1) ]),
      musicianId: this.fb.control<string>('', 
        [ Validators.required ])
    })
  }

  // tip musician method
  async tipMusician() {

    // get token from local storage
    // extract user id as tipper id
    this.tipperId = this.authSvc.extractUIDFromToken()

    // take in form values
    this.musicianId = Number(this.form.value.musicianId)
    this.amount = this.form.value.amount

    // if tipper id invalid, request user to login again and navigate to login page
    if (this.tipperId == null) {
      this.errorMsg = 'Invalid JWT token. Please log in again.'
      setTimeout(() => this.router.navigate(['/']), 3000)
      return
    }

    // if either field does not exist, return error message for display
    if (this.musicianId == null || this.amount == 0) {
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

    // generate stripe token
    const result = await this.stripe.createToken(this.card)

    // if stripe result error, set error message
    if (result.error && result.error.message) {
      this.errorMsg = result.error.message
    } 
    
    // if success and stripe result has token
    // prepare tip request
    if (result.token) {

      this.request = {
        tipperId: this.tipperId,
        musicianId: this.musicianId,
        amount: this.amount,
        stripeToken: result.token.id // gets token id from stripe result
      }
      console.log('>>> Tip request built: ', this.request)

      // send tip to server
      // subscribe to the svc and return response/error accordingly
      this.tipSub = this.tipSvc.insertTip(this.request).pipe(
        map(response => {
          this.successMsg = 'Tip successful!'
          this.errorMsg = null // set to null to prevent confusion
          console.log('>>> Tip successful: ', response)
        }),
        catchError(error => {
          this.errorMsg = 'Payment failed. Please try again.'
          this.successMsg = null // set to null to prevent confusion
          console.error('>>> Tip failed: ', error)
          return of(null)
        })).subscribe()

    } else {
      this.errorMsg = 'Stripe token generation failed. Please try again.' // if token generation fails
    }

  }

  // unsub from svc on destroy
  ngOnDestroy(): void {
    if (this.tipSub) {
      this.tipSub.unsubscribe()
    }
  }

}