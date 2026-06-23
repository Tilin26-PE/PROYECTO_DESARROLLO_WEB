import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JuegoService, Juego } from '../../services/juego.service';

@Component({
  selector: 'app-top-juegos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Leaderboard de Popularidad</h2>
          <p>Los videojuegos mejor valorados por nuestra comunidad gamer, ordenados por su puntuación promedio.</p>
        </div>
      </section>

      <!-- Leaderboard List -->
      <div *ngIf="juegos().length === 0" class="empty-message">
        <p>No hay suficientes reseñas para clasificar los juegos en este momento.</p>
      </div>

      <div class="leaderboard-list" style="display: flex; flex-direction: column; gap: 14px; margin-top: 20px;" *ngIf="juegos().length > 0">
        <div *ngFor="let juego of juegos(); let idx = index" 
             class="leaderboard-card" 
             style="display: grid; grid-template-columns: 80px 100px 1fr 180px 120px; align-items: center; padding: 20px; gap: 20px; border-radius: 20px; transition: transform 0.22s ease, border-color 0.22s ease;">
          
          <!-- Rank Badge -->
          <div style="display: flex; justify-content: center;">
            <div [class]="'rank-badge rank-' + (idx + 1 <= 3 ? (idx + 1) : 'other')">
              {{ idx + 1 }}
            </div>
          </div>

          <!-- Cover image -->
          <div>
            <img [src]="juego.imagenUrl" [alt]="juego.nombre" style="width: 80px; height: 100px; object-fit: cover; border-radius: 12px; border: 1px solid var(--border);">
          </div>

          <!-- Game details -->
          <div class="card-info-col">
            <a [routerLink]="['/juego', juego.id]" style="text-decoration: none;">
              <h3 style="margin: 0 0 6px 0; font-size: 1.25rem; font-weight: 700;">{{ juego.nombre }}</h3>
            </a>
            <span style="color: #9bc6ff; font-size: 0.82rem; text-transform: uppercase; letter-spacing: 0.08em;">
              {{ juego.categoria }}
            </span>
          </div>

          <!-- Average Score -->
          <div style="display: flex; flex-direction: column; gap: 4px;">
            <div *ngIf="juego.calificacionPromedio" style="display: inline-flex; align-items: center; gap: 6px; background: rgba(255, 184, 0, 0.15); border: 1px solid rgba(255, 184, 0, 0.3); padding: 6px 12px; border-radius: 8px; color: #ffb800; font-weight: bold; width: fit-content;">
              ⭐ {{ juego.calificacionPromedio | number:'1.1-1' }}
            </div>
            <span style="font-size: 0.88rem; color: var(--muted);" *ngIf="juego.cantidadResenas">
              Basado en {{ juego.cantidadResenas }} opiniones
            </span>
            <span style="font-size: 0.88rem; color: var(--muted);" *ngIf="!juego.cantidadResenas">
              Sin opiniones todavía
            </span>
          </div>

          <!-- Price & CTA -->
          <div style="display: flex; flex-direction: column; align-items: flex-end; gap: 6px;">
            <strong style="font-size: 1.15rem; color: white;">
              S/ {{ (juego.precioConDescuento || juego.precio) | number:'1.2-2' }}
            </strong>
            <a [routerLink]="['/juego', juego.id]" class="button-solid" style="padding: 8px 14px; font-size: 0.88rem; font-weight: 700; width: 100%; text-align: center;">
              Ver Ficha
            </a>
          </div>

        </div>
      </div>
    </main>
  `
})
export class TopJuegosComponent implements OnInit {
  public juegos = signal<Juego[]>([]);

  constructor(private juegoService: JuegoService) {}

  ngOnInit() {
    this.juegoService.getRanking().subscribe({
      next: (data) => this.juegos.set(data)
    });
  }
}
