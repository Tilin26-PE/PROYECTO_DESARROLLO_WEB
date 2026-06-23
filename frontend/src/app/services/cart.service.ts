import { Injectable, signal, WritableSignal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Juego } from './juego.service';

export interface CartItem {
  id: number;
  cantidad: number;
  precio: number;
  juego: {
    id: number;
    nombre: string;
    precio: number;
    descuento: number;
    precioConDescuento: number;
    imagenUrl: string;
  };
}

export interface CartResponse {
  items: CartItem[];
  total: number;
}

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = 'http://localhost:8080/api/carrito';

  // Signals for navbar and components to bind reactively
  public cartItemsCount = signal<number>(0);
  public cartTotal = signal<number>(0);
  public currentCartItems = signal<CartItem[]>([]);

  constructor(private http: HttpClient) {}

  private updateSignals(res: CartResponse) {
    this.currentCartItems.set(res.items);
    this.cartTotal.set(res.total);
    const count = res.items.reduce((sum, item) => sum + item.cantidad, 0);
    this.cartItemsCount.set(count);
  }

  getCart(): Observable<CartResponse> {
    return this.http.get<CartResponse>(this.apiUrl, { withCredentials: true }).pipe(
      tap(res => this.updateSignals(res))
    );
  }

  addToCart(juegoId: number): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/agregar/${juegoId}`, {}, { withCredentials: true }).pipe(
      tap(res => this.updateSignals(res))
    );
  }

  removeFromCart(juegoId: number): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/remover/${juegoId}`, {}, { withCredentials: true }).pipe(
      tap(res => this.updateSignals(res))
    );
  }

  deleteFromCart(juegoId: number): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/eliminar/${juegoId}`, {}, { withCredentials: true }).pipe(
      tap(res => this.updateSignals(res))
    );
  }

  clearCart(): Observable<CartResponse> {
    return this.http.post<CartResponse>(`${this.apiUrl}/vaciar`, {}, { withCredentials: true }).pipe(
      tap(res => this.updateSignals(res))
    );
  }

  checkout(): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/checkout`, {}, { withCredentials: true }).pipe(
      tap(() => {
        // Reset signals upon successful checkout
        this.cartItemsCount.set(0);
        this.cartTotal.set(0);
        this.currentCartItems.set([]);
      })
    );
  }

  getHistorial(): Observable<any[]> {
    return this.http.get<any[]>(`${this.apiUrl}/historial`, { withCredentials: true });
  }
}
