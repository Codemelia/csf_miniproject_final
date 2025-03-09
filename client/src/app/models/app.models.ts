export interface TipRequest {
    tipperId: number
    musicianId: number
    amount: number
    stripeToken: string
}

export interface Tip {
    id: number,
    tipperId: number
    musicianId: number,
    amount: number,
    stripeChargeId: string
    createdAt: Date
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