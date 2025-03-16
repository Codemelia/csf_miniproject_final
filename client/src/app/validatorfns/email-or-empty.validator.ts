import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from "@angular/forms";

export function emailOrEmptyValidator(): ValidatorFn {
    return (ctrl: AbstractControl): ValidationErrors | null => {
        if (!ctrl.value) return null // no validation applied without input
        return Validators.email(ctrl) ? null : { emailOrEmpty: true } // return error if invalid
    }
}