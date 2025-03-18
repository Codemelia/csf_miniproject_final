import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { AuthStore } from "../stores/auth.store";

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

    // inject services
    private authStore = inject(AuthStore)
    private router = inject(Router)

    canActivate(route: ActivatedRouteSnapshot): boolean {
        
        const expectedRole = route.data['expectedRole']
        const userRole = this.authStore.extractUserRoleFromToken()

        if (this.authStore.isLoggedIn() && userRole === expectedRole) {
            return true
        }

        this.router.navigate(['/home']) // redirect to home page if logged in
        return false

    }

}