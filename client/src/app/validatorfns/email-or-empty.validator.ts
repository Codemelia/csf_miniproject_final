import { AbstractControl, ValidationErrors, ValidatorFn, Validators } from "@angular/forms";

export function emailOrEmptyValidator(): ValidatorFn {
    return (ctrl: AbstractControl): ValidationErrors | null => {
        if (!ctrl.value) return null // no validation applied without input
        const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/
        return emailRegex.test(ctrl.value) ? null : { emailOrEmpty: true } // return error if invalid
    }
}