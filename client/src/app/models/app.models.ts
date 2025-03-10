export interface TipRequest {
    tipperId: number
    musicianId: number
    amount: number
}

export interface TipResponse {
    clientSecret: string
    tip: TipResponse
}

export interface Tip {
    id: number
    tipperId: number
    musicianId: number
    amount: number
    paymentIntentId: string
    paymentStatus: string
    createdAt: Date
    updatedAt:Date
}

export interface AuthResponse {
    id: number
    token: string
    message: string
}

export interface UserRegistration {
    email: string
    username: string
    password: string
    phoneNumber: string
    role: string 

}

export interface UserLogin {
    email: string
    password: string
}

export interface AuthState {
    loggedIn: boolean;
    role: string | null;
    uid: number | null;
}

export interface DecodedToken {
    role: string
    sub: string
    [key: string]: any
}

export interface ApiError {
    timestamp: Date
    status: number
    error: string
    message: string
}