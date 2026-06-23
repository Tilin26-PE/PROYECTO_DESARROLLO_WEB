import { Component, OnInit, signal, effect } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { JuegoService, Juego } from '../../services/juego.service';

@Component({
  selector: 'app-comparar',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Comparador de Videojuegos</h2>
          <p>Selecciona dos juegos de nuestro catálogo para contrastar sus precios, calificaciones, plataformas y más.</p>
        </div>
      </section>

      <!-- Selectors Card -->
      <section class="search-filter-panel" style="margin-bottom: 24px;">
        <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
          <div>
            <label for="game1" style="display: block; margin-bottom: 8px; font-weight: 700; color: var(--accent-2);">Juego A</label>
            <select 
              id="game1" 
              [(ngModel)]="game1Id" 
              (change)="onSelectGame1()"
              style="width: 100%; padding: 12px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              <option [value]="null">-- Selecciona un juego --</option>
              <option *ngFor="let g of availableGames()" [value]="g.id">{{ g.nombre }}</option>
            </select>
          </div>
          
          <div>
            <label for="game2" style="display: block; margin-bottom: 8px; font-weight: 700; color: var(--accent);">Juego B</label>
            <select 
              id="game2" 
              [(ngModel)]="game2Id" 
              (change)="onSelectGame2()"
              style="width: 100%; padding: 12px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              <option [value]="null">-- Selecciona un juego --</option>
              <option *ngFor="let g of availableGames()" [value]="g.id">{{ g.nombre }}</option>
            </select>
          </div>
        </div>
      </section>

      <!-- Comparison Matrix -->
      <div *ngIf="!juego1() && !juego2()" class="empty-message">
        <p>Selecciona al menos un juego en los menús de arriba para iniciar la comparativa.</p>
      </div>

      <section *ngIf="juego1() || juego2()" class="tabs-container" style="padding: 0; overflow-x: auto; border-radius: 20px;">
        <table class="comparison-table" style="width: 100%; border-collapse: collapse; text-align: left;">
          <thead>
            <tr style="border-bottom: 1px solid var(--border); background: rgba(255,255,255,0.02);">
              <th style="padding: 16px; width: 20%; color: var(--muted); font-weight: 700;">Característica</th>
              <th style="padding: 16px; width: 40%; font-weight: 800; color: white;">
                <span *ngIf="juego1()">{{ juego1()?.nombre }}</span>
                <span *ngIf="!juego1()" style="color: var(--muted); font-weight: normal;">Juego A no seleccionado</span>
              </th>
              <th style="padding: 16px; width: 40%; font-weight: 800; color: white;">
                <span *ngIf="juego2()">{{ juego2()?.nombre }}</span>
                <span *ngIf="!juego2()" style="color: var(--muted); font-weight: normal;">Juego B no seleccionado</span>
              </th>
            </tr>
          </thead>
          <tbody>
            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Portada</td>
              <td style="padding: 16px;">
                <img *ngIf="juego1()" [src]="juego1()?.imagenUrl" [alt]="juego1()?.nombre" class="comparison-img" style="width: 100px; height: 130px; object-fit: cover; border-radius: 12px; border: 1px solid var(--border);">
              </td>
              <td style="padding: 16px;">
                <img *ngIf="juego2()" [src]="juego2()?.imagenUrl" [alt]="juego2()?.nombre" class="comparison-img" style="width: 100px; height: 130px; object-fit: cover; border-radius: 12px; border: 1px solid var(--border);">
              </td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Categoría</td>
              <td style="padding: 16px;">{{ juego1()?.categoria || '-' }}</td>
              <td style="padding: 16px;">{{ juego2()?.categoria || '-' }}</td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Plataforma</td>
              <td style="padding: 16px;">{{ juego1()?.plataforma || '-' }}</td>
              <td style="padding: 16px;">{{ juego2()?.plataforma || '-' }}</td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Lanzamiento</td>
              <td style="padding: 16px;">{{ (juego1()?.fechaLanzamiento | date:'dd/MM/yyyy') || '-' }}</td>
              <td style="padding: 16px;">{{ (juego2()?.fechaLanzamiento | date:'dd/MM/yyyy') || '-' }}</td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Precio Lista</td>
              <td style="padding: 16px;">
                <span *ngIf="juego1()">S/ {{ juego1()?.precio | number:'1.2-2' }}</span>
                <span *ngIf="!juego1()">-</span>
              </td>
              <td style="padding: 16px;">
                <span *ngIf="juego2()">S/ {{ juego2()?.precio | number:'1.2-2' }}</span>
                <span *ngIf="!juego2()">-</span>
              </td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Precio Final</td>
              <td style="padding: 16px;">
                <div *ngIf="juego1()">
                  <strong style="color: #10b981;">S/ {{ (juego1()?.precioConDescuento || juego1()?.precio) | number:'1.2-2' }}</strong>
                  <span *ngIf="juego1()?.descuento" style="margin-left: 6px; background: #ef4444; color: white; padding: 2px 6px; border-radius: 4px; font-size: 0.75rem; font-weight: bold;">
                    -{{ juego1()?.descuento }}%
                  </span>
                </div>
                <span *ngIf="!juego1()">-</span>
              </td>
              <td style="padding: 16px;">
                <div *ngIf="juego2()">
                  <strong style="color: #10b981;">S/ {{ (juego2()?.precioConDescuento || juego2()?.precio) | number:'1.2-2' }}</strong>
                  <span *ngIf="juego2()?.descuento" style="margin-left: 6px; background: #ef4444; color: white; padding: 2px 6px; border-radius: 4px; font-size: 0.75rem; font-weight: bold;">
                    -{{ juego2()?.descuento }}%
                  </span>
                </div>
                <span *ngIf="!juego2()">-</span>
              </td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Puntuación</td>
              <td style="padding: 16px;">
                <div *ngIf="juego1() && juego1()?.calificacionPromedio" style="display: inline-flex; align-items: center; gap: 6px; background: rgba(255, 184, 0, 0.15); border: 1px solid rgba(255, 184, 0, 0.3); padding: 4px 8px; border-radius: 8px; color: #ffb800; font-weight: bold;">
                  ⭐ {{ juego1()?.calificacionPromedio | number:'1.1-1' }}
                </div>
                <span *ngIf="juego1() && !juego1()?.calificacionPromedio" style="color: var(--muted);">Sin reseñas</span>
                <span *ngIf="!juego1()">-</span>
              </td>
              <td style="padding: 16px;">
                <div *ngIf="juego2() && juego2()?.calificacionPromedio" style="display: inline-flex; align-items: center; gap: 6px; background: rgba(255, 184, 0, 0.15); border: 1px solid rgba(255, 184, 0, 0.3); padding: 4px 8px; border-radius: 8px; color: #ffb800; font-weight: bold;">
                  ⭐ {{ juego2()?.calificacionPromedio | number:'1.1-1' }}
                </div>
                <span *ngIf="juego2() && !juego2()?.calificacionPromedio" style="color: var(--muted);">Sin reseñas</span>
                <span *ngIf="!juego2()">-</span>
              </td>
            </tr>

            <tr style="border-bottom: 1px solid var(--border);">
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Reseñas</td>
              <td style="padding: 16px;">{{ juego1()?.cantidadResenas || 0 }} opiniones</td>
              <td style="padding: 16px;">{{ juego2()?.cantidadResenas || 0 }} opiniones</td>
            </tr>

            <tr>
              <td style="padding: 16px; font-weight: 700; color: var(--muted);">Descripción</td>
              <td style="padding: 16px; line-height: 1.5; font-size: 0.9rem; color: var(--muted);">{{ juego1()?.descripcion || '-' }}</td>
              <td style="padding: 16px; line-height: 1.5; font-size: 0.9rem; color: var(--muted);">{{ juego2()?.descripcion || '-' }}</td>
            </tr>
          </tbody>
        </table>
      </section>
    </main>
  `
})
export class CompararComponent implements OnInit {
  public availableGames = signal<Juego[]>([]);
  public game1Id: number | null = null;
  public game2Id: number | null = null;

  public juego1 = signal<Juego | null>(null);
  public juego2 = signal<Juego | null>(null);

  constructor(private juegoService: JuegoService) {}

  ngOnInit() {
    this.juegoService.getJuegos().subscribe(data => {
      this.availableGames.set(data);
    });
  }

  onSelectGame1() {
    if (this.game1Id) {
      // Find game details from list, or hit the details API
      this.juegoService.getJuego(Number(this.game1Id)).subscribe(j => this.juego1.set(j));
    } else {
      this.juego1.set(null);
    }
  }

  onSelectGame2() {
    if (this.game2Id) {
      this.juegoService.getJuego(Number(this.game2Id)).subscribe(j => this.juego2.set(j));
    } else {
      this.juego2.set(null);
    }
  }
}
