import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { JuegoService, Juego } from '../../services/juego.service';
import { ContenidoService, Noticia } from '../../services/contenido.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      
      <!-- Hero Banner Replica -->
      <section class="hero-mock" style="background-image: linear-gradient(to right, rgba(5, 5, 8, 0.95) 30%, rgba(5, 5, 8, 0.25) 100%), url('/imgs/infinite-frontiers.png');">
        <div class="hero-mock-content">
          <span class="hero-mock-subtitle">Lanzamiento Destacado</span>
          <h2 class="hero-mock-title">INFINITE FRONTIERS</h2>
          <p class="hero-mock-desc">
            Explora lo desconocido. Sobrevive. El futuro de la humanidad está en tus manos. 
            Prepárate para una aventura de rol y exploración espacial sin precedentes.
          </p>
          <div class="hero-mock-actions">
            <a class="button-solid btn-violet" routerLink="/tienda">Ver Más</a>
            <a class="button-ghost btn-outline" routerLink="/tienda">Comprar Ahora</a>
          </div>
        </div>
        
        <!-- Controls overlay matching mock indicators -->
        <div class="hero-mock-controls">
          <div class="hero-mock-dots">
            <span class="dot active"></span>
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
          <div class="hero-mock-arrows">
            <button class="arrow-btn" aria-label="Anterior">&lt;</button>
            <button class="arrow-btn" aria-label="Siguiente">&gt;</button>
          </div>
        </div>
      </section>

      <!-- Two Columns Content -->
      <div class="home-columns">
        
        <!-- Left Column: Featured Games -->
        <div class="column-left">
          <div class="column-header">
            <div class="section-title-wrapper">
              <span class="title-indicator"></span>
              <h2 class="column-title">Juegos Destacados</h2>
            </div>
            <a class="view-all-link" routerLink="/tienda">Ver Todos <span class="arrow">→</span></a>
          </div>

          <!-- Games Compact Grid -->
          <div class="games-grid-layout">
            <a *ngFor="let juego of juegos().slice(0, 4)" 
               class="game-compact-card" 
               [routerLink]="['/juego', juego.id]">
              <div class="card-img-wrapper">
                <img [src]="juego.imagenUrl" [alt]="juego.nombre">
                <span *ngIf="juego.descuento && juego.descuento > 0" class="discount-tag">-{{ juego.descuento }}%</span>
              </div>
              <div class="card-info">
                <h3>{{ juego.nombre }}</h3>
                <div class="card-meta">
                  <span class="card-category">{{ juego.categoria }}</span>
                  <span class="card-price">S/ {{ (juego.precioConDescuento || juego.precio) | number:'1.2-2' }}</span>
                </div>
              </div>
            </a>
          </div>
        </div>

        <!-- Right Column: Latest News -->
        <div class="column-right">
          <div class="column-header">
            <div class="section-title-wrapper">
              <span class="title-indicator"></span>
              <h2 class="column-title">Últimas Noticias</h2>
            </div>
          </div>

          <!-- Dynamic News List with High Fidelity Fallbacks -->
          <div class="news-list-layout">
            <ng-container *ngIf="noticias().length > 0; else fallbackNews">
              <div *ngFor="let noticia of noticias().slice(0, 3)" class="news-row-item">
                <img [src]="noticia.imagenUrl" [alt]="noticia.titulo" class="news-thumb">
                <div class="news-item-info">
                  <h3>{{ noticia.titulo }}</h3>
                  <span class="news-time">Hace unas horas</span>
                </div>
              </div>
            </ng-container>
            
            <ng-template #fallbackNews>
              <div class="news-row-item">
                <img src="/imgs/fifa26.jpg" alt="Xbox Showcase" class="news-thumb">
                <div class="news-item-info">
                  <h3>Xbox Showcase 2024: Todos los anuncios y juegos presentados</h3>
                  <span class="news-time">Hace 2 horas</span>
                </div>
              </div>
              <div class="news-row-item">
                <img src="/imgs/hogwarts.jpg" alt="Elden Ring DLC" class="news-thumb">
                <div class="news-item-info">
                  <h3>El nuevo DLC de Elden Ring ya tiene fecha de lanzamiento oficial</h3>
                  <span class="news-time">Hace 5 horas</span>
                </div>
              </div>
              <div class="news-row-item">
                <img src="/imgs/god-of-war.jpg" alt="PlayStation 5 Pro" class="news-thumb">
                <div class="news-item-info">
                  <h3>PlayStation 5 Pro: Especificaciones oficiales y mejoras técnicas</h3>
                  <span class="news-time">Hace 1 día</span>
                </div>
              </div>
            </ng-template>
          </div>

          <!-- Join Community Box -->
          <a class="community-banner-card" href="https://discord.com" target="_blank">
            <div class="community-icon-wrapper">
              <svg viewBox="0 0 24 24" width="20" height="20" fill="currentColor">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm1 17.93c-3.95-.49-7-3.85-7-7.93 0-.62.08-1.21.21-1.79L9 15v1c0 1.1.9 2 2 2v1.93zm6.9-2.54c-.26-.81-1-1.39-1.9-1.39h-1v-3c0-.55-.45-1-1-1H8v-2h2c.55 0 1-.45 1-1V7h2c1.1 0 2-.9 2-2v-.41c2.93 1.19 5 4.06 5 7.41 0 2.08-.8 3.97-2.1 5.39z"/>
              </svg>
            </div>
            <div class="community-copy">
              <h4>Únete a Nuestra Comunidad</h4>
              <p>Comparte, conoce y juega con millones de jugadores.</p>
            </div>
            <span class="arrow-indicator">→</span>
          </a>
        </div>
      </div>

      <!-- Features Bar Replica -->
      <section class="features-bar">
        <div class="feature-item">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feature-icon-svg">
            <polygon points="13 2 3 14 12 14 11 22 21 10 12 10 13 2"/>
          </svg>
          <div class="feature-text">
            <strong>Entrega Instantánea</strong>
            <span>Juega al instante sin esperas.</span>
          </div>
        </div>
        
        <div class="feature-item">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feature-icon-svg">
            <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"/>
            <path d="m9 11 2 2 4-4"/>
          </svg>
          <div class="feature-text">
            <strong>Pagos Seguros</strong>
            <span>Tus compras están protegidas.</span>
          </div>
        </div>
        
        <div class="feature-item">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feature-icon-svg">
            <path d="M12 2H2v10l9.29 9.29c.94.94 2.48.94 3.42 0l5.58-5.58c.94-.94.94-2.48 0-3.42L12 2z"/>
            <line x1="7" y1="7" x2="7.01" y2="7"/>
          </svg>
          <div class="feature-text">
            <strong>Mejores Ofertas</strong>
            <span>Descuentos exclusivos cada semana.</span>
          </div>
        </div>
        
        <div class="feature-item">
          <svg viewBox="0 0 24 24" width="28" height="28" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" class="feature-icon-svg">
            <path d="M3 18v-6a9 9 0 0 1 18 0v6"/>
            <path d="M21 19a2 2 0 0 1-2 2h-1a2 2 0 0 1-2-2v-3a2 2 0 0 1 2-2h3zM3 19a2 2 0 0 0 2 2h1a2 2 0 0 0 2-2v-3a2 2 0 0 0-2-2H3z"/>
          </svg>
          <div class="feature-text">
            <strong>Soporte 24/7</strong>
            <span>Estamos aquí para ayudarte.</span>
          </div>
        </div>
      </section>

    </main>
  `
})
export class HomeComponent implements OnInit {
  private juegoService = inject(JuegoService);
  private contenidoService = inject(ContenidoService);

  public juegos = signal<Juego[]>([]);
  public noticias = signal<Noticia[]>([]);

  ngOnInit() {
    // Load games catalog
    this.juegoService.getJuegos().subscribe({
      next: (data) => {
        this.juegos.set(data);
      }
    });

    // Load dynamic news
    this.contenidoService.getNoticias().subscribe({
      next: (data) => {
        this.noticias.set(data);
      }
    });
  }
}
