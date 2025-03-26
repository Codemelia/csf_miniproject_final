import { Component, inject, OnInit } from '@angular/core';
import { AuthStore } from '../../stores/auth.store';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-home',
  standalone: false,
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  private authStore = inject(AuthStore)
  private title = inject(Title)

  errorMsg: string | null = null
  userId: string | null = null
  userRole: string | null = null

  isVibee: boolean = false
  
  ngOnInit(): void {

    this.title.setTitle('Home')

    // retrieve user id and role from token
    this.userId = this.authStore.extractUIDFromToken()
    this.userRole = this.authStore.extractUserRoleFromToken()

    // if user role is vibee, set to true
    if (this.userRole === 'ARTISTE') this.isVibee = true

  }

}
