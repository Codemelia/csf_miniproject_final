import { inject, Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { Tip, TipResponse } from '../models/app.models';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthStore } from '../stores/auth.store';

// inserted here to avoid confusion; only used in service
export interface TipState {
  amount: number;
  artisteId: string | null;
}

@Injectable({ providedIn: 'root' })
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)
  amount: number = 0
  artisteId: string | null = null

  // send tip as tip request obj to server
  // retrieves client secret from server
  getPaymentIntentClientSecret(unconfirmRequest: Tip): Observable<TipResponse> {
    return this.http.post<TipResponse>('/api/tips/process', unconfirmRequest, { 
      headers: this.authStore.getJsonHeaders() })
  }

  // save tip after confirmed payment
  saveTip(confirmRequest: Tip): Observable<string> {
    return this.http.put<string>('api/tips/save', confirmRequest, { 
      headers: this.authStore.getJsonHeaders(),
      responseType: 'text' as 'json' })
  }

}