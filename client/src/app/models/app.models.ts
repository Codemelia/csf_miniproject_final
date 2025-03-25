import { SafeResourceUrl } from "@angular/platform-browser"

export interface ApiError {
    timestamp: Date
    status: number
    error: string
    message: string
}

export interface UserRegistration {
    email: string
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
    expiry: Date
}

export interface AuthState {
    token: string | null
    loggedIn: boolean
    userRole: string | null
    userId: string | null
}

export interface DecodedToken {
    role: string | null
    sub: string | null
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
    paymentMethodId: string | null
}

export interface TipResponse {
    tipperId: string
    clientSecret: string
}

export interface ArtisteProfile {
    artisteId: string
    stageName: string
    categories: string[]
    bio: string | null
    photo: Blob | null
    photoUrl: string | null
    qrCode: Blob | null
    qrCodeUrl: string | null
    thankYouMessage: string | null
}

export interface SpotifyTrack {
    trackId: string
    embedUrl: string
    sanitizedUrl: SafeResourceUrl | null
}

export interface PlaylistDetails {
    playlistId: string
    playlistName: string
    playlistUrl: string
}