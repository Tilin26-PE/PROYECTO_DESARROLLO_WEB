import { Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface User {
  authenticated: boolean;
  username?: string;
  displayName?: string;
  email?: string;
  role?: string;
  avatarUrl?: string;
  bio?: string;
  alertasDescuentos?: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/login';
  
  // Expose current user state as an Angular signal for reactive UI bindings
  public currentUser: WritableSignal<User> = signal({ authenticated: false });

  constructor(private http: HttpClient) {}

  checkSession(): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/current`, { withCredentials: true }).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  login(credentials: { username: string; password: string }): Observable<User> {
    return this.http.post<User>(this.apiUrl, credentials, { withCredentials: true }).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  register(data: { username: string; email: string; password: string }): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/register`, data, { withCredentials: true }).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  logout(): Observable<any> {
    return this.http.get(`${this.apiUrl}/logout`, { withCredentials: true }).pipe(
      tap(() => this.currentUser.set({ authenticated: false }))
    );
  }

  updateProfile(profileData: { displayName: string; email: string; avatarUrl: string; bio: string; alertasDescuentos: boolean }): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/perfil`, profileData, { withCredentials: true }).pipe(
      tap(user => this.currentUser.set(user))
    );
  }

  changePassword(passwordData: { currentPassword: string; newPassword: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/cambiar-password`, passwordData, { withCredentials: true });
  }
}
