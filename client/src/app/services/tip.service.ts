import { inject, Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { Tip, TipRequest, TipResponse } from '../models/app.models';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';
import { PaymentIntent } from '@stripe/stripe-js';

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
  // retrieves tip as tip object from server
  processTip(request: TipRequest): Observable<TipResponse> {
    return this.http.post<TipResponse>('/api/tips/process', request, { headers: this.authSvc.getJsonHeaders() })
  }

  // confirm payment
  confirmTip(paymentIntent: PaymentIntent): Observable<string> {
    return this.http.put<string>(`api/tips/confirm/${paymentIntent.id}`, paymentIntent, { headers: this.authSvc.getJsonHeaders() })
  }

}