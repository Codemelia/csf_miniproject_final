import { Component, inject, OnInit } from '@angular/core';
import { AuthStore } from './stores/auth.store';
import { map } from 'rxjs';
import { AuthState } from './models/app.models';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit {

  title = 'client'
 
  // inject service
  protected authStore = inject(AuthStore)

  ngOnInit(): void {
    // Subscribe to authState$ and log the state when it changes
    this.authStore.authState$.pipe(
      map(state => {
        return {
          token: state.token,
          loggedIn: state.loggedIn,
          userRole: state.userRole,
          userId: state.userId
        };
      })
    ).subscribe(authState => {
      // Log the actual value inside the subscription
      console.log('>>> Auth state: ', authState);
    });
  }
  
}
