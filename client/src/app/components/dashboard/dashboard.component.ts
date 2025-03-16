import { AfterViewInit, ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { DashboardService } from '../../services/dashboard.service';
import { ApiError, Tip } from '../../models/app.models';
import { catchError, Subscription, tap } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { ArtisteService } from '../../services/artiste.service';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit, OnDestroy, AfterViewInit {

  // services
  private authSvc = inject(AuthService)
  protected artisteSvc = inject(ArtisteService)
  private route = inject(ActivatedRoute)
  private cdr = inject(ChangeDetectorRef)
  private router = inject(Router)

  // subscriptions
  private artisteSub!: Subscription
  private querySub!: Subscription
  private stripeSub!: Subscription

  // data and states
  protected artisteId: string | null = null
  protected artisteExists: boolean = false
  protected isStripeLinked: boolean = false
  isLoading: boolean = true // for API loading state
  error!: ApiError
  isMenuCollapsed = false

  // toggle menu visibility
  toggleMenu() {
    this.isMenuCollapsed = !this.isMenuCollapsed
  }

  ngOnInit(): void {
    this.artisteId = this.authSvc.extractUIDFromToken()
    
    // check if artiste ID is available and linked with Stripe
    if (this.artisteId) {
      this.stripeSub = this.artisteSvc.stripeAccessTokenExists(this.artisteId).subscribe({
        next: (resp) => {
          this.isStripeLinked = resp
          console.log('>>> Stripe linked: ', this.isStripeLinked)
        },
        error: () => {
          this.isStripeLinked = false
          console.log('>>> Stripe not linked')
        }
      });
    }

    // handling query params for Stripe completion state
    this.querySub = this.route.queryParams.subscribe((params) => {
      const fromStripe = params['stripeComplete']
      if (fromStripe === 'true') {
        console.log('>>> Stripe has been linked')
      }
    });

    // checking if artiste exists
    if (this.artisteId != null) {
      this.isLoading = true;
      this.artisteSub = this.artisteSvc.artisteExists(this.artisteId).pipe(
        tap({
          next: (resp) => {
            this.artisteExists = resp
            console.log('>>> Artiste exists: ', this.artisteExists)
            this.isLoading = false
          },
          error: (err) => {
            this.error = err
            this.isLoading = false
          }
        }),
        catchError((err) => {
          this.error = err
          this.isLoading = false
          return []
        })
      ).subscribe()
    } else {
      this.isLoading = false
    }

  }

  // trigger change detection after view init
  ngAfterViewInit(): void {
    this.cdr.detectChanges()
  }

  // unsub
  ngOnDestroy(): void {
    if (this.artisteSub) this.artisteSub.unsubscribe()
    if (this.querySub) this.querySub.unsubscribe()
    if (this.stripeSub) this.stripeSub.unsubscribe()
  }

}