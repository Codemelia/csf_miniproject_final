import { Component, inject } from '@angular/core';
import { AuthService } from './services/auth.service';
import { ArtisteService } from './services/artiste.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  standalone: false,
  styleUrl: './app.component.scss'
})
export class AppComponent {

  title = 'client'
 
  // inject service
  protected authSvc = inject(AuthService)
  
}
