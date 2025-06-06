import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthStore } from '../../stores/auth.store';
import { ApiError, UserRegistration } from '../../models/app.models';
import { Subscription } from 'rxjs';
import { Title } from '@angular/platform-browser';

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
  private authStore = inject(AuthStore)
  private title = inject(Title)
  
  // error obj
  protected error: ApiError | null = null

  // subscription
  protected regisSub?: Subscription

  // success message
  successMsg: string | null = null
  isLoading: boolean = false

  ngOnInit(): void {

    this.title.setTitle('Registration')

    this.createForm() // create form on init

    // subscribe to regis result on init
    this.regisSub = this.authStore.registrationResult$.subscribe(response => {
      if ('userId' in response) {
        this.successMsg = response.message // prompts user to login after successful registration
        this.error = null
        this.form.reset()
        this.isLoading = false
        setTimeout(() => this.router.navigate(['/']), 2000)
      } else {
        this.error = response
        this.successMsg = null
        this.isLoading = false
      }
    })
  }


  createForm() {
    this.form = this.fb.group({
      email: this.fb.control('',
        [ Validators.required, Validators.email, Validators.maxLength(255) ]),
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

  // register method - sends user regis details to auth store for saving
  register() {
    if (this.form.valid) {
      this.isLoading = true
      const user: UserRegistration = this.form.value;
      this.authStore.register(user);
    }
  }

  // unsub
  ngOnDestroy(): void {
    this.regisSub?.unsubscribe()
  }

}
