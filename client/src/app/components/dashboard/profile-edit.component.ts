import { Component, inject, Input, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ArtisteService } from '../../services/artiste.service';
import { AuthStore } from '../../stores/auth.store';
import { catchError, debounceTime, map, Subscription, tap, throwError } from 'rxjs';
import { ApiError, ArtisteProfile, PlaylistDetails, SpotifyTrack } from '../../models/app.models';
import { Router } from '@angular/router';
import { MatSnackBar } from '@angular/material/snack-bar';
import { SpotifyService } from '../../services/spotify.service';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';

@Component({
  selector: 'app-profile',
  standalone: false,
  templateUrl: './profile-edit.component.html',
  styleUrl: './profile-edit.component.scss'
})
export class ProfileEditComponent implements OnInit, OnDestroy {

  // Form group for profile editing
  private fb = inject(FormBuilder)
  protected profileForm!: FormGroup
  protected spotifyForm!: FormGroup

  // services
  private artisteSvc = inject(ArtisteService)
  private authStore = inject(AuthStore)
  private snackBar = inject(MatSnackBar)
  private spotifySvc = inject(SpotifyService)
  private sanitizer = inject(DomSanitizer)

  artisteExists: boolean = false
  artisteId: string | null = null
  isLoading: boolean = false // for API loading state
  error!: ApiError
  spotifyLoading: boolean = false

  // subs
  private artisteSub!: Subscription
  private updateSub!: Subscription

  private linkCheckSub!: Subscription
  private oAuthSub!: Subscription
  private searchSub!: Subscription
  private saveSub!: Subscription
  private playlistSub!: Subscription

  // profile properties
  stageName: string = ''
  qrCode: Blob | null = null
  qrCodeUrl: string | null = null // for sharing of tipping form
  qrCodeViewUrl: string | null = null // for viewing of qr code on page

  // photo properties
  photoUrl: string | null = null
  photo: Blob | null = null
  fileTooLarge: boolean = false

  // categories for artistes to choose from
  categories: string[] = [
    'Music', 'Comedy', 'Dance', 'Theater',
    'Poetry', 'Film', 'Visual Arts', 'Literature',
    'Photography', 'Magic', 'Fashion', 'Podcasting',
    'Street Performance', 'Painting', 'Animation',
    'Live Streaming', 'Radio', 'Fitness', 'Crafts',
    'Gaming', 'Others' ]

  // spotify properties
  spotifyLinked = false
  searchResults: SpotifyTrack[] = []
  selectedTracks: SpotifyTrack[] = []
  playlistDetails: PlaylistDetails | null = null
  spotifyError!: ApiError
  playlistUrl: string | null = null

  sanitizedUrl!: SafeResourceUrl

  ngOnInit() {

    // authentication
    this.artisteId = this.authStore.extractUIDFromToken()
    this.checkIfArtisteExists()

    // create forms on init
    this.createForms()

    // get profile data on init
    const profile = localStorage.getItem("artisteProfile")
    if (!profile || profile === null) { 
      console.log('>>> Getting profile from server')
      this.fetchProfileData()
    } else {
      console.log('>>> Getting profile from local storage')
      this.parseProfileData(JSON.parse(profile))
    }
    
    // spotify
    this.checkSpotifyStatus()

  }

  // create forms
  createForms() {
    this.profileForm = this.fb.group({
      categories: this.fb.control<string[]>([],
        [ Validators.maxLength(5) ]
      ),
      bio: this.fb.control<string>('',
        [ Validators.maxLength(150) ]
      ),
      thankYouMessage: this.fb.control<string>('',
        [ Validators.maxLength(150) ]
      )
    })

    this.spotifyForm = this.fb.group({
      searchQuery: this.fb.control<string>('')
    })
  }

  // checking if artiste exists
  checkIfArtisteExists(): void {
    const existString = localStorage.getItem("artisteExists")
    if (existString) this.artisteExists = JSON.parse(existString)
      else this.artisteExists = false
  }

  // fetch profile data
  fetchProfileData() {
    this.isLoading = true
    this.artisteSub = this.artisteSvc.getArtisteProfile(this.artisteId!).subscribe(profile => {
      this.parseProfileData(profile)
      this.isLoading = false
      localStorage.setItem("artisteProfile", JSON.stringify(profile)) // save profile to localstorage for later retrieval
    })
  }

  // helper to parse profile data
  private parseProfileData(profile: ArtisteProfile) {
    this.stageName = profile.stageName
      this.qrCode = profile.qrCode
      this.qrCodeUrl = profile.qrCodeUrl
      if (this.qrCode && this.qrCodeUrl) this.qrCodeViewUrl = `data:png;base64, ${this.qrCode}`
      this.profileForm.patchValue({
        categories: profile.categories,
        bio: profile.bio,
        thankYouMessage: profile.thankYouMessage
      })
      if (profile.photo) {
        this.photo = profile.photo
        const mimeType = this.artisteSvc.getImageMimeType(this.photo.toString())
        this.photoUrl = `data:${mimeType};base64,${this.photo}`
      }
  }
  
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

  // update profile data
  updateProfile() {
    // handle image
    if (this.photo) {
      const reader = new FileReader();
      reader.onload = () => {
        // convert the base64 result back to a Blob
        const photoBlob = this.dataURLtoBlob(reader.result as string)
        console.log("Blob size (bytes):", photoBlob?.size)
        this.saveArtiste(photoBlob || null)
      };
      reader.readAsDataURL(this.photo) // convert photo to base64 for preview image
    } else if (this.photo == null || this.photoUrl === '/default_profile_pic.png') {
      this.saveArtiste(null); // if no photo, pass null
    }
    
  }

  // helper method conv data url back to blob
  private dataURLtoBlob(dataURL: string): Blob | undefined {
    if (!dataURL) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Bad Request',
        message: 'Invalid data URL. Please try again.'
      }
      return
    }

    const arr = dataURL.split(',') // to remove img prefix
    if (arr.length !== 2) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Bad Request',
        message: 'Invalid data URL. Please try again.'
      }
      return
    }

    const mimeMatch = arr[0].match(/:(.*?);/);
    if (!mimeMatch || mimeMatch.length < 2) {
      this.error = {
        timestamp: new Date(),
        status: 400,
        error: 'Bad Request',
        message: 'Invalid data URL. Please try again.'
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

  // helper method to save profile
  private saveArtiste(photoBlob: Blob | null) {
    this.isLoading = true
    const updatedProfile = {
      ...this.profileForm.value,
      photo: photoBlob 
    }
    console.log('>>> Updated profile: ', updatedProfile)

    this.updateSub = this.artisteSvc.updateArtisteProfile(updatedProfile, this.artisteId!)
      .subscribe((response) => {
        if (response == true) {
          this.isLoading = false
          console.log('>>> Profile update successful')
          localStorage.removeItem("artisteProfile") // remove from local storage
          window.location.reload() // refresh to update
        } else {
          this.isLoading = false
          console.log('>>> Profile update failed')
          this.error = {
            timestamp: new Date(),
            status: 500,
            error: 'Internal Server Error',
            message: 'Profile update failed. Please try again.'
          }
        }
    })
  }

  // share qr code url
  shareQrCodeUrl() {
    this.shareUrl(this.qrCodeUrl!)
  }

  // SPOTIFY FUNCTIONS

  // check linked and get existing tracks
  checkSpotifyStatus() {
    this.spotifyLoading = true
    this.linkCheckSub = this.spotifySvc.checkSpotifyLinked(this.artisteId!).subscribe((linked) => {
      this.spotifyLinked = linked // updates ui
      console.log('>>> Spotify linked: ', this.spotifyLinked)

      const storedUrl: string | null = localStorage.getItem('playlistUrl')
      if (linked && storedUrl === null) {
        this.playlistSub = this.spotifySvc.getPlaylistUrl(this.artisteId!).subscribe({
          next: (url) => {
            console.log('>>> Playlist url retrieved: ', url)
            this.playlistUrl = url
            localStorage.setItem('playlistUrl', url)
            this.spotifyLoading = false
          },
          error: (err) => {
            console.info('>>> Playlist url error: ', err.error)
            this.error = err.error
            this.spotifyLoading = false
          }
        })
      } else {
        this.playlistUrl = storedUrl
        this.spotifyLoading = false
      }
    })
  }

  linkSpotify() {
    this.spotifyLoading = true
    this.oAuthSub = this.spotifySvc.genOAuthUrl(this.artisteId!).subscribe({
      next: (resp) => {
        console.log('>>> Spotify OAuth URL: ', resp as string)
        if (resp.startsWith('https://')) {
          window.location.href = resp // takes user to url
          this.spotifyLoading = false
        }
      },
      error: (err) => {
        this.error = err.error
        this.spotifyLoading = false
        console.log('>>> Spotify OAuth URL creation failed: ', err)
      }
    })
  }

  // search track
  searchTracks() {
    this.spotifyLoading = true
    const query = this.spotifyForm.value.searchQuery
    if (query.length < 2) return

    this.searchSub = this.spotifySvc.searchTracks(this.artisteId!, query).pipe(
      tap((results: SpotifyTrack[]) => {
        console.log('>>> Retrieved search results')
        this.searchResults = results
        this.searchResults.forEach((t) => {
          t.sanitizedUrl = this.sanitizer.bypassSecurityTrustResourceUrl( // sanitise url for iframe
            t.embedUrl
          )
        })
        this.spotifyLoading = false
      }),
      catchError((err: any) => {
        console.log('>>> Error retrieving search results')
        this.spotifyLoading = false
        this.searchResults = []
        return []
      })
    ).subscribe()
  }

  // add/remove track to/from selected tracks
  selectTrack(track: SpotifyTrack) {
    if (this.selectedTracks.includes(track)) {
      console.log('>>> Removing track from list')
      this.selectedTracks = this.selectedTracks
        .filter((t) => t.trackId !== track.trackId)
    } else {
      console.log('>>> Adding track to list')
      this.selectedTracks = [
        ...this.selectedTracks,
        track
      ]
    }
  }

  // save tracks to playlist
  savePlaylist() {
    this.spotifyLoading = true
    console.log('>>> Saving playlist')
    localStorage.removeItem('playlistUrl') // remove to update local storage on next retrieval

    // dont save if selection empty
    if (this.selectedTracks.length <= 0) {
      return
    }

    this.saveSub = this.spotifySvc.savePlaylist(this.artisteId!, this.selectedTracks)
    .subscribe({
        next: (details) => {
            console.log('>>> Playlist saved successfully')
            this.playlistDetails = details
            this.spotifyLoading = false
        },
        error: (err) => {
            console.error('>>> Error saving playlist: ', err)
            this.spotifyError = err.error
            this.spotifyLoading = false
        }
    })
  }

  // button to share playlist
  sharePlaylist() {
    this.shareUrl(this.playlistUrl!)
  }

  // share url helper method
  shareUrl(url: string) {
    if (url) {
      navigator.clipboard.writeText(url).then(() => {
        // success notification
        this.snackBar.open('Link copied to clipboard!', 'Close', {
          duration: 3000, // Display for 3 seconds
          horizontalPosition: 'center',
          verticalPosition: 'bottom'
        })
      }).catch(err => {
        // error notification
        this.snackBar.open('Failed to copy link.', 'Close', {
          duration: 3000,
          horizontalPosition: 'center',
          verticalPosition: 'bottom'
        })
        console.error('Failed to copy link:', err)
      })
    } else {
      this.snackBar.open('No link available to copy.', 'Close', {
        duration: 3000,
        horizontalPosition: 'center',
        verticalPosition: 'bottom'
      })
    }
  }

  ngOnDestroy(): void {
    this.artisteSub?.unsubscribe()
    this.updateSub?.unsubscribe()
    this.linkCheckSub?.unsubscribe()
    this.oAuthSub?.unsubscribe()
    this.searchSub?.unsubscribe()
    this.saveSub?.unsubscribe()
    this.playlistSub?.unsubscribe()
  }

}