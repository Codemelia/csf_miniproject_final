import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, Observable, tap } from "rxjs";
import { ApiError, AuthResponse, UserLogin, UserRegistration } from "../models/app.models";

import { jwtDecode } from 'jwt-decode';
import { Router } from "@angular/router";

@Injectable({ providedIn: 'root' })
export class AuthService {

    // inject services
    private http = inject(HttpClient)
    private router = inject(Router)

    // auth request to build
    protected userLogin!: UserLogin
    protected userRegis!: UserRegistration

    // set auth token key to avoid errors
    private tokenKey = 'auth_token'

    // error
    protected error!: ApiError

    // register user
    register(email: string, username: string, password: string, phoneNumber: string, role: string): Observable<AuthResponse> {
        this.userRegis = { email, username, password, phoneNumber, role }
        return this.http.post<AuthResponse>('/api/auth/register', this.userRegis);
    }

    // login user
    login(email: string, password: string): Observable<AuthResponse> {
        this.userLogin = { email, password }
        return this.http.post<AuthResponse>('/api/auth/login', this.userLogin)
            .pipe(tap(response => localStorage.setItem(this.tokenKey, response.token)));
    }

    // get token for auth guard
    getToken(): string | null {
        return localStorage.getItem(this.tokenKey)
    }

    // get user role for role guard
    getUserRole(): string | null {

        const token = this.getToken() // get token for validation
        if (token) {
            try {
                const decoded: any = jwtDecode(token) // decode token
                console.log('>>> Decoded token: ', decoded as string)
                return decoded.role || null
            } catch (error) {
                console.error('>>> Error decoding token: ', error)
                return null
            }
        }
        return null

    }

    // extract user id from token
    extractUIDFromToken(): number | null {

        const token = this.getToken()
        if (token) {
            try {
                const decoded: any = jwtDecode(token) // decode token
                console.log('>>> Decoded token: ', decoded as string)
                return Number(decoded.sub) || null
            } catch (error) {
                console.error('>>> Error decoding token: ', error)
                return null
            }
        }
        return null

    }
    
    // check if user logged in
    // used to authenticate user for routing
    isLoggedIn(): boolean {
        return !!this.getToken()
    }

    // log out user
    logout(): void {
        localStorage.removeItem(this.tokenKey) // remove token key from local storage
        this.router.navigate(['/']) // redirect to login page
    }

}