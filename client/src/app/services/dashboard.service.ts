import { inject, Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tip } from '../models/app.models';
import { AuthStore } from '../stores/auth.store';

@Injectable({ providedIn: 'root' })
export class DashboardService {

  // inject services
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)

  // send jwt token and get tips from mysql
  getTips(artisteId: string): Observable<Tip[]> {
    return this.http.get<Tip[]>(`/api/tips/${artisteId}`, { headers: this.authStore.getJsonHeaders() })
  }

  

}
