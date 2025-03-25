// spotify.service.ts
import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthStore } from '../stores/auth.store';
import { PlaylistDetails, SpotifyTrack } from '../models/app.models';

@Injectable()
export class SpotifyService {

    // service/store
    private http = inject(HttpClient)
    private authStore = inject(AuthStore)

    // gen oauth link
    genOAuthUrl(artisteId: string): Observable<string> {
        return this.http.get<string>(`/api/spotify/gen-oauth/${artisteId}`,
            { headers: this.authStore.getJsonHeaders(),
                responseType: 'text' as 'json' })
    }

    // check if spotify is linked
    checkSpotifyLinked(artisteId: string): Observable<boolean> {
        const params = new HttpParams().set('artisteId', artisteId)
        return this.http.get<boolean>('/api/spotify/linked',
            { params, headers: this.authStore.getJsonHeaders() })
    }
    
    // search tracks
    searchTracks(artisteId: string, query: string): Observable<SpotifyTrack[]> {
        const params = new HttpParams().set('query', query)
        return this.http.get<SpotifyTrack[]>(`/api/spotify/search/${artisteId}`,
            { params, headers: this.authStore.getJsonHeaders() })
    }
    
    // save playlist
    savePlaylist(artisteId: string, selectedTracks: SpotifyTrack[]): Observable<PlaylistDetails> {
        return this.http.put<PlaylistDetails>(`/api/spotify/save-playlist/${artisteId}`, 
            selectedTracks, { headers: this.authStore.getJsonHeaders() })
    }

    // get playlist url
    getPlaylistUrl(artisteId: string): Observable<string> {
        return this.http.get<string>(`/api/spotify/get-playlist/${artisteId}`,
            { headers: this.authStore.getJsonHeaders(), 
                responseType: 'text' as 'json' })
    }

}