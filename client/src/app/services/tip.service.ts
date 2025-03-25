import { inject, Injectable } from '@angular/core';
import { Tip, TipResponse } from '../models/app.models';
import { Observable, tap, map, catchError, throwError, of, BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { AuthStore } from '../stores/auth.store';

@Injectable()
export class TipService {

  // tip state variables
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)

  private artisteId: string | null = null

  // send tip as tip request obj to server
  // retrieves client secret from server
  getPaymentIntentClientSecret(unconfirmRequest: Tip): Observable<TipResponse> {
    return this.http.post<TipResponse>('/api/tips/process', unconfirmRequest)
  }

  // save tip after confirmed payment
  saveTip(confirmRequest: Tip): Observable<string> {
    return this.http.post<string>('api/tips/save', confirmRequest, 
      { responseType: 'text' as 'json' })
  }

  // get tips from mysql
  getTips(): Observable<Tip[]> {
    this.artisteId = this.authStore.extractUIDFromToken()
    return this.http.get<Tip[]>(`/api/tips/get/${this.artisteId}`, {
        headers: this.authStore.getJsonHeaders()
    }).pipe(
        tap(tips => { return tips }),
        catchError(error => {
            console.error('Error fetching tips:', error);
            return of([])
        })
    )
  }

}