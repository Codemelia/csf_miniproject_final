import { inject, Injectable } from "@angular/core";
import { ActivatedRouteSnapshot, CanActivate, GuardResult, MaybeAsync, Router, RouterStateSnapshot } from "@angular/router";
import { AuthService } from "../services/auth.service";

@Injectable({ providedIn: 'root' })
export class RoleGuard implements CanActivate {

    // inject services
    private authSvc = inject(AuthService)
    private router = inject(Router)

    canActivate(route: ActivatedRouteSnapshot): boolean {
        
        const expectedRole = route.data['expectedRole']
        const userRole = this.authSvc.getUserRole()

        if (this.authSvc.isLoggedIn() && userRole === expectedRole) {
            return true
        }

        this.router.navigate(['/home']) // redirect to home page if logged in
        return false

    }

}