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
          <a *ngFor="let n of noticias()" class="news-card-wide" [routerLink]="['/noticia', n.id]">
            <img [src]="n.imagenUrl" [alt]="n.titulo" class="news-card-img">
            <div class="news-card-body">
              <span class="news-card-date">🕒 {{ n.fecha | date:'dd MMM yyyy' }}</span>
              <h3>{{ n.titulo }}</h3>
              <p>{{ n.subtitulo }}</p>
              <span class="news-card-link" style="margin-top: 8px; display: inline-block;">Leer artículo completo →</span>
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
          <a *ngFor="let g of guias()" class="guide-card-wide" [routerLink]="['/guia', g.id]">
            <div class="guide-card-body">
              <span class="guide-card-author">👤 Escrito por: <strong>{{ g.autor }}</strong></span>
              <h3>{{ g.titulo }}</h3>
              <span class="guide-card-date">🕒 Publicado: {{ g.fecha | date:'dd MMM yyyy' }}</span>
              <span class="guide-card-link" style="margin-top: 12px; display: inline-block;">Ver guía paso a paso →</span>
            </div>
          </a>
        </div>
      </div>
    </main>
  `
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
