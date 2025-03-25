import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ArtisteService } from '../../services/artiste.service';
import { ApiError, ArtisteProfile } from '../../models/app.models';
import { Subscription, tap } from 'rxjs';

@Component({
  selector: 'app-artiste-quiz',
  standalone: false,
  templateUrl: './artiste-quiz.component.html',
  styleUrl: './artiste-quiz.component.scss'
})

export class ArtisteQuizComponent implements OnInit, OnDestroy {

  // evaluates artiste account setup progress
  @Input()
  isStripeLinked!: boolean

  @Input()
  artisteExists: boolean = false

  @Input()
  artisteId: string | null = null

  step: number = 1
  totalSteps: number = 7
  progress: number = 100 / 7 // Initial progress (1/7)

  // categories for artistes to choose from
  categories: string[] = [
    'Music', 'Comedy', 'Dance', 'Theater',
    'Poetry', 'Film', 'Visual Arts', 'Literature',
    'Photography', 'Magic', 'Fashion', 'Podcasting',
    'Street Performance', 'Painting', 'Animation',
    'Live Streaming', 'Radio', 'Fitness', 'Crafts',
    'Gaming', 'Others' ]

  stageName = ''
  selectedCategories: string[] = []
  bio: string | null = null
  photo: Blob | null = null
  thankYouMessage: string | null = null

  photoUrl: string | null = null
  fileTooLarge: boolean = false

  error!: ApiError

  onboardingUrl: string | null = null

  private artisteSvc = inject(ArtisteService)
  private router = inject(Router)

  private artisteSub!: Subscription
  private stripeSub!: Subscription

  isLoading: boolean = false

  ngOnInit(): void {

    if (this.artisteExists && !this.isStripeLinked) {
      this.step = 6 // if artiste exists but stripe oauth not complete, set step to 6
      this.progress = (this.step / this.totalSteps) * 100 // set 
    }
    
  }

  nextStep() {
      if (this.step < this.totalSteps) {
        this.step++;
        this.progress = (this.step / this.totalSteps) * 100
      }
  }

  previousStep() {
      if (this.step > 1) {
        this.step--;
        this.progress = (this.step / this.totalSteps) * 100
      }
  }

  // for image preview (ui feedback)
  onFileChange(event: any) {
      this.photo = event.target.files[0]

      if (this.photo) {

        // set max size to 10mb in bytes
        // if image size too large, set file too large to show error on form
        const maxSize = 10 * 1024 * 1024
        if (this.photo.size > maxSize) {

          this.fileTooLarge = true
          this.photoUrl = null

        } else {

          this.fileTooLarge = false

          // conv to base64 url for preview
          const reader = new FileReader()
          reader.onload = () => {
            this.photoUrl = reader.result as string
          }

          reader.readAsDataURL(this.photo)
        }

      }

  }

  // save artiste data before setting up stripe
  saveData() {

    // validate stage name
    if (!this.stageName || this.stageName.trim().length <= 0) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Invalid Stage Name',
        message: 'Invalid stage name. Please enter a valid stage name to continue.'
      }
      return
    }

    if (this.photo) {
      const reader = new FileReader();
      reader.onload = () => {
        // convert the base64 result back to a Blob
        const photoBlob = this.dataURLtoBlob(reader.result as string)
        console.log("Blob size (bytes):", photoBlob?.size)
        this.saveArtiste(photoBlob || null)
      };
      reader.readAsDataURL(this.photo) // convert photo to base64 for preview image
    } else {
      this.saveArtiste(null); // if no photo, pass null
    }

  }

  // conv data url back to blob
  dataURLtoBlob(dataURL: string): Blob | undefined {
    if (!dataURL) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Invalid data URL',
        message: 'Invalid data URL'
      }
      return
    }
  
    const arr = dataURL.split(',') // to remove img prefix
    if (arr.length !== 2) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Invalid data URL',
        message: 'Invalid data URL'
      }
      return
    }
  
    const mimeMatch = arr[0].match(/:(.*?);/);
    if (!mimeMatch || mimeMatch.length < 2) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Invalid data URL',
        message: 'Invalid data URL'
      }
      return
    }
    
    const mime = mimeMatch[1] // get mime type
    const bstr = atob(arr[1]) // decode base64 string
    const n = bstr.length
    const u8arr = new Uint8Array(n) // conv to uint array
    for (let i = 0; i < n; i++) {
      u8arr[i] = bstr.charCodeAt(i) // replace array with bstr
    }
    
    return new Blob([u8arr], { type: mime }) // change to blob and takes in mimetype
  }  

  // helper method for saving artiste after conversion of photo
  private saveArtiste(photoBlob: Blob | null) {
    this.isLoading = true // set spinner
    if (this.artisteId) {
      const profile: ArtisteProfile = {
        artisteId: this.artisteId,
        stageName: this.stageName,
        categories: this.selectedCategories,
        bio: this.bio,
        photo: photoBlob,
        qrCode: null,
        qrCodeUrl: null,
        thankYouMessage: this.thankYouMessage,
        photoUrl: null
      }

      this.artisteSub = this.artisteSvc.createArtisteProfile(profile).subscribe({
        next: (resp) => {
          console.log(`>>> Artiste save response: ${resp}`)
          
          if (resp.includes('successful')) {
            this.nextStep() // go next step in progress bar
            this.artisteExists = true
            this.isLoading = false
          } else {
            console.error('>>> Error saving:', resp)
            this.error = {
              timestamp: new Date(),
              status: 400,
              error: 'Error saving profile. Please try again.',
              message: resp
            }
            this.isLoading = false
          }
        },
        error: (err) => {
          this.error = err.error
          console.log(this.error)
          this.isLoading = false
        }
      })
    }
  }

  // generate oauth url only if artisteId exists and stripe access token exists under that id
  linkStripe() {
    this.isLoading = true // set spinner
    if (this.artisteId && !this.isStripeLinked) {
      this.stripeSub = this.artisteSvc.genOAuthUrl(this.artisteId).subscribe({
        next: (resp) => {
          if (resp.startsWith('https://')) {
            console.log('>>> Stripe OAuth URL: ', resp as string)
            setTimeout(() => {
              this.isLoading = false
              window.location.href = resp // takes user to url
            }, 2000)
          }
        },
        error: (err) => {
          this.isLoading = false
          this.error = err.error
          console.log('>>> Stripe OAuth URL creation failed: ', this.error.message)
        }
      })
    }
  }

  // unsub
  ngOnDestroy(): void {
    if (this.artisteSub) { this.artisteSub.unsubscribe() }
    if (this.stripeSub) { this.stripeSub.unsubscribe() }
  }

}
