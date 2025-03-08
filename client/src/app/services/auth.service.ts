import { HttpClient } from "@angular/common/http";
import { inject, Injectable } from "@angular/core";
import { catchError, Observable, tap } from "rxjs";
import { ApiError, AuthResponse, UserLogin, UserRegistration } from "../models/app.models";

@Injectable({ providedIn: 'root' })
export class AuthService {

    // inject httpclient
    private http = inject(HttpClient)

    // auth request to build
    protected userLogin!: UserLogin
    protected userRegis!: UserRegistration

    // set auth token key to avoid errors
    private tokenKey = 'auth_token'

    // error
    protected error!: ApiError

    // register user
    register(username: string, password: string, role: string): Observable<AuthResponse> {
        this.userRegis = { username, password, role }
        return this.http.post<AuthResponse>('/api/auth/register', this.userRegis);
    }

    // login user
    login(username: string, password: string): Observable<AuthResponse> {
        this.userLogin = { username, password }
        return this.http.post<AuthResponse>('/api/auth/login', this.userLogin)
            .pipe(tap(response => localStorage.setItem(this.tokenKey, response.token)));
    }

    // get token
    getToken(): string | null {
        return localStorage.getItem(this.tokenKey)
    }
    
    // check if user logged in
    isLoggedIn(): boolean {
        return !!this.getToken()
    }

    // log out user
    logOut(): void {
        localStorage.removeItem(this.tokenKey)
    }

}