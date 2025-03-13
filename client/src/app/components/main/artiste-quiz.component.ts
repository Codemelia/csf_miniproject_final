import { ChangeDetectorRef, Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ArtisteService } from '../../services/artiste.service';
import { ApiError } from '../../models/app.models';
import { Subscription, tap } from 'rxjs';
import { AuthService } from '../../services/auth.service';

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
  totalSteps: number = 5
  progress: number = 20 // Initial progress (1/5)

  stageName = ''
  bio = ''
  photo: Blob | null = null
  photoUrl: string | null = null

  error!: ApiError
  successMsg: string | null = null

  onboardingUrl: string | null = null

  private artisteSvc = inject(ArtisteService)
  private router = inject(Router)

  private artisteSub!: Subscription
  private stripeSub!: Subscription

  ngOnInit(): void {

    if (!this.artisteId) {
      this.error = { timestamp: new Date(), status: 401, error: 'Unauthorized', message: 'Unauthorized access. Please log in again.' }
      this.router.navigate(['/'])
      return;
    }

    if (this.artisteExists && !this.isStripeLinked) {
      this.step = 4 // if artiste exists, set step to 4 directly
      this.progress = 100 // set progress to full directly
    }
    
  }

  nextStep() {
      if (this.step < this.totalSteps) {
          this.step++;
          this.progress = (this.step / this.totalSteps) * 100;
      }
  }

  previousStep() {
      if (this.step > 1) {
          this.step--;
          this.progress = (this.step / this.totalSteps) * 100;
      }
  }

  // for image preview (ui feedback)
  onFileChange(event: any) {
      this.photo = event.target.files[0]

      // conv to base64 url for preview
      const reader = new FileReader()
      reader.onload = () => {
        this.photoUrl = reader.result as string
      }

      if (this.photo != null) {
        reader.readAsDataURL(this.photo)
      }
  }

  /*
  // converts base 64 to bytes
  private base64ToBytes(base64: string): Uint8Array {
    const binary = atob(base64);
    const bytes = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
        bytes[i] = binary.charCodeAt(i);
    }
    return bytes;
  }
  */

  // save artiste data before setting up stripe (will update visually in ui)
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
        // Convert the base64 result back to a Blob
        const photoBlob = this.dataURLtoBlob(reader.result as string)
        console.log("Blob size (bytes):", photoBlob.size);
        this.saveArtiste(photoBlob)
      };
      reader.readAsDataURL(this.photo); // Convert photo to base64
    } else {
      this.saveArtiste(null); // If no photo, pass null
    }

  }

  // conv data url to blob
  dataURLtoBlob(dataURL: string): Blob {
    if (!dataURL) {
      throw new Error("Invalid data URL provided.");
    }
  
    const arr = dataURL.split(',');
    if (arr.length !== 2) {
      throw new Error("Data URL format is incorrect.");
    }
  
    const mimeMatch = arr[0].match(/:(.*?);/);
    if (!mimeMatch || mimeMatch.length < 2) {
      throw new Error("Could not extract MIME type from data URL.");
    }
    
    const mime = mimeMatch[1];
    const bstr = atob(arr[1]);
    const n = bstr.length;
    const u8arr = new Uint8Array(n);
    for (let i = 0; i < n; i++) {
      u8arr[i] = bstr.charCodeAt(i);
    }
    
    return new Blob([u8arr], { type: mime });
  }  

  // helper method for saving artiste after conversion of photo
  private saveArtiste(photoBlob: Blob | null) {
    if (this.artisteId) {
      this.artisteSub = this.artisteSvc.createArtiste(this.artisteId, this.stageName, this.bio, photoBlob).subscribe({
        next: (resp) => {
          console.log(`>>> Artiste save response: ${resp}`)
          
          if (resp.includes('successful')) {
            this.nextStep() // go next step in progress bar
            this.successMsg = null
          } else {
            console.error('>>> Stripe error:', resp)
            this.error = {
              timestamp: new Date(),
              status: 400,
              error: 'Error saving profile. Please try again.',
              message: resp
            }
          }
        },
        error: (err) => {
          this.error = err
          console.log(this.error)
        }
      })
    }
  }

  // generate oauth url only if artisteId exists and stripe access token exists under that id
  genOAuthUrl() {
    if (this.artisteId && !this.isStripeLinked) {
      this.stripeSub = this.artisteSvc.genOAuthUrl(this.artisteId).subscribe({
        next: (resp) => {
          if (resp.startsWith('https://')) {
            this.successMsg = null
            console.log('>>> Stripe OAuth URL: ', resp as string)
            this.onboardingUrl = resp
            setTimeout(() => {
              if (this.onboardingUrl) {
                window.location.href = this.onboardingUrl } // takes user to url
            }, 2000)
          }
        },
        error: (err) => {
          this.successMsg = null
          this.error = err
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
