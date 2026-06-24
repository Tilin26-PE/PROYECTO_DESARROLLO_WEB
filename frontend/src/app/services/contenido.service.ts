import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface Noticia {
  id: number;
  titulo: string;
  subtitulo: string;
  contenido: string;
  fecha: string;
  imagenUrl: string;
  juegoId?: number;
}

export interface Guia {
  id: number;
  titulo: string;
  contenido: string;
  autor: string;
  fecha: string;
  juegoId?: number;
}

export interface Truco {
  id: number;
  titulo: string;
  contenido: string;
  juegoId?: number;
}

@Injectable({
  providedIn: 'root'
})
export class ContenidoService {
  private apiUrl = `${environment.apiUrl}/api/contenido`;

  constructor(private http: HttpClient) {}

  getNoticias(juegoId?: number): Observable<Noticia[]> {
    let params = new HttpParams();
    if (juegoId) params = params.set('juegoId', juegoId.toString());
    return this.http.get<Noticia[]>(`${this.apiUrl}/noticias`, { params });
  }

  getNoticia(id: number): Observable<Noticia> {
    return this.http.get<Noticia>(`${this.apiUrl}/noticias/${id}`);
  }

  getGuias(juegoId?: number): Observable<Guia[]> {
    let params = new HttpParams();
    if (juegoId) params = params.set('juegoId', juegoId.toString());
    return this.http.get<Guia[]>(`${this.apiUrl}/guias`, { params });
  }

  getGuia(id: number): Observable<Guia> {
    return this.http.get<Guia>(`${this.apiUrl}/guias/${id}`);
  }

  getTrucos(juegoId: number): Observable<Truco[]> {
    return this.http.get<Truco[]>(`${this.apiUrl}/trucos/juego/${juegoId}`);
  }
}
