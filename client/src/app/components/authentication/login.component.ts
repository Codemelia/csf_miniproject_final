import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { Subscription } from 'rxjs';
import { ApiError } from '../../models/app.models';
import { AuthService } from '../../services/auth.service';

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

  // router
  private router = inject(Router)

  // auth service
  private authSvc = inject(AuthService)
  
  // error obj
  protected error!: ApiError

  // subscription
  protected loginSub!: Subscription

  ngOnInit(): void {
    this.createForm() // create form on init
  }

  // create form
  createForm() {
    this.form = this.fb.group({
      username: this.fb.control('', 
        [ Validators.required ]),
      password: this.fb.control('', 
        [ Validators.required ])
    })
  }

  // submit button
  login() {
    if (this.form.valid) {
      const { username, password } = this.form.value
      this.loginSub = this.authSvc.login(username, password).subscribe({
        next: () => {
          console.log('>>> User login successful')
          this.router.navigate(['dashboard'])
        },
        error: (error) => {
          this.error = error
          console.error('>>> User login failed: ', this.error)
        }
      })
    }
  }

  // unsub
  ngOnDestroy(): void {
      this.loginSub.unsubscribe()
  }

}
