// IDs are taken as strings to avoid going over number limit and ensure accuracy

export interface TipRequest {
    musicianId: number
    amount: number
    stripeToken: string
}

export interface Tip {
    id: number,
    musicianId: number,
    amount: number,
    stripeChargeId: string
}

export interface AuthResponse {
    id: number
    token: string
    message: string
}

export interface UserRegistration {
    username: string
    password: string
    role: string | null
}

export interface UserLogin {
    username: string
    password: string
}

export interface ApiError {
    timestamp: Date
    status: number
    error: string
    message: string
}