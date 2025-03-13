import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable, OnInit } from '@angular/core';
import { catchError, Observable, of } from 'rxjs';
import { AuthService } from './auth.service';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ArtisteService {

  private http = inject(HttpClient)
  private authSvc = inject(AuthService)
  private router = inject(Router)

  // create artiste object
  // returns feedback string
  createArtiste(artisteId: string, stageName: string, bio: string | null, photoBlob: Blob | null): Observable<string> {
    if (artisteId) {
      const formData = new FormData();
      formData.append('stageName', stageName)
      if (bio) { formData.append('bio', bio) }
      if (photoBlob) { formData.append('photo', photoBlob ) }
  
      return this.http.post<string>(`/api/artiste/create/${artisteId}`, formData, {
        headers: this.authSvc.getNoContentHeaders(),
        responseType: 'text' as 'json' })
    } else {
      return of('Could not retrieve Vibee ID for Vibee profile creation.');
    }
  }

  // check if artiste id exists in db
  // returns true if exist
  artisteExists(artisteId: string): Observable<boolean> {
    const params = new HttpParams().set('artisteId', artisteId)
    return this.http.get<boolean>('/api/artiste-check', { params,
      headers: this.authSvc.getJsonHeaders()})
  }

  // gen oauth url
  genOAuthUrl(artisteId: string): Observable<string> {
    return this.http.get<string>(`/api/stripe/oauth-gen/${artisteId}`,
      { headers: this.authSvc.getJsonHeaders(),
        responseType: 'text' as 'json' })
  }

  // check if artiste stripe access token is in redis
  stripeAccessTokenExists(artisteId: string): Observable<boolean> {
    return this.http.get<boolean>(`api/stripe/check-access/${artisteId}`,
      { headers: this.authSvc.getJsonHeaders() })
      .pipe(
        catchError(() => {
          console.log('>>> Error checking Stripe access token.');
          return of(false); // return false in case of an error
        })
      )
  }

}