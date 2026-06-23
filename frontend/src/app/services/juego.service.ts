import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Juego {
  id: number;
  nombre: string;
  categoria: string;
  descripcion: string;
  precio: number;
  imagenUrl: string;
  videoUrl: string;
  plataforma: string;
  fechaLanzamiento: string;
  descuento?: number;
  precioConDescuento?: number;
  calificacionPromedio?: number;
  cantidadResenas?: number;
  requisitosMinimos?: string;
  requisitosRecomendados?: string;
}

@Injectable({
  providedIn: 'root'
})
export class JuegoService {
  private apiUrl = 'http://localhost:8080/api/juegos';

  constructor(private http: HttpClient) {}

  getJuegos(): Observable<Juego[]> {
    return this.http.get<Juego[]>(this.apiUrl);
  }

  getJuego(id: number): Observable<Juego> {
    return this.http.get<Juego>(`${this.apiUrl}/${id}`);
  }

  getDlcs(id: number): Observable<Juego[]> {
    return this.http.get<Juego[]>(`${this.apiUrl}/${id}/dlcs`);
  }

  getCategorias(): Observable<String[]> {
    return this.http.get<String[]>(`${this.apiUrl}/categorias`);
  }

  getPlataformas(): Observable<String[]> {
    return this.http.get<String[]>(`${this.apiUrl}/plataformas`);
  }

  getAnios(): Observable<number[]> {
    return this.http.get<number[]>(`${this.apiUrl}/anios`);
  }

  buscarJuegos(query?: string, categoria?: string, plataforma?: string, anio?: number): Observable<Juego[]> {
    let params = new HttpParams();
    if (query) params = params.set('query', query);
    if (categoria) params = params.set('categoria', categoria);
    if (plataforma) params = params.set('plataforma', plataforma);
    if (anio) params = params.set('anio', anio.toString());
    
    return this.http.get<Juego[]>(`${this.apiUrl}/buscar`, { params });
  }

  getRanking(): Observable<Juego[]> {
    return this.http.get<Juego[]>(`${this.apiUrl}/ranking`);
  }

  getRecientes(): Observable<Juego[]> {
    return this.http.get<Juego[]>(`${this.apiUrl}/lanzamientos/recientes`);
  }

  getProximos(): Observable<Juego[]> {
    return this.http.get<Juego[]>(`${this.apiUrl}/lanzamientos/proximos`);
  }
}
