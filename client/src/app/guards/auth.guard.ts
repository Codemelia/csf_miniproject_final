import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { AuthStore } from "../stores/auth.store";

@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {

    // inject services
    private authStore = inject(AuthStore)
    private router = inject(Router)

    // can activate method
    // validates if user is logged in, if not navigates to login page
    canActivate(): boolean {
        if (this.authStore.isLoggedIn()) {
            return true
        } else {
            this.router.navigate(['/'])
            return false
        }
    }

}