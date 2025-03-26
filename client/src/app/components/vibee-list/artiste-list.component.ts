import { Component, inject, OnDestroy, OnInit } from '@angular/core';
import { ApiError, ArtisteProfile } from '../../models/app.models';
import { ArtisteService } from '../../services/artiste.service';
import { AuthStore } from '../../stores/auth.store';
import { Subscription } from 'rxjs';
import { Router } from '@angular/router';
import { Title } from '@angular/platform-browser';

@Component({
  selector: 'app-artiste-list',
  standalone: false,
  templateUrl: './artiste-list.component.html',
  styleUrl: './artiste-list.component.scss'
})
export class ArtisteListComponent implements OnInit, OnDestroy {

  artistes: ArtisteProfile[] = []
  isLoading = true
  error: ApiError | null = null
  searchTerm: string = ''
  filteredArtistes: ArtisteProfile[] = []

  private artisteSvc = inject(ArtisteService)
  private authStore = inject(AuthStore)
  private router = inject(Router)
  private title = inject(Title)

  artisteId: string | null = null

  // subs
  private artisteSub!: Subscription

  ngOnInit() {
    this.title.setTitle('Vibees')

    this.artisteId = this.authStore.extractUserRoleFromToken()
    this.artisteSub = this.artisteSvc.getAllArtisteProfiles().subscribe({
      next: (data) => {
        this.error = null
        this.artistes = data
        this.artistes.forEach(
          (artiste) => {
            if (artiste.photo) {
              const mimeType = this.artisteSvc.getImageMimeType(artiste.photo.toString())
              artiste.photoUrl = `data:${mimeType};base64,${artiste.photo}` // convert to data url
            }

            this.filteredArtistes = [
              ...this.filteredArtistes, // only add artiste if they have valid stage name
              artiste
            ]
            console.log('>>> Filtered artistes: ', this.filteredArtistes)
          }
        )
        this.isLoading = false
      },
      error: (err) => {
        this.error = err.error
        this.isLoading = false
      }
    })
  }

  tipVibee(qrCodeUrl: string | null) {
    if (qrCodeUrl) window.location.href = qrCodeUrl
  }

  filterArtistes() {
    console.log('>>> Filtering...')
    this.filteredArtistes = this.artistes.filter(artiste =>
      artiste.stageName.toLowerCase().includes(this.searchTerm.toLowerCase())
    )
  }

  ngOnDestroy() {
    this.artisteSub?.unsubscribe()
  }
  
}