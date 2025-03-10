import { inject, Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { Tip, TipRequest, TipResponse } from '../models/app.models';
import { Observable } from 'rxjs';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from './auth.service';

// inserted here to avoid confusion; only used in service
export interface TipState {
  amount: number;
  musicianId: string | null;
}

@Injectable({ providedIn: 'root' })
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  private http = inject(HttpClient)
  private authSvc = inject(AuthService)
  amount: number = 0
  musicianId: number | null = null

  // send tip as tip request obj to server
  // retrieves tip as tip object from server
  processTip(request: TipRequest): Observable<TipResponse> {
    return this.http.post<TipResponse>('/api/tips/process', request, { headers: this.authSvc.getHeaders() })
  }

  // confirm payment
  confirmTip(paymentIntentId: string, paymentStatus: string): Observable<string> {
    return this.http.put<string>(`api/tips/confirm/${paymentIntentId}`, paymentStatus, { headers: this.authSvc.getHeaders() })
  }

}