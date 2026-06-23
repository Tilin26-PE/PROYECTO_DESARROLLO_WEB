import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { CartService, CartItem } from '../../services/cart.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-carrito',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Tu Carrito de Compras</h2>
          <p>Revisa los videojuegos seleccionados, ajusta las cantidades y confirma tu compra segura.</p>
        </div>
      </section>

      <!-- Main Cart Layout -->
      <div *ngIf="items().length === 0 && !checkoutSuccess()" class="empty-message" style="padding: 40px 0;">
        <p style="font-size: 1.25rem; color: var(--muted); margin-bottom: 20px;">Tu carrito de compras está vacío.</p>
        <a routerLink="/tienda" class="button-solid">Explorar la tienda</a>
      </div>

      <!-- Success Feedback -->
      <div *ngIf="checkoutSuccess()" class="tabs-container" style="text-align: center; padding: 40px; margin-bottom: 24px;">
        <span style="font-size: 3rem;">🎉</span>
        <h3 style="color: white; font-size: 1.8rem; margin: 10px 0 6px 0;">¡Compra Confirmada!</h3>
        <p style="color: var(--muted); max-width: 600px; margin: 0 auto 20px;">
          Tu pedido ha sido procesado con éxito en la base de datos. Los videojuegos han sido agregados a tu biblioteca personal.
        </p>
        <div style="display: flex; gap: 10px; justify-content: center;">
          <a routerLink="/actividad" class="button-solid">Ver Bitácora</a>
          <button (click)="resetCheckoutFeedback()" class="button-ghost">Volver al Carrito</button>
        </div>
      </div>

      <div class="detail-layout" style="grid-template-columns: 1.3fr 0.7fr;" *ngIf="items().length > 0">
        
        <!-- Cart Items List -->
        <div class="tabs-container" style="padding: 20px; display: flex; flex-direction: column; gap: 16px;">
          <div *ngFor="let item of items()" 
               style="display: grid; grid-template-columns: 80px 1fr 140px 100px; align-items: center; gap: 18px; border-bottom: 1px solid var(--border); padding-bottom: 16px;">
            
            <!-- Game Cover -->
            <div>
              <img [src]="item.juego.imagenUrl" [alt]="item.juego.nombre" style="width: 80px; height: 105px; object-fit: cover; border-radius: 12px; border: 1px solid var(--border);">
            </div>

            <!-- Title & Price details -->
            <div>
              <h4 style="margin: 0 0 6px 0; font-size: 1.15rem; color: white;">{{ item.juego.nombre }}</h4>
              <span style="color: var(--muted); font-size: 0.88rem;">Precio unitario: S/ {{ item.juego.precioConDescuento | number:'1.2-2' }}</span>
            </div>

            <!-- Quantity adjust controls -->
            <div style="display: flex; align-items: center; gap: 10px;">
              <button (click)="decrementQuantity(item.juego.id)" class="button-ghost" style="width: 32px; height: 32px; padding: 0; display: inline-flex; align-items: center; justify-content: center; font-weight: bold;">-</button>
              <strong style="font-size: 1.1rem; color: white; min-width: 20px; text-align: center;">{{ item.cantidad }}</strong>
              <button (click)="incrementQuantity(item.juego.id)" class="button-ghost" style="width: 32px; height: 32px; padding: 0; display: inline-flex; align-items: center; justify-content: center; font-weight: bold;">+</button>
            </div>

            <!-- Item Total & Delete -->
            <div style="text-align: right; display: flex; flex-direction: column; align-items: flex-end; gap: 8px;">
              <strong style="color: white; font-size: 1.1rem;">S/ {{ (item.juego.precioConDescuento * item.cantidad) | number:'1.2-2' }}</strong>
              <button (click)="deleteItem(item.juego.id)" class="logout-btn" style="padding: 4px 8px; font-size: 0.78rem; border-radius: 6px;">Quitar</button>
            </div>

          </div>

          <!-- Empty Actions -->
          <div style="display: flex; justify-content: space-between; align-items: center; padding-top: 10px;">
            <a routerLink="/tienda" class="button-link" style="padding: 8px 16px;">← Seguir comprando</a>
            <button (click)="clearCart()" class="logout-btn" style="padding: 8px 16px; border-radius: 99px;">Vaciar Carrito</button>
          </div>
        </div>

        <!-- Cart Summary Panel -->
        <div class="detail-panel" style="padding: 24px; display: flex; flex-direction: column; gap: 18px;">
          <h3 style="margin: 0; font-size: 1.45rem; color: white; border-bottom: 1px solid var(--border); padding-bottom: 12px;">Resumen</h3>
          
          <div style="display: flex; justify-content: space-between; font-size: 0.95rem; color: var(--muted);">
            <span>Subtotal</span>
            <span>S/ {{ total() | number:'1.2-2' }}</span>
          </div>
          <div style="display: flex; justify-content: space-between; font-size: 0.95rem; color: var(--muted);">
            <span>IGV (18% Incluido)</span>
            <span>S/ {{ (total() * 0.18) | number:'1.2-2' }}</span>
          </div>

          <div style="display: flex; justify-content: space-between; font-size: 1.25rem; font-weight: 800; color: white; border-top: 1px solid var(--border); padding-top: 14px; margin-top: 4px;">
            <span>Total Final</span>
            <span style="color: var(--accent-2);">S/ {{ total() | number:'1.2-2' }}</span>
          </div>

          <div style="margin-top: 10px;">
            <ng-container *ngIf="user().authenticated; else loginToBuy">
              <button (click)="processCheckout()" class="button-solid" style="width: 100%; padding: 14px 20px; font-size: 1.05rem;">
                Confirmar Compra ✔
              </button>
            </ng-container>
            <ng-template #loginToBuy>
              <a routerLink="/login" class="button-solid" style="width: 100%; text-decoration: none; padding: 14px 20px; text-align: center; display: block;">
                Ingresar para Comprar
              </a>
            </ng-template>
          </div>
          
          <p *ngIf="errorMessage()" style="color: #ef4444; font-weight: bold; font-size: 0.88rem; text-align: center; margin: 0;">
            {{ errorMessage() }}
          </p>
        </div>

      </div>

      <!-- Purchase History Section (Visible if logged in) -->
      <section class="detail-section" style="margin-top: 60px;" *ngIf="user().authenticated">
        <h3 class="timeline-section-header" style="margin-bottom: 24px;">Historial de Pedidos Realizados</h3>
        
        <div *ngIf="history().length === 0" class="empty-message">
          <p>No registras compras anteriores en esta cuenta.</p>
        </div>

        <div style="display: flex; flex-direction: column; gap: 16px;" *ngIf="history().length > 0">
          <div *ngFor="let order of history()" class="requirements-card" style="padding: 20px;">
            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px; margin-bottom: 12px; flex-wrap: wrap; gap: 10px;">
              <div>
                <strong style="color: white; font-size: 1.1rem;">Pedido #{{ order.id }}</strong>
                <span style="color: var(--muted); font-size: 0.88rem; margin-left: 12px;">Realizado el: {{ order.fecha | date:'dd/MM/yyyy HH:mm' }}</span>
              </div>
              <strong style="color: var(--accent-2); font-size: 1.15rem;">S/ {{ order.total | number:'1.2-2' }}</strong>
            </div>

            <!-- Ordered items details -->
            <div style="display: flex; flex-direction: column; gap: 10px;">
              <div *ngFor="let item of order.items" style="display: flex; align-items: center; justify-content: space-between; font-size: 0.92rem;">
                <div style="display: flex; align-items: center; gap: 10px;">
                  <span style="color: var(--muted);">{{ item.cantidad }}x</span>
                  <a [routerLink]="['/juego', item.juego.id]" style="color: white; font-weight: 600; text-decoration: none;">
                    {{ item.juego.nombre }}
                  </a>
                </div>
                <span style="color: var(--muted);">S/ {{ item.precio | number:'1.2-2' }} c/u</span>
              </div>
            </div>
          </div>
        </div>
      </section>

    </main>
  `
})
export class CarritoComponent implements OnInit {
  private cartService = inject(CartService);
  private authService = inject(AuthService);

  public items = this.cartService.currentCartItems;
  public total = this.cartService.cartTotal;
  public user = this.authService.currentUser;
  
  public history = signal<any[]>([]);
  public checkoutSuccess = signal<boolean>(false);
  public errorMessage = signal<string>('');

  constructor() {}

  ngOnInit() {
    this.cartService.getCart().subscribe();
    this.loadHistory();
  }

  loadHistory() {
    this.authService.checkSession().subscribe(user => {
      if (user.authenticated) {
        this.cartService.getHistorial().subscribe(data => {
          this.history.set(data);
        });
      }
    });
  }

  incrementQuantity(juegoId: number) {
    this.cartService.addToCart(juegoId).subscribe();
  }

  decrementQuantity(juegoId: number) {
    this.cartService.removeFromCart(juegoId).subscribe();
  }

  deleteItem(juegoId: number) {
    this.cartService.deleteFromCart(juegoId).subscribe();
  }

  clearCart() {
    this.cartService.clearCart().subscribe();
  }

  processCheckout() {
    this.errorMessage.set('');
    this.cartService.checkout().subscribe({
      next: (res) => {
        this.checkoutSuccess.set(true);
        this.loadHistory(); // Reload orders history list
      },
      error: (err) => {
        this.errorMessage.set(err.error?.error || 'Ocurrió un error al procesar el checkout.');
      }
    });
  }

  resetCheckoutFeedback() {
    this.checkoutSuccess.set(false);
  }
}
