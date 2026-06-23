import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JuegoService, Juego } from '../../services/juego.service';

@Component({
  selector: 'app-lanzamientos',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Calendario de Lanzamientos</h2>
          <p>Mantente al día con los videojuegos estrenados recientemente y los títulos más esperados del año.</p>
        </div>
      </section>

      <!-- Upcoming Section Header -->
      <h3 class="timeline-section-header">
        🚀 Próximos Lanzamientos
      </h3>

      <div *ngIf="upcoming().length === 0" class="empty-message">
        <p>No hay próximos lanzamientos programados para los siguientes meses.</p>
      </div>

      <div class="timeline-container" *ngIf="upcoming().length > 0">
        <div *ngFor="let juego of upcoming()" class="timeline-node">
          <!-- Marker dot -->
          <div class="timeline-marker"></div>
          
          <!-- Content Card -->
          <div class="timeline-content-card">
            <div>
              <img [src]="juego.imagenUrl" [alt]="juego.nombre">
            </div>
            
            <div class="timeline-game-info">
              <span class="timeline-date badge-upcoming">
                📅 {{ juego.fechaLanzamiento | date:'dd/MM/yyyy' }}
              </span>
              <h3>{{ juego.nombre }}</h3>
              <p>{{ juego.descripcion }}</p>
              <span style="color: #9bc6ff; font-size: 0.82rem; text-transform: uppercase; font-weight: bold; letter-spacing: 0.08em;">
                {{ juego.categoria }} | {{ juego.plataforma }}
              </span>
            </div>

            <div class="timeline-actions-col">
              <span style="font-weight: 700; color: var(--accent-3);">PRÓXIMAMENTE</span>
              <a [routerLink]="['/juego', juego.id]" class="button-solid" style="padding: 8px 14px; font-size: 0.88rem;">
                Ver Detalles
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- Recent/Past Releases Section Header -->
      <h3 class="timeline-section-header" style="margin-top: 50px;">
        🎮 Estrenos Recientes
      </h3>

      <div *ngIf="recent().length === 0" class="empty-message">
        <p>No se registran lanzamientos recientes en la base de datos.</p>
      </div>

      <div class="timeline-container" *ngIf="recent().length > 0">
        <div *ngFor="let juego of recent()" class="timeline-node">
          <!-- Marker dot -->
          <div class="timeline-marker" style="border-color: var(--accent-2);"></div>
          
          <!-- Content Card -->
          <div class="timeline-content-card">
            <div>
              <img [src]="juego.imagenUrl" [alt]="juego.nombre">
            </div>
            
            <div class="timeline-game-info">
              <span class="timeline-date">
                ✔ Estrenado el {{ juego.fechaLanzamiento | date:'dd/MM/yyyy' }}
              </span>
              <h3>{{ juego.nombre }}</h3>
              <p>{{ juego.descripcion }}</p>
              <span style="color: #9bc6ff; font-size: 0.82rem; text-transform: uppercase; font-weight: bold; letter-spacing: 0.08em;">
                {{ juego.categoria }} | {{ juego.plataforma }}
              </span>
            </div>

            <div class="timeline-actions-col">
              <strong style="color: white; font-size: 1.1rem; text-align: right; display: block; margin-bottom: 4px;">
                S/ {{ (juego.precioConDescuento || juego.precio) | number:'1.2-2' }}
              </strong>
              <a [routerLink]="['/juego', juego.id]" class="button-solid" style="padding: 8px 14px; font-size: 0.88rem;">
                Comprar Ahora
              </a>
            </div>
          </div>
        </div>
      </div>
    </main>
  `
})
export class LanzamientosComponent implements OnInit {
  public recent = signal<Juego[]>([]);
  public upcoming = signal<Juego[]>([]);

  constructor(private juegoService: JuegoService) {}

  ngOnInit() {
    this.juegoService.getRecientes().subscribe({
      next: (data) => this.recent.set(data)
    });

    this.juegoService.getProximos().subscribe({
      next: (data) => this.upcoming.set(data)
    });
  }
}
