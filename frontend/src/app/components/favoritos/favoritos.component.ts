import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FavoriteService } from '../../services/favorite.service';
import { CartService } from '../../services/cart.service';
import { Juego } from '../../services/juego.service';

@Component({
  selector: 'app-favoritos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Tu Lista de Deseos</h2>
          <p>Sigue la pista de tus juegos preferidos, entérate de sus ofertas de descuento y agrégalos al carrito.</p>
        </div>
      </section>

      <div *ngIf="favoritos().length === 0" class="empty-message" style="padding: 40px 0;">
        <p style="font-size: 1.25rem; color: var(--muted); margin-bottom: 20px;">No has agregado ningún videojuego a favoritos.</p>
        <a routerLink="/tienda" class="button-solid">Ver la Tienda</a>
      </div>

      <section class="games" *ngIf="favoritos().length > 0">
        <div *ngFor="let juego of favoritos()" class="game-card" style="display: flex; flex-direction: column; overflow: hidden; padding: 16px; position: relative;">
          
          <!-- Delete button directly in the card -->
          <button (click)="removeFavorite(juego.id)" 
                  style="position: absolute; top: 22px; right: 22px; z-index: 10; background: rgba(239, 68, 68, 0.9); border: none; border-radius: 50%; width: 36px; height: 36px; display: inline-flex; align-items: center; justify-content: center; cursor: pointer; color: white; box-shadow: 0 4px 10px rgba(0,0,0,0.3); font-weight: bold; transition: transform 0.2s ease;"
                  title="Eliminar de favoritos"
                  onmouseover="this.style.transform='scale(1.1)'"
                  onmouseout="this.style.transform='scale(1)'">
            ❌
          </button>

          <a [routerLink]="['/juego', juego.id]" style="text-decoration: none; color: inherit; display: block;">
            <div class="game-image">
              <img [src]="juego.imagenUrl" [alt]="juego.nombre">
              <span *ngIf="juego.descuento && juego.descuento > 0" class="game-badge" style="background: #ef4444; color: white; border: none; font-weight: 800; top: 12px; left: 12px;">
                OFERTA -{{ juego.descuento }}%
              </span>
            </div>
          </a>

          <div class="game-body" style="padding-top: 14px; flex-grow: 1; display: flex; flex-direction: column;">
            <span class="game-category">{{ juego.categoria }}</span>
            <a [routerLink]="['/juego', juego.id]" style="text-decoration: none; color: inherit;">
              <h3 style="margin: 0 0 6px 0; font-size: 1.15rem; color: white;">{{ juego.nombre }}</h3>
            </a>
            <p style="font-size: 0.82rem; color: var(--muted); margin: 0 0 10px 0;">Plataforma: {{ juego.plataforma }}</p>
            
            <div class="game-price-container" style="display: flex; align-items: center; gap: 8px; margin-bottom: 14px; min-height: 24px;">
              <span *ngIf="juego.descuento && juego.descuento > 0" 
                    style="text-decoration: line-through; color: var(--muted); font-size: 0.9rem;">
                S/ {{ juego.precio | number:'1.2-2' }}
              </span>
              <span class="game-price" style="margin: 0; font-weight: 700; color: white;">
                S/ {{ (juego.precioConDescuento || juego.precio) | number:'1.2-2' }}
              </span>
            </div>

            <!-- Action buttons inside cart/wishlist context -->
            <div style="display: flex; gap: 8px; margin-top: auto;">
              <button (click)="addToCart(juego.id)" class="button-solid" style="padding: 10px 14px; font-size: 0.88rem; flex: 1;">
                🛒 Al Carrito
              </button>
            </div>
            
            <p *ngIf="addedFeedbackMap[juego.id]" style="color: #10b981; font-size: 0.82rem; font-weight: bold; text-align: center; margin: 6px 0 0 0;">
              ✔ ¡Añadido con éxito!
            </p>
          </div>

        </div>
      </section>
    </main>
  `
})
export class FavoritosComponent implements OnInit {
  private favoriteService = inject(FavoriteService);
  private cartService = inject(CartService);

  public favoritos = this.favoriteService.favoritesList;
  public addedFeedbackMap: { [key: number]: boolean } = {};

  constructor() {}

  ngOnInit() {
    this.favoriteService.getFavorites().subscribe();
  }

  removeFavorite(juegoId: number) {
    this.favoriteService.toggleFavorite(juegoId).subscribe();
  }

  addToCart(juegoId: number) {
    this.cartService.addToCart(juegoId).subscribe({
      next: () => {
        this.addedFeedbackMap[juegoId] = true;
        setTimeout(() => {
          this.addedFeedbackMap[juegoId] = false;
        }, 3000);
      }
    });
  }
}
