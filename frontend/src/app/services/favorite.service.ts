import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Juego } from './juego.service';

@Injectable({
  providedIn: 'root'
})
export class FavoriteService {
  private apiUrl = 'http://localhost:8080/api/favoritos';
  
  // Writable signal tracking current favorites list to trigger global discount alerts in navbar
  public favoritesList = signal<Juego[]>([]);

  constructor(private http: HttpClient) {}

  getFavorites(): Observable<Juego[]> {
    return this.http.get<Juego[]>(this.apiUrl, { withCredentials: true }).pipe(
      tap(juegos => this.favoritesList.set(juegos))
    );
  }

  toggleFavorite(juegoId: number): Observable<{ favorito: boolean }> {
    return this.http.post<{ favorito: boolean }>(`${this.apiUrl}/alternar/${juegoId}`, {}, { withCredentials: true }).pipe(
      tap(() => {
        // Refresh local list to keep alerts synched
        this.getFavorites().subscribe();
      })
    );
  }

  checkFavoriteStatus(juegoId: number): Observable<{ favorito: boolean }> {
    return this.http.get<{ favorito: boolean }>(`${this.apiUrl}/status/${juegoId}`, { withCredentials: true });
  }
}
