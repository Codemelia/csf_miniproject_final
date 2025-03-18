import { Component, inject } from '@angular/core';
import { AuthStore } from './stores/auth.store';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.scss'
})
export class AppComponent {

  title = 'client'
 
  // inject service
  protected authStore = inject(AuthStore)
  
}
