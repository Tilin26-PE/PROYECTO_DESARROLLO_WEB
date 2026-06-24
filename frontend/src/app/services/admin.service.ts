import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Juego } from './juego.service';

export interface AdminDashboardData {
  juegos: Juego[];
  resenas: {
    id: number;
    autor: string;
    comentario: string;
    calificacion: number;
    juegoId: number;
    juegoNombre: string;
  }[];
  categorias: string[];
}

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private apiUrl = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  getDashboard(): Observable<AdminDashboardData> {
    return this.http.get<AdminDashboardData>(`${this.apiUrl}/dashboard`, { withCredentials: true });
  }

  saveJuego(juego: Partial<Juego>): Observable<Juego> {
    return this.http.post<Juego>(`${this.apiUrl}/juegos`, juego, { withCredentials: true });
  }

  deleteJuego(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/juegos/${id}`, { withCredentials: true });
  }

  uploadPortada(file: File): Observable<{ url: string }> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<{ url: string }>(`${this.apiUrl}/juegos/upload`, formData, { withCredentials: true });
  }

  saveResena(resena: { id?: number | null; autor: string; comentario: string; calificacion: number; juegoId: number }): Observable<any> {
    return this.http.post(`${this.apiUrl}/resenas`, resena, { withCredentials: true });
  }

  deleteResena(id: number): Observable<any> {
    return this.http.delete(`${this.apiUrl}/resenas/${id}`, { withCredentials: true });
  }
}
