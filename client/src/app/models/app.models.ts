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