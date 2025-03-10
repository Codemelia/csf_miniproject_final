export interface TipRequest {
    tipperId: number
    musicianId: number
    amount: number
    stripeToken: string
}

export interface Tip {
    id: number
    tipperId: number
    musicianId: number
    amount: number
    stripeChargeId: string
    transactionStatus: string
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

export interface ApiError {
    timestamp: Date
    status: number
    error: string
    message: string
}