import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Resena {
  id: number;
  autor: string;
  comentario: string;
  calificacion: number;
  usuarioLogin: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = `${environment.apiUrl}/api/resenas`;

  constructor(private http: HttpClient) {}

  getReviews(juegoId: number): Observable<Resena[]> {
    return this.http.get<Resena[]>(`${this.apiUrl}/juego/${juegoId}`);
  }

  postReview(juegoId: number, commentData: { comentario: string; calificacion: number }): Observable<Resena> {
    return this.http.post<Resena>(`${this.apiUrl}/juego/${juegoId}`, commentData, { withCredentials: true });
  }
}
