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

export interface Tip {
    tipId: number | null
    tipperName: string | null
    tipperMessage: string | null
    tipperEmail: string | null
    tipperId: string | null
    artisteId: string | null
    amount: number 
    paymentIntentId: string | null
    paymentStatus: string | null
    createdAt: Date | null
    updatedAt: Date | null
    stageName: string | null
}

export interface TipResponse {
    tipperId: string
    clientSecret: string
}

export interface Artiste {
    artisteId: string
    stageName: string
    bio: string | null
    photo: Blob | null
}