import { inject, Injectable } from '@angular/core';

import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tip } from '../models/app.models';
import { AuthStore } from '../stores/auth.store';

@Injectable()
export class DashboardService {

  // inject services
  private http = inject(HttpClient)
  private authStore = inject(AuthStore)

  

  

}
