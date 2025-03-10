import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, catchError, map, Observable, tap } from "rxjs";
import { ApiError, AuthResponse, AuthState, DecodedToken, UserLogin, UserRegistration } from "../models/app.models";

import { jwtDecode } from 'jwt-decode';
import { Router } from "@angular/router";

@Injectable({ providedIn: 'root' })
export class AuthService {

    // inject services
    private http = inject(HttpClient)
    private router = inject(Router)

    // set auth token key to avoid errors
    private tokenKey = 'auth_token'

    // instance of BehaviorSubject (Observable)
    // emits new values to all subscribers upon subscription and when updated
    // holds either string token or null (if no token) from local storage
    private tokenSub = new BehaviorSubject<string | null>(this.getToken())
    token$ = this.tokenSub.asObservable() // converts behaviorsubject into plain observable

    // auth state observable
    // emits full auth state object
    // maps values to DecodeToken object
    authState$ = this.token$.pipe(
        map(token => this.decodeToken(token))
    )

    // register user
    register(email: string, username: string, password: string, 
        phoneNumber: string, role: string): Observable<AuthResponse> {
        const user: UserRegistration = { email, username, password, phoneNumber, role }
        return this.http.post<AuthResponse>('/api/auth/register', user);
    }

    // login user
    login(email: string, password: string): Observable<AuthResponse> {
        const user: UserLogin = { email, password }
        return this.http.post<AuthResponse>('/api/auth/login', user)
            .pipe(tap(response => {
                console.log('Token upon login: ', response.token)
                this.setToken(response.token)}));
    }

    // log out user
    logout(): void {
        localStorage.removeItem(this.tokenKey) // remove token key from local storage
        this.tokenSub.next(null) // emits null value to all subscribers
        this.router.navigate(['/']) // redirect to login page
    }

    // get token for auth guard
    getToken(): string | null {
        console.log('>>> Retrieving token: ', localStorage.getItem(this.tokenKey))
        return localStorage.getItem(this.tokenKey)
    }

    // check if user logged in
    // used to authenticate user for routing
    isLoggedIn(): boolean {
        return !!this.getToken()
    }

    // get user role for role guard
    extractUserRoleFromToken(): string | null {
        return this.decodeToken(this.getToken()).role
    }

    // extract user id from token
    extractUIDFromToken(): number | null {
        return this.decodeToken(this.getToken()).uid
    }

    // helper funct to decode token
    private decodeToken(token: string | null): AuthState {
        // if no token, return false/null auth state
        if (!token) {
            return { loggedIn: false, role: null, uid: null } }

        try {
            // try decode token and return auth state
            const decoded: DecodedToken = jwtDecode(token)
            return {
                loggedIn: true, role: decoded.role || null, 
                uid: Number(decoded.sub) || null }
        } catch (error) {
            console.error('>>> Error decoding token: ', error)
            return { loggedIn: false, role: null, uid: null }
        }
    }

    // helper funct to set token
    private setToken(token: string): void {
        localStorage.setItem(this.tokenKey, token)
        this.tokenSub.next(token)
    }
    
    // set headers with token
    getHeaders(): HttpHeaders {
        const token = this.getToken()
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        })
    }

}