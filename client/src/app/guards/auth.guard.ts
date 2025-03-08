import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    // inject services
    private authSvc = inject(AuthService)
    private router = inject(Router)

    // can activate method
    // validates if user is logged in, if not navigates to login page
    canActivate(): boolean {
        if (this.authSvc.isLoggedIn()) {
            return true
        } else {
            this.router.navigate(['/login'])
            return false
        }
    }

}