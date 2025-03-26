import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs';
import { ApiError, UserLogin } from '../../models/app.models';
import { AuthStore } from '../../stores/auth.store';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent implements OnInit, OnDestroy {

  // form
  private fb = inject(FormBuilder)
  protected form!: FormGroup

  // services
  private router = inject(Router)
  private authStore = inject(AuthStore)
  private title = inject(Title)
  
  // error obj
  protected error!: ApiError

  // subscription
  protected loginSub?: Subscription

  // success message
  successMsg: string | null = null

  ngOnInit(): void {

    this.title.setTitle('Login')

    this.createForm() // create form on init

    // subscribe to login result on init
    this.loginSub = this.authStore.loginResult$.subscribe(
      response => {
        if ('token' in response) {
          this.successMsg = 'Login successful! Welcome back.'
          console.log('>>> User login successful')
          this.router.navigate(['/home'])
        } else {
          this.error = response
          console.error('>>> User login failed')
        }
      }
    ) 
  }

  // create form
  createForm() {
    this.form = this.fb.group({
      email: this.fb.control('', 
        [ Validators.required, Validators.email ]),
      password: this.fb.control('', 
        [ Validators.required ])
    })
  }

  // log in - sends user details to auth store for validation
  login() {
    if (this.form.valid) {
      const user = this.form.value
      this.authStore.login(user)
    }
  }

  // unsub
  ngOnDestroy(): void {
      this.loginSub?.unsubscribe()
  }

}
