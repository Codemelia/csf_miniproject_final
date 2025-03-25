import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, OnInit } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { AuthStore } from '../stores/auth.store';
import { Router } from '@angular/router';
import { ArtisteProfile } from '../models/app.models';

@Injectable()
export class ArtisteService {

  // service/store
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)

  // ARTISTE AUTHENTICATION FUNCTIONS

  // create artiste profile
  // returns feedback string
  createArtisteProfile(profile: ArtisteProfile): Observable<string> {
    const formData = new FormData();
    formData.append('stageName', profile.stageName)
    if (profile.categories && profile.categories.length > 0) {
      formData.append('categories', profile.categories.join(',')) }
    if (profile.bio) { formData.append('bio', profile.bio) }
    if (profile.photo) { formData.append('photo', profile.photo ) }
    if (profile.thankYouMessage) { formData.append('thankYouMessage', profile.thankYouMessage) }

    return this.http.post<string>(`/api/artiste/create-profile/${profile.artisteId}`, formData, {
      headers: this.authStore.getNoContentHeaders(),
      responseType: 'text' as 'json' })
  }

  // check if artiste id exists in db
  // returns true if exist
  artisteExists(artisteId: string): Observable<boolean> {
    const params = new HttpParams().set('artisteId', artisteId)
    return this.http.get<boolean>('/api/artiste/check', 
      { params, headers: this.authStore.getJsonHeaders()})
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
    return this.http.get<boolean>(`/api/stripe/check-access/${artisteId}`,
      { headers: this.authStore.getJsonHeaders() })
      .pipe(
        catchError(() => {
          console.log('>>> Error checking Stripe access token.');
          return of(false); // return false in case of an error
        })
      )
  }

  // get artiste profile
  getArtisteProfile(artisteId: string): Observable<ArtisteProfile> {
    return this.http.get<ArtisteProfile>(`/api/artiste/profile/${artisteId}`,
    { headers: this.authStore.getJsonHeaders() })
  }

  // update artiste profile
  updateArtisteProfile(profile: ArtisteProfile, artisteId: string): Observable<boolean> {
    const formData = new FormData();
    formData.append('stageName', profile.stageName)
    if (profile.categories && profile.categories.length > 0) {
      formData.append('categories', profile.categories.join(',')) }
    if (profile.bio) { formData.append('bio', profile.bio) }
    if (profile.photo) { formData.append('photo', profile.photo ) }
    if (profile.thankYouMessage) { formData.append('thankYouMessage', profile.thankYouMessage) }

    return this.http.put<boolean>(`/api/artiste/update-profile/${artisteId}`,
      formData, { headers: this.authStore.getNoContentHeaders() })
      .pipe(
        catchError(() => {
          console.log('>>> Error updating profile');
          return of(false); // return false in case of an error
        })
      )
  }

  // get all artiste profiles
  getAllArtisteProfiles(artisteId: string): Observable<ArtisteProfile[]> {
    return this.http.get<ArtisteProfile[]>(`/api/artistes`,
      { headers: this.authStore.getJsonHeaders() })
  }

  // get prof photo mime type
  // get file type
  getImageMimeType(base64: string): string {
    if (base64.startsWith("/9j/")) return "image/jpeg"; // JPEG
    if (base64.startsWith("iVBORw0KGgo")) return "image/png"; // PNG
    if (base64.startsWith("R0lGODdh") || base64.startsWith("R0lGODlh")) return "image/gif"; // GIF
    if (base64.startsWith("UklGR")) return "image/webp"; // WebP
    return "image/png"; // fallback
  }

}