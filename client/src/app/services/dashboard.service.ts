import { Injectable } from '@angular/core';

import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tip } from '../models/app.models';

@Injectable({
  providedIn: 'root'
})
export class DashboardService {

  constructor(private http: HttpClient) { }

  // get tips from mysql
  getTips(musicianId: string): Observable<Tip[]> {

    return this.http.get<Tip[]>(`/api/tips/${musicianId}`)

  }

}
