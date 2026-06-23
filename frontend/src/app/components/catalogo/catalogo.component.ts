import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { JuegoService, Juego } from '../../services/juego.service';

@Component({
  selector: 'app-catalogo',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Buscador Avanzado</h2>
          <p>Filtra la base de datos de videojuegos por título, género/categoría, plataforma o año de lanzamiento.</p>
        </div>
      </section>

      <!-- Search and Filter Panel -->
      <section class="search-filter-panel">
        <form (ngSubmit)="onSearch()" class="admin-form" style="display: flex; flex-wrap: wrap; gap: 14px; align-items: flex-end;">
          
          <div style="flex: 2; min-width: 250px;">
            <label for="searchQuery" style="display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem;">Nombre del juego</label>
            <input 
              type="text" 
              id="searchQuery" 
              name="searchQuery" 
              [(ngModel)]="query" 
              placeholder="Ej. Grand Theft Auto, Metroid..." 
              class="search-input" 
              style="width: 100%; padding: 10px 14px; border-radius: 999px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
          </div>

          <div style="flex: 1; min-width: 150px;">
            <label for="searchCategory" style="display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem;">Categoría / Género</label>
            <select 
              id="searchCategory" 
              name="searchCategory" 
              [(ngModel)]="selectedCategory" 
              class="filter-select" 
              style="width: 100%; padding: 10px 14px; border-radius: 999px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              <option value="">Todas</option>
              <option *ngFor="let cat of categories()" [value]="cat">{{ cat }}</option>
            </select>
          </div>

          <div style="flex: 1; min-width: 150px;">
            <label for="searchPlatform" style="display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem;">Plataforma</label>
            <select 
              id="searchPlatform" 
              name="searchPlatform" 
              [(ngModel)]="selectedPlatform" 
              class="filter-select" 
              style="width: 100%; padding: 10px 14px; border-radius: 999px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              <option value="">Todas</option>
              <option *ngFor="let plat of platforms()" [value]="plat">{{ plat }}</option>
            </select>
          </div>

          <div style="flex: 1; min-width: 120px;">
            <label for="searchYear" style="display: block; margin-bottom: 6px; font-weight: 600; font-size: 0.9rem;">Año</label>
            <select 
              id="searchYear" 
              name="searchYear" 
              [(ngModel)]="selectedYear" 
              class="filter-select" 
              style="width: 100%; padding: 10px 14px; border-radius: 999px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              <option value="">Todos</option>
              <option *ngFor="let y of years()" [value]="y">{{ y }}</option>
            </select>
          </div>

          <div>
            <button type="submit" class="button-solid" style="height: 44px; padding: 0 24px;">Buscar 🔍</button>
          </div>

        </form>
      </section>

      <!-- Results Grid -->
      <div *ngIf="juegos().length === 0" class="empty-message" style="margin-top: 40px;">
        <p>No se encontraron resultados para la combinación de filtros ingresada.</p>
      </div>

      <section class="games" *ngIf="juegos().length > 0">
        <a *ngFor="let juego of juegos()" 
           class="game-card game-card-link" 
           [routerLink]="['/juego', juego.id]">
          <div class="game-image">
            <img [src]="juego.imagenUrl" [alt]="juego.nombre">
          </div>
          
          <div class="game-body">
            <span class="game-category">{{ juego.categoria }}</span>
            <h3>{{ juego.nombre }}</h3>
            <p style="font-size: 0.82rem; color: var(--muted); margin: 0;">Plataforma: {{ juego.plataforma }} ({{ juego.fechaLanzamiento | date:'yyyy' }})</p>
            
            <div class="game-price-container" style="display: flex; align-items: center; gap: 8px; margin: 8px 0 12px; min-height: 24px;">
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
            
            <span class="game-cta">Ver ficha</span>
          </div>
        </a>
      </section>
    </main>
  `
})
export class CatalogoComponent implements OnInit {
  private route = inject(ActivatedRoute);
  private juegoService = inject(JuegoService);

  public query = '';
  public selectedCategory = '';
  public selectedPlatform = '';
  public selectedYear: number | '' = '';

  public categories = signal<String[]>([]);
  public platforms = signal<String[]>([]);
  public years = signal<number[]>([]);
  public juegos = signal<Juego[]>([]);

  constructor() {}

  ngOnInit() {
    // Populate select lists from API
    this.juegoService.getCategorias().subscribe(data => this.categories.set(data));
    this.juegoService.getPlataformas().subscribe(data => this.platforms.set(data));
    this.juegoService.getAnios().subscribe(data => this.years.set(data));

    // Listen for query parameters from routing
    this.route.queryParams.subscribe(params => {
      if (params['q']) {
        this.query = params['q'];
      }
      this.onSearch();
    });
  }

  onSearch() {
    const yr = this.selectedYear === '' ? undefined : this.selectedYear;
    this.juegoService.buscarJuegos(
      this.query || undefined,
      this.selectedCategory || undefined,
      this.selectedPlatform || undefined,
      yr
    ).subscribe({
      next: (data) => this.juegos.set(data)
    });
  }
}
