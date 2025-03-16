import { Component, inject, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  private authSvc = inject(AuthService)
  private router = inject(Router)

  errorMsg: string | null = null
  userId: string | null = null
  userRole: string | null = null

  isVibee: boolean = false
  
  ngOnInit(): void {
      
    if (!this.authSvc.isLoggedIn()) {
      this.errorMsg = 'Unauthorized user. Please log in.'
      this.authSvc.logout()
    }

    // retrieve user id and role from token
    this.userId = this.authSvc.extractUIDFromToken()
    this.userRole = this.authSvc.extractUserRoleFromToken()

    // if user role is vibee, set to true
    if (this.userRole === 'ARTISTE') this.isVibee = true

  }

}
