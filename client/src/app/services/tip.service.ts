import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { TipState } from '../models/app.models';

@Injectable({
  providedIn: 'root'
})
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  constructor() {
    super({ amount: 0, buskerId: null });
  }

  // selects variables from state as readonly
  readonly amount$ = this.select(state => state.amount)
  readonly buskerId$ = this.select(state => state.buskerId)

  // set tip as readonly variable
  readonly setTip = this.updater((state, tip: 
    { amount: number, buskerId: string }) => ({
      ...state,
      amount: tip.amount,
      buskerId: tip.buskerId
  }))

}