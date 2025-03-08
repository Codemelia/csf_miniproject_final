import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { Tip, TipRequest } from '../models/app.models';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';

// inserted here to avoid confusion; only used in service
export interface TipState {
  amount: number;
  musicianId: string | null;
}

@Injectable({ providedIn: 'root' })
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  constructor(private http: HttpClient) {
    super({ amount: 0, musicianId: null });
  }

  // selects variables from state as readonly
  readonly amount$ = this.select(state => state.amount)
  readonly musicianId$ = this.select(state => state.musicianId)

  // set tip as readonly variable
  readonly setTip = this.updater((state, tip: 
    { amount: number, musicianId: string }) => ({
      ...state,
      amount: tip.amount,
      musicianId: tip.musicianId
  }))

  // send tip as tip request obj to server
  // retrieves tip as tip object from server
  insertTip(request: TipRequest): Observable<Tip> {
    return this.http.post<Tip>('/api/tips/insert', { request });
  }

}