import { AfterViewInit, ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ApiError } from '../../models/app.models';
import { catchError, Subscription, tap } from 'rxjs';
import { AuthStore } from '../../stores/auth.store';
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
  private authStore = inject(AuthStore)
  protected artisteSvc = inject(ArtisteService)
  private route = inject(ActivatedRoute)
  private cdr = inject(ChangeDetectorRef)
  private router = inject(Router)

  // subscriptions
  private artisteSub!: Subscription
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
    this.artisteId = this.authStore.extractUIDFromToken()
    console.log('>>> Artiste ID: ', this.artisteId)
    
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
    this.artisteSub?.unsubscribe()
    this.stripeSub?.unsubscribe()
  }

}