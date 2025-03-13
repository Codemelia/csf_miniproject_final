export interface ApiError {
    timestamp: Date
    status: number
    error: string
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

export interface AuthResponse {
    userId: string
    token: string
    message: string
}

export interface AuthState {
    loggedIn: boolean;
    userRole: string | null;
    userId: string | null;
}

export interface DecodedToken {
    role: string
    sub: string
    [key: string]: any
}

export interface TipRequest {
    tipperId: string
    artisteStageName: string
    amount: number
}

export interface TipResponse {
    clientSecret: string
    tip: TipResponse
}

export interface Tip {
    tipId: number
    tipperId: string
    artisteId: string
    amount: number
    paymentIntentId: string
    paymentStatus: string
    createdAt: Date
    updatedAt:Date
}

export interface Artiste {
    artisteId: string
    stageName: string
    bio: string | null
    photo: Blob | null
}