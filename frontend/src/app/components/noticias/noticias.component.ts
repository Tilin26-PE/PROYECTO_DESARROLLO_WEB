import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ContenidoService, Noticia, Guia } from '../../services/contenido.service';

@Component({
  selector: 'app-noticias',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Novedades y Guías</h2>
          <p>Mantente al día con los últimos anuncios de la industria y descubre guías útiles creadas por la comunidad.</p>
        </div>
      </section>

      <!-- Subtabs for News vs Guides -->
      <div class="tabs-nav" style="margin-top: 20px;">
        <button (click)="activeTab.set('noticias')" class="tab-btn" [class.active]="activeTab() === 'noticias'">📰 Últimas Noticias</button>
        <button (click)="activeTab.set('guias')" class="tab-btn" [class.active]="activeTab() === 'guias'">📖 Guías de la Comunidad</button>
      </div>

      <!-- 1. Noticias Tab -->
      <div *ngIf="activeTab() === 'noticias'" class="tab-content active">
        <div *ngIf="noticias().length === 0" class="empty-message">
          <p>No hay noticias publicadas en este momento.</p>
        </div>
        
        <div class="news-grid-page" *ngIf="noticias().length > 0">
          <a *ngFor="let n of noticias()" class="news-card" [routerLink]="['/noticia', n.id]">
            <div class="news-card-img-wrapper">
              <img [src]="n.imagenUrl" [alt]="n.titulo" class="news-card-img">
            </div>
            <div class="news-card-body">
              <span class="news-card-date">🕒 {{ n.fecha | date:'dd/MM/yyyy' }}</span>
              <h3 class="news-card-title">{{ n.titulo }}</h3>
              <p class="news-card-desc">{{ n.subtitulo }}</p>
              <span class="news-card-link">Leer artículo completo →</span>
            </div>
          </a>
        </div>
      </div>

      <!-- 2. Guias Tab -->
      <div *ngIf="activeTab() === 'guias'" class="tab-content active">
        <div *ngIf="guias().length === 0" class="empty-message">
          <p>No hay guías creadas por los usuarios en este momento.</p>
        </div>

        <div class="guides-grid-page" *ngIf="guias().length > 0">
          <a *ngFor="let g of guias()" class="guide-card" [routerLink]="['/guia', g.id]">
            <div class="guide-card-body">
              <div class="guide-card-header">
                <span class="guide-card-author">👤 {{ g.autor }}</span>
                <span class="guide-card-date">🕒 {{ g.fecha | date:'dd/MM/yyyy' }}</span>
              </div>
              <h3 class="guide-card-title">{{ g.titulo }}</h3>
              <span class="guide-card-link">Ver guía paso a paso →</span>
            </div>
          </a>
        </div>
      </div>
    </main>
  `,
  styles: [`
    .news-grid-page {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 24px;
      margin-top: 24px;
    }
    .news-card {
      background: rgba(255, 255, 255, 0.02);
      border: 1px solid var(--border);
      border-radius: 16px;
      overflow: hidden;
      text-decoration: none;
      display: flex;
      flex-direction: column;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      box-shadow: 0 4px 20px rgba(0,0,0,0.15);
    }
    .news-card:hover {
      transform: translateY(-5px);
      border-color: #7c5cff;
      background: rgba(255, 255, 255, 0.04);
      box-shadow: 0 12px 30px rgba(124, 92, 255, 0.15);
    }
    .news-card-img-wrapper {
      width: 100%;
      aspect-ratio: 16/10;
      overflow: hidden;
      position: relative;
      background: rgba(0, 0, 0, 0.2);
    }
    .news-card-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
    }
    .news-card:hover .news-card-img {
      transform: scale(1.04);
    }
    .news-card-body {
      padding: 20px;
      display: flex;
      flex-direction: column;
      flex-grow: 1;
    }
    .news-card-date {
      font-size: 0.75rem;
      color: var(--muted);
      margin-bottom: 8px;
      display: block;
    }
    .news-card-title {
      font-size: 1.1rem;
      font-weight: 700;
      color: white;
      margin: 0 0 10px 0;
      line-height: 1.4;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      overflow: hidden;
    }
    .news-card-desc {
      font-size: 0.88rem;
      color: var(--muted);
      margin: 0 0 16px 0;
      line-height: 1.5;
      display: -webkit-box;
      -webkit-line-clamp: 3;
      -webkit-box-orient: vertical;
      overflow: hidden;
      flex-grow: 1;
    }
    .news-card-link {
      font-size: 0.85rem;
      font-weight: 600;
      color: #7c5cff;
      display: inline-flex;
      align-items: center;
      transition: transform 0.2s ease;
    }
    .news-card:hover .news-card-link {
      transform: translateX(4px);
    }
    .guides-grid-page {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
      gap: 20px;
      margin-top: 24px;
    }
    .guide-card {
      background: rgba(255, 255, 255, 0.02);
      border: 1px solid var(--border);
      border-radius: 12px;
      text-decoration: none;
      display: flex;
      flex-direction: column;
      transition: all 0.3s ease;
      box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }
    .guide-card:hover {
      transform: translateY(-3px);
      border-color: #19d3ff;
      background: rgba(255, 255, 255, 0.04);
      box-shadow: 0 8px 24px rgba(25, 211, 255, 0.1);
    }
    .guide-card-body {
      padding: 20px;
      display: flex;
      flex-direction: column;
      height: 100%;
    }
    .guide-card-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      font-size: 0.75rem;
      color: var(--muted);
      margin-bottom: 12px;
    }
    .guide-card-author {
      font-weight: 600;
      color: var(--text);
    }
    .guide-card-title {
      font-size: 1rem;
      font-weight: 600;
      color: white;
      margin: 0 0 16px 0;
      line-height: 1.4;
      flex-grow: 1;
    }
    .guide-card-link {
      font-size: 0.82rem;
      font-weight: 600;
      color: #19d3ff;
    }
  `]
})
export class NoticiasComponent implements OnInit {
  private contenidoService = inject(ContenidoService);
  
  public activeTab = signal<string>('noticias');
  public noticias = signal<Noticia[]>([]);
  public guias = signal<Guia[]>([]);

  ngOnInit() {
    this.contenidoService.getNoticias().subscribe({
      next: (data) => this.noticias.set(data)
    });
    this.contenidoService.getGuias().subscribe({
      next: (data) => this.guias.set(data)
    });
  }
}
