import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { ApiError, Tip } from '../../models/app.models';
import { Subscription, tap } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtisteService } from '../../services/artiste.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy {

  // inject db service
  private dbSvc = inject(DashboardService)
  private authSvc = inject(AuthService)
  protected artisteSvc = inject(ArtisteService)
  private router = inject(Router)
  private route = inject(ActivatedRoute)

  // tip details
  tips: Tip[] = [] // to hold tip objects
  totalTips: number = 0 // to hold total sum amount
  columns: string[] = ['id', 'amount', 'paymentIntentId'] // for display

  // sub
  private dbSub!: Subscription
  private artisteSub!: Subscription
  private querySub!: Subscription
  private stripeSub!: Subscription

  protected artisteId: string | null = null
  protected artisteExists: boolean = false
  protected isStripeLinked: boolean = false
  
  isLoading: boolean = true // for api loading

  error!: ApiError

  headerWelcome: string | null = null // header string for welcome smg

  ngOnInit(): void {
    this.artisteId = this.authSvc.extractUIDFromToken()
    if (this.artisteId) {
      this.stripeSub = this.artisteSvc.stripeAccessTokenExists(this.artisteId).subscribe({
        next: (resp) => {
          this.isStripeLinked = resp
        },
        error: (err) => { 
          console.log('>>> Stripe has not been linked')
        }
      })
    }

    this.querySub = this.route.queryParams.subscribe(params => {
      const fromStripe = params['stripeComplete']
      if (fromStripe === 'true') {
        this.headerWelcome = 'Welcome onboard, Vibee!'
        setTimeout(() => {
          this.router.navigate(['/dashboard']) // safe refresh
        }, 2000)
      }
    })

    if (this.artisteId != null) {
      this.isLoading = true
      this.artisteSub = this.artisteSvc.artisteExists(this.artisteId)
        .pipe(
          tap({
            next: (resp) => {
              this.artisteExists = resp
              console.log('>>> Artiste exists: ', this.artisteExists)
              if (this.artisteExists) { this.loadTips() }
              this.isLoading = false
            },
            error: (err) => {
              this.error = err
              this.isLoading = false }
          })).subscribe()
    } else {
      this.isLoading = false
    }
  }

  // to retrieve tips by artiste id
  loadTips() {
    if (this.artisteId != null) {
      this.dbSub = this.dbSvc.getTips(this.artisteId).subscribe(
        (tips: Tip[]) => {
          this.tips = tips
          this.totalTips = tips.reduce((sum, tip) => sum + tip.amount, 0) // sets an initial sum of 0 and adds each tip amount
        },
        error => console.error('>>> Error loading tips: ', error)
      )
    }
  }

  // unsub
  ngOnDestroy(): void {
    if (this.dbSub) { this.dbSub.unsubscribe() }
    if (this.artisteSub) { this.artisteSub.unsubscribe() }
    if (this.querySub) { this.querySub.unsubscribe() }
    if (this.stripeSub) { this.stripeSub.unsubscribe() }
  }

}
