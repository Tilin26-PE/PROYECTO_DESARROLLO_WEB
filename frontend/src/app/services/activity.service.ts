import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

export interface ActivityLog {
  id: number;
  accion: string;
  detalle: string;
  fecha: string;
  juegoId?: number;
  juegoNombre?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ActivityService {
  private apiUrl = `${environment.apiUrl}/api/actividad`;

  constructor(private http: HttpClient) {}

  getActivityLogs(): Observable<ActivityLog[]> {
    return this.http.get<ActivityLog[]>(this.apiUrl, { withCredentials: true });
  }
}
