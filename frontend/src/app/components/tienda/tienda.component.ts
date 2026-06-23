import { Component, OnInit, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JuegoService, Juego } from '../../services/juego.service';

@Component({
  selector: 'app-tienda',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title" style="margin-bottom: 8px;">Tienda Virtual</h2>
          <p>Encuentra tus videojuegos favoritos al mejor precio y activa notificaciones para enterarte de descuentos.</p>
        </div>
      </section>

      <!-- Category Filter Pills -->
      <section class="store-toolbar" style="margin-top: 10px;">
        <div class="category-bar">
          <button 
            (click)="selectCategory('TODOS')" 
            class="category-pill" 
            [class.active]="selectedCategory() === 'TODOS'">
            Todos
          </button>
          <button 
            *ngFor="let cat of categories()" 
            (click)="selectCategory(cat)" 
            class="category-pill" 
            [class.active]="selectedCategory() === cat">
            {{ cat }}
          </button>
        </div>
      </section>

      <!-- Games Grid -->
      <div *ngIf="filteredJuegos().length === 0" class="empty-message">
        <p>No se encontraron juegos para la categoría seleccionada.</p>
      </div>

      <section class="games" *ngIf="filteredJuegos().length > 0">
        <a *ngFor="let juego of filteredJuegos()" 
           class="game-card game-card-link" 
           [routerLink]="['/juego', juego.id]">
          <div class="game-image">
            <img [src]="juego.imagenUrl" [alt]="juego.nombre">
            <span *ngIf="juego.descuento && juego.descuento > 0" class="game-badge" style="background: #ef4444; color: white; border: none; font-weight: 800;">
              OFERTA
            </span>
          </div>
          
          <div class="game-body">
            <span class="game-category">{{ juego.categoria }}</span>
            <h3>{{ juego.nombre }}</h3>
            
            <div class="game-price-container" style="display: flex; align-items: center; gap: 8px; margin-bottom: 12px; min-height: 24px;">
              <span *ngIf="juego.descuento && juego.descuento > 0" 
                    style="text-decoration: line-through; color: var(--muted); font-size: 0.9rem;">
                S/ {{ juego.precio | number:'1.2-2' }}
              </span>
              <span class="game-price" style="margin: 0; font-weight: 700;">
                S/ {{ (juego.precioConDescuento || juego.precio) | number:'1.2-2' }}
              </span>
              <span *ngIf="juego.descuento && juego.descuento > 0" 
                    style="background: #ef4444; color: white; padding: 2px 6px; border-radius: 4px; font-size: 0.78rem; font-weight: 700;">
                -{{ juego.descuento }}%
              </span>
            </div>
            
            <span class="game-cta">Ver detalles</span>
          </div>
        </a>
      </section>
    </main>
  `
})
export class TiendaComponent implements OnInit {
  public juegos = signal<Juego[]>([]);
  public categories = signal<String[]>([]);
  public selectedCategory = signal<string>('TODOS');

  // Reactively compute filtered games list
  public filteredJuegos = computed(() => {
    const list = this.juegos();
    const cat = this.selectedCategory();
    if (cat === 'TODOS') {
      return list;
    }
    return list.filter(j => j.categoria?.toUpperCase() === cat.toUpperCase());
  });

  constructor(private juegoService: JuegoService) {}

  ngOnInit() {
    // Load all games
    this.juegoService.getJuegos().subscribe({
      next: (data) => this.juegos.set(data)
    });

    // Load active categories list
    this.juegoService.getCategorias().subscribe({
      next: (data) => this.categories.set(data)
    });
  }

  selectCategory(category: any) {
    this.selectedCategory.set(category.toString());
  }
}
