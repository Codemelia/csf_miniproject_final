import { inject, Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { Tip } from '../models/app.models';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';

// inserted here to avoid confusion; only used in service
export interface TipState {
  amount: number;
  artisteId: string | null;
}

@Injectable({ providedIn: 'root' })
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  private http = inject(HttpClient)
  private authSvc = inject(AuthService)
  amount: number = 0
  artisteId: string | null = null

  // send tip as tip request obj to server
  // retrieves client secret from server
  getPaymentIntentClientSecret(unconfirmRequest: Tip): Observable<string> {
    return this.http.post<string>('/api/tips/process', unconfirmRequest, { 
      headers: this.authSvc.getJsonHeaders(),
      responseType: 'text' as 'json' })
  }

  // save tip after confirmed payment
  saveTip(confirmRequest: Tip): Observable<number> {
    return this.http.post<number>('api/tips/save', confirmRequest, { 
      headers: this.authSvc.getJsonHeaders() })
  }

}