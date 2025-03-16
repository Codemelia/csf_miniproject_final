import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { ApiError, UserRegistration } from '../../models/app.models';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent implements OnInit, OnDestroy {

  // form
  private fb = inject(FormBuilder)
  protected form!: FormGroup

  // services
  private router = inject(Router)
  private authSvc = inject(AuthService)
  
  // error obj
  protected error!: ApiError

  // subscription
  protected regisSub?: Subscription

  // success message
  successMsg: string | null = null

  ngOnInit(): void {
    this.createForm() // create form on init
  }


  createForm() {
    this.form = this.fb.group({
      email: this.fb.control('',
        [ Validators.required, Validators.email, Validators.maxLength(255) ]),
      username: this.fb.control('', 
        [ Validators.required, Validators.minLength(8), Validators.maxLength(30) ]),
      password: this.fb.control('', 
        [ Validators.required, Validators.minLength(12), Validators.maxLength(42),
          // password must contain at least 1 uppercase letter, 1 lowercase letter, 1 digit, 1 special char 12-42 length
          Validators.pattern(/^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=\[\]{};:'",.<>/?])[A-Za-z\d!@#$%^&*()_+\-=\[\]{};:'",.<>/?]{12,42}$/) ]),
      phoneNumber: this.fb.control('', 
        [ Validators.required,
          Validators.pattern(/^(?:\+65\s?)?[689]\d{3}\s?\d{4}$/) ]),
      role: this.fb.control('',
        [ Validators.required ])
    })
  }

  // register on submit
  register() {
    if (this.form.valid) {
      const { email, username, password, phoneNumber, role } = this.form.value
      this.regisSub = this.authSvc.register(email, username, password, phoneNumber, role).subscribe({
        next: (response) => {
          this.successMsg = 'Registration successful! Please log in.'
          console.log('>>> Registration successful', response)
          setTimeout(() => this.router.navigate(['/']), 2000) // redirect to login page aft 2 secs
        },
        error: (err) => {
          this.error = err.error
          console.error('>>> Registration error', this.error)
        }
      })
    }
  }

  // unsub
  ngOnDestroy(): void {
      this.regisSub?.unsubscribe()
  }

}
