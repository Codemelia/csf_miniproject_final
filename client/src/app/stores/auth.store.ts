import { HttpClient, HttpHeaders } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { BehaviorSubject, catchError, EMPTY, map, Observable, of, Subject, switchMap, tap, throwError } from "rxjs";
import { ApiError, AuthResponse, AuthState, DecodedToken, UserLogin, UserRegistration } from "../models/app.models";

import { jwtDecode } from 'jwt-decode';
import { Router } from "@angular/router";
import { ComponentStore } from "@ngrx/component-store";

@Injectable()
export class AuthStore extends ComponentStore<AuthState> {

    // inject services
    private http = inject(HttpClient)
    private router = inject(Router)

    // set auth token key to avoid errors
    private tokenKey = 'auth_token'

    // subjects for login and registration results
    private loginResultSubject = new Subject<AuthResponse | ApiError>()
    loginResult$: Observable<AuthResponse | ApiError> = this.loginResultSubject.asObservable()

    private registrationResultSubject = new Subject<AuthResponse | ApiError>()
    registrationResult$: Observable<AuthResponse | ApiError> = this.registrationResultSubject.asObservable()

    constructor() {
        super({ token: null, loggedIn: false, userId: null, userRole: null })
        this.initializeState()
    }

    // initialise state
    private initializeState(): void {
        const token = localStorage.getItem(this.tokenKey)
        if (token) {
            const decoded = this.decodeToken(token)
            this.setState({ token, loggedIn: true, userId: decoded.userId, userRole: decoded.userRole })
        }
    }

    // SELECTORS

    // expose authstate as observable for app.component.html
    // configures home navbar button routing
    readonly authState$ = this.select(state => state)

    // EFFECTS

    // login method
    // takes in email and password > posts to backend to receive auth response
    // auth response structure: token, message
    // set state with new token, and update logged in + role/id from decoded token
    // either emit auth response or error
    readonly login = this.effect<UserLogin>(credentials$ =>
        credentials$.pipe(
        switchMap(credentials =>
            this.http.post<AuthResponse>('/api/auth/login', credentials).pipe(
            tap(response => {
                localStorage.setItem(this.tokenKey, response.token!)
                const decoded = this.decodeToken(response.token!)
                this.setState({
                    token: response.token!,
                    loggedIn: true,
                    userId: decoded.userId,
                    userRole: decoded.userRole
                });
                this.loginResultSubject.next(response)
            }),
            catchError(error => {
                this.loginResultSubject.next(error.error)
                return EMPTY
            })
            )
        ))
    )

    // register method
    // takes in user regis object > posts to backend to receive auth response
    // auth response structure: userId, message
    // for registration, only emit response/error and let component define messages
    readonly register = this.effect<UserRegistration>(user$ =>
        user$.pipe(
        switchMap(user =>
            this.http.post<AuthResponse>('/api/auth/register', user).pipe(
            tap(response => {
                this.registrationResultSubject.next(response)
            }),
            catchError(error => {
                this.registrationResultSubject.next(error.error)
                return EMPTY
            })
            )
        )
        )
    )

    // UPDATERS
    
    // logout method
    // remove token from local storage
    // return values as initialised state
    readonly logout = this.updater(() => {
        localStorage.clear()
        this.router.navigate(['/'])
        return { token: null, loggedIn: false, userId: null, userRole: null }
    })

    // HELPERS

    // decode token
    // decoded token structure: role, user ID
    // uses jwt decode to decode, returning decoded user id and role
    // on error, set values to null/false
    private decodeToken(token: string | null): AuthState {
        // if no token, return false/null auth state
        if (!token) {
            return { token: null, loggedIn: false, userRole: null, userId: null } }

        try {
            // try decode token and return auth state
            const decoded: DecodedToken = jwtDecode(token)
            return {
                token: token,
                loggedIn: true, 
                userRole: decoded.role, 
                userId: decoded.sub }
        } catch (error) {
            return { 
                token: null,
                loggedIn: false, 
                userRole: null, 
                userId: null }
        }
    }

    // for http requests with content type set to app/json
    getJsonHeaders(): HttpHeaders {
        const token = this.state().token
        return new HttpHeaders({
          'Authorization': `Bearer ${token}`,
          'Content-Type': 'application/json'
        })
    }

    // for multipart http request
    getNoContentHeaders(): HttpHeaders {
        const token = this.state().token
        return new HttpHeaders({
            'Authorization': `Bearer ${token}`
        })
    }

    // methods to get values from state directly for one-time usage
    isLoggedIn(): boolean { return !!this.state().token }
    extractUserRoleFromToken(): string | null { return this.state().userRole }
    extractUIDFromToken(): string | null { return this.state().userId }

}