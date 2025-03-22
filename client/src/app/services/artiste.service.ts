import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, OnInit } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { AuthStore } from '../stores/auth.store';
import { Router } from '@angular/router';

@Injectable()
export class ArtisteService {

  // service/store
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)

  // ARTISTE AUTHENTICATION FUNCTIONS

  // create artiste object
  // returns feedback string
  createArtiste(artisteId: string, stageName: string, categories: string[], 
      bio: string | null, photoBlob: Blob | null, thankYouMessage: string | null): Observable<string> {
    if (artisteId) {
      const formData = new FormData();
      formData.append('stageName', stageName)
      if (categories && categories.length > 0) {
        formData.append('categories', categories.join(',')) }
      if (bio) { formData.append('bio', bio) }
      if (photoBlob) { formData.append('photo', photoBlob ) }
      if (thankYouMessage) { formData.append('thankYouMessage', thankYouMessage) }
  
      return this.http.post<string>(`/api/artiste/create/${artisteId}`, formData, {
        headers: this.authStore.getNoContentHeaders(),
        responseType: 'text' as 'json' })
    } else {
      return of('Could not retrieve Vibee ID for Vibee profile creation.');
    }
  }

  // check if artiste id exists in db
  // returns true if exist
  artisteExists(artisteId: string): Observable<boolean> {
    const params = new HttpParams().set('artisteId', artisteId)
    return this.http.get<boolean>('/api/artiste/check', { params,
      headers: this.authStore.getJsonHeaders()})
  }

  // ARTISTE STRIPE FUNCTIONS

  // gen oauth url
  genOAuthUrl(artisteId: string): Observable<string> {
    return this.http.get<string>(`/api/stripe/gen-oauth/${artisteId}`,
      { headers: this.authStore.getJsonHeaders(),
        responseType: 'text' as 'json' })
  }

  // check if artiste stripe access token is in mysql
  stripeAccessTokenExists(artisteId: string): Observable<boolean> {
    return this.http.get<boolean>(`api/stripe/check-access/${artisteId}`,
      { headers: this.authStore.getJsonHeaders() })
      .pipe(
        catchError(() => {
          console.log('>>> Error checking Stripe access token.');
          return of(false); // return false in case of an error
        })
      )
  }

  // ARTISTE WALLET FUNCTIONS
  requestPayout(walletBalance: number) {
    throw new Error('Method not implemented.');
  }
  getPayoutHistory() {
    throw new Error('Method not implemented.');
  }
  getPendingPayouts() {
    throw new Error('Method not implemented.');
  }
  getWalletBalance() {
    throw new Error('Method not implemented.');
  }

}