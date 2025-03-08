import { inject, Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tip } from '../models/app.models';
import { AuthService } from './auth.service';

@Injectable({ providedIn: 'root' })
export class DashboardService {

  // inject services
  private http = inject(HttpClient)
  private authSvc = inject(AuthService)

  // set headers with token
  private getHeaders(): HttpHeaders {
    const token = this.authSvc.getToken()
    return new HttpHeaders().set('Authorization', `Bearer ${token}`)
  }

  // send jwt token and get tips from mysql
  getTips(musicianId: string): Observable<Tip[]> {
    return this.http.get<Tip[]>(`/api/tips/${musicianId}`, { headers: this.getHeaders() })
  }

}
