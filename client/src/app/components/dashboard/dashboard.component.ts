import { AfterViewInit, ChangeDetectorRef, Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ApiError } from '../../models/app.models';
import { catchError, Subscription, tap, throwError } from 'rxjs';
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
  private cdr = inject(ChangeDetectorRef)

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

  ngOnInit(): void {

    // authentication
    this.artisteId = this.authStore.extractUIDFromToken()
    console.log('>>> Artiste ID: ', this.artisteId)
    this.checkIfArtisteExists()
    this.checkArtisteStripe()

  }
  
  // check artiste stripe
  checkArtisteStripe() {
    this.stripeSub = this.artisteSvc.stripeAccessTokenExists(this.artisteId!).subscribe({
      next: (resp) => {
        this.isStripeLinked = resp
        console.log('>>> Stripe linked: ', this.isStripeLinked)
      },
      error: () => {
        this.isStripeLinked = false
        console.log('>>> Stripe not linked')
      }
    })
  }

  // checking if artiste exists
  checkIfArtisteExists(): void {
    if (this.artisteId != null) {
      this.isLoading = true;
      this.artisteSub = this.artisteSvc.artisteExists(this.artisteId).pipe(
        tap({
          next: (resp) => {
            this.artisteExists = resp
            console.log('>>> Artiste exists: ', this.artisteExists)
            localStorage.setItem('artisteExists', JSON.stringify(this.artisteExists)) // set status to local storage
            this.isLoading = false
          }
        }),
        catchError((err) => {
          this.error = err.error
          this.artisteExists = false
          this.isLoading = false
          return throwError(() => this.error)
        })
      ).subscribe()
    } else {
      this.isLoading = false
    }
  }

  // toggle menu visibility
  toggleMenu() {
    this.isMenuCollapsed = !this.isMenuCollapsed
  }
  
  get isContentAvailable(): boolean {
    return !this.isLoading && this.isStripeLinked && this.artisteExists;
  }

  get isMenuOpen(): boolean {
    return !this.isLoading && this.isStripeLinked && this.artisteExists && !this.isMenuCollapsed;
  }

  // logout
  logout() {
    this.authStore.logout()
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