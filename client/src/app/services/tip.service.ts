import { Injectable } from '@angular/core';
import { ComponentStore } from '@ngrx/component-store';
import { TipState } from '../models/app.models';

@Injectable({
  providedIn: 'root'
})
export class TipService extends ComponentStore<TipState> {

  // tip state variables
  constructor() {
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

}