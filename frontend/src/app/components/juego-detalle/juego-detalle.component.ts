import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { JuegoService, Juego } from '../../services/juego.service';
import { ContenidoService, Noticia, Guia, Truco } from '../../services/contenido.service';
import { ReviewService, Resena } from '../../services/review.service';
import { CartService } from '../../services/cart.service';
import { FavoriteService } from '../../services/favorite.service';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-juego-detalle',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  template: `
    <main class="page-shell" *ngIf="juego()">
      <!-- Game main details layout -->
      <section class="detail-layout">
        
        <!-- Column 1: Media (Image or Video) -->
        <div class="detail-media" style="display: flex; flex-direction: column; gap: 16px;">
          <div *ngIf="!juego()?.videoUrl" class="detail-poster">
            <img [src]="juego()?.imagenUrl" [alt]="juego()?.nombre">
          </div>
          
          <div *ngIf="juego()?.videoUrl" class="detail-video">
            <iframe 
              [src]="safeVideoUrl()" 
              title="YouTube video player" 
              allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture" 
              allowfullscreen>
            </iframe>
          </div>
          
          <!-- Image backup if video is played -->
          <div *ngIf="juego()?.videoUrl" style="display: flex; gap: 10px; align-items: center;">
            <img [src]="juego()?.imagenUrl" [alt]="juego()?.nombre" style="width: 60px; height: 80px; object-fit: cover; border-radius: 8px; border: 1px solid var(--border);">
            <div>
              <strong style="font-size: 0.95rem; color: white;">Tráiler oficial de juego</strong>
              <p style="margin: 2px 0 0 0; font-size: 0.82rem; color: var(--muted);">{{ juego()?.nombre }} - Ver gameplay</p>
            </div>
          </div>
        </div>

        <!-- Column 2: Buy & Description Panel -->
        <div class="detail-panel">
          <div class="detail-meta">
            <span class="chip" style="background: rgba(124, 92, 255, 0.12); border-color: rgba(124, 92, 255, 0.25); color: #a78bfa; font-weight: bold; text-transform: uppercase;">
              {{ juego()?.categoria }}
            </span>
            <span class="chip">🎮 {{ juego()?.plataforma }}</span>
            <span class="chip">📅 {{ juego()?.fechaLanzamiento | date:'dd/MM/yyyy' }}</span>
          </div>

          <h2 class="detail-title" style="color: white; font-weight: 800; font-size: clamp(2rem, 3.5vw, 3rem);">
            {{ juego()?.nombre }}
          </h2>

          <div style="display: flex; align-items: center; gap: 12px;">
            <div *ngIf="juego()?.calificacionPromedio" style="display: inline-flex; align-items: center; gap: 6px; background: rgba(255, 184, 0, 0.15); border: 1px solid rgba(255, 184, 0, 0.3); padding: 6px 12px; border-radius: 8px; color: #ffb800; font-weight: bold;">
              ⭐ {{ juego()?.calificacionPromedio | number:'1.1-1' }}
            </div>
            <span style="color: var(--muted); font-size: 0.88rem;" *ngIf="juego()?.cantidadResenas">
              ({{ juego()?.cantidadResenas }} reseñas de la comunidad)
            </span>
            <span style="color: var(--muted); font-size: 0.88rem;" *ngIf="!juego()?.cantidadResenas">
              (Sin valoraciones todavía)
            </span>
          </div>

          <p class="game-summary" style="line-height: 1.7; font-size: 1.05rem;">
            {{ juego()?.descripcion }}
          </p>

          <!-- Buy Section -->
          <div style="background: var(--bg-soft); border: 1px solid var(--border); padding: 24px; border-radius: 20px; margin-top: 10px; display: flex; flex-direction: column; gap: 16px;">
            <div style="display: flex; align-items: baseline; gap: 12px;">
              <p class="detail-price" style="margin: 0; font-size: 2.2rem; font-weight: 900; color: white;">
                S/ {{ (juego()?.precioConDescuento || juego()?.precio) | number:'1.2-2' }}
              </p>
              <span *ngIf="juego()?.descuento && juego()?.descuento! > 0" 
                    style="text-decoration: line-through; color: var(--muted); font-size: 1.1rem;">
                S/ {{ juego()?.precio | number:'1.2-2' }}
              </span>
              <span *ngIf="juego()?.descuento && juego()?.descuento! > 0" 
                    style="background: #ef4444; color: white; padding: 4px 8px; border-radius: 6px; font-weight: bold; font-size: 0.88rem;">
                -{{ juego()?.descuento }}%
              </span>
            </div>

            <div style="display: flex; gap: 12px; flex-wrap: wrap;">
              <!-- Add to Cart Button -->
              <button (click)="addToCart()" class="button-solid" style="flex: 1; min-width: 150px; font-size: 1rem; padding: 14px 20px;">
                🛒 Añadir al Carrito
              </button>

              <!-- Favorite Toggle Button -->
              <button (click)="toggleFavorite()" class="button-ghost" [style.color]="isFavorite() ? '#ef4444' : 'var(--text)'" style="display: inline-flex; align-items: center; gap: 8px; padding: 14px 20px; font-weight: bold;">
                {{ isFavorite() ? '❤️ En Favoritos' : '🤍 Guardar en Deseos' }}
              </button>
            </div>
            
            <p *ngIf="cartFeedback()" style="margin: 0; color: #10b981; font-weight: bold; font-size: 0.88rem; text-align: center;">
              ✔ ¡Añadido al carrito con éxito!
            </p>
          </div>

        </div>
      </section>

      <!-- Interactive Tabs section -->
      <section class="tabs-container">
        <div class="tabs-nav">
          <button (click)="setActiveTab('specs')" class="tab-btn" [class.active]="activeTab() === 'specs'">📋 Requisitos</button>
          <button (click)="setActiveTab('dlcs')" class="tab-btn" [class.active]="activeTab() === 'dlcs'">🔌 DLCs & Expansiones</button>
          <button (click)="setActiveTab('news')" class="tab-btn" [class.active]="activeTab() === 'news'">📰 Noticias</button>
          <button (click)="setActiveTab('guides')" class="tab-btn" [class.active]="activeTab() === 'guides'">📚 Guías & Tutoriales</button>
          <button (click)="setActiveTab('cheats')" class="tab-btn" [class.active]="activeTab() === 'cheats'">🔑 Trucos & Secretos</button>
        </div>

        <!-- Specifications Tab -->
        <div class="tab-content" [class.active]="activeTab() === 'specs'">
          <div class="requirements-grid" *ngIf="juego()?.requisitosMinimos || juego()?.requisitosRecomendados; else noSpecs">
            <div class="requirements-card">
              <h4>Requisitos Mínimos</h4>
              <p>{{ juego()?.requisitosMinimos || 'No especificados.' }}</p>
            </div>
            <div class="requirements-card">
              <h4>Requisitos Recomendados</h4>
              <p>{{ juego()?.requisitosRecomendados || 'No especificados.' }}</p>
            </div>
          </div>
          <ng-template #noSpecs>
            <p class="empty-message">No se han registrado requisitos de sistema para este juego.</p>
          </ng-template>
        </div>

        <!-- DLCs Tab -->
        <div class="tab-content" [class.active]="activeTab() === 'dlcs'">
          <div *ngIf="dlcs().length === 0" class="empty-message">
            <p>No hay DLCs o expansiones registradas para este videojuego.</p>
          </div>
          <div class="dlc-grid" *ngIf="dlcs().length > 0">
            <div *ngFor="let dlc of dlcs()" class="dlc-card">
              <h4>{{ dlc.nombre }}</h4>
              <p>{{ dlc.descripcion }}</p>
              <div class="dlc-footer">
                <span class="dlc-price">S/ {{ dlc.precio | number:'1.2-2' }}</span>
                <button (click)="addDlcToCart(dlc.id)" class="button-solid" style="padding: 6px 12px; font-size: 0.82rem;">+ Carrito</button>
              </div>
            </div>
          </div>
        </div>

        <!-- News Tab -->
        <div class="tab-content" [class.active]="activeTab() === 'news'">
          <div *ngIf="noticias().length === 0" class="empty-message">
            <p>No se reportan noticias de actualidad para este videojuego.</p>
          </div>
          <div class="extra-list" *ngIf="noticias().length > 0">
            <div *ngFor="let post of noticias()" class="extra-item-card">
              <div class="extra-item-content">
                <h4>{{ post.titulo }}</h4>
                <p>{{ post.subtitulo }}</p>
                <span style="font-size: 0.78rem; color: var(--muted);">Publicado el {{ post.fecha | date:'dd/MM/yyyy' }}</span>
              </div>
              <div style="line-height: 1.6; color: var(--text); font-size: 0.9rem; margin-top: 8px;">
                {{ post.contenido }}
              </div>
            </div>
          </div>
        </div>

        <!-- Guides Tab -->
        <div class="tab-content" [class.active]="activeTab() === 'guides'">
          <div *ngIf="guias().length === 0" class="empty-message">
            <p>No se han publicado guías o tutoriales para este título todavía.</p>
          </div>
          <div class="extra-list" *ngIf="guias().length > 0">
            <div *ngFor="let guide of guias()" class="extra-item-card" style="align-items: flex-start; flex-direction: column;">
              <div class="extra-item-content" style="border-bottom: 1px solid var(--border); width: 100%; padding-bottom: 8px; margin-bottom: 8px;">
                <h4>{{ guide.titulo }}</h4>
                <span style="font-size: 0.78rem; color: var(--muted);">Por {{ guide.autor }} | {{ guide.fecha | date:'dd/MM/yyyy' }}</span>
              </div>
              <div style="line-height: 1.6; color: var(--text); font-size: 0.9rem; white-space: pre-line;">
                {{ guide.contenido }}
              </div>
            </div>
          </div>
        </div>

        <!-- Cheats Tab -->
        <div class="tab-content" [class.active]="activeTab() === 'cheats'">
          <div *ngIf="trucos().length === 0" class="empty-message">
            <p>Ningún truco o secreto registrado. ¡Juega limpio o descúbrelos tú mismo!</p>
          </div>
          <div class="extra-list" *ngIf="trucos().length > 0">
            <div *ngFor="let hack of trucos()" class="extra-item-card" style="flex-direction: column; align-items: flex-start;">
              <h4 style="color: var(--accent-2);">🔑 {{ hack.titulo }}</h4>
              <p style="margin-top: 6px; font-size: 0.92rem; line-height: 1.6; color: var(--text);">
                {{ hack.contenido }}
              </p>
            </div>
          </div>
        </div>
      </section>

      <!-- Review Section -->
      <section class="detail-section" style="margin-top: 40px;">
        <h3 class="timeline-section-header" style="margin-bottom: 20px;">Opiniones de la Comunidad</h3>

        <div class="rating-overview">
          <div class="rating-big-score">
            <h3>{{ juego()?.calificacionPromedio ? (juego()?.calificacionPromedio | number:'1.1-1') : '0.0' }}</h3>
            <div class="rating-stars">
              ⭐⭐⭐⭐⭐
            </div>
            <p>Puntuación general</p>
          </div>
          <div>
            <strong style="color: white; font-size: 1.15rem;">¿Qué te pareció el juego?</strong>
            <p style="color: var(--muted); margin: 4px 0 0 0;">Comparte tu valoración y reseña para ayudar a otros miembros del foro.</p>
          </div>
        </div>

        <!-- Add Review Form (Only visible if logged in) -->
        <div *ngIf="user().authenticated; else loginPrompt" class="tabs-container" style="margin-bottom: 30px;">
          <h4 style="margin: 0 0 16px 0; color: white;">Escribir una Reseña</h4>
          <form (ngSubmit)="submitReview()" class="admin-form" style="display: flex; flex-direction: column; gap: 14px;">
            <div>
              <label for="ratingVal" style="display: block; margin-bottom: 6px; font-weight: 600;">Calificación (1 a 5 estrellas)</label>
              <select 
                id="ratingVal" 
                name="ratingVal" 
                [(ngModel)]="newRating"
                style="padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); width: 120px;">
                <option [value]="5">⭐⭐⭐⭐⭐ (5)</option>
                <option [value]="4">⭐⭐⭐⭐ (4)</option>
                <option [value]="3">⭐⭐⭐ (3)</option>
                <option [value]="2">⭐⭐ (2)</option>
                <option [value]="1">⭐ (1)</option>
              </select>
            </div>
            
            <div>
              <label for="reviewComment" style="display: block; margin-bottom: 6px; font-weight: 600;">Comentario</label>
              <textarea 
                id="reviewComment" 
                name="reviewComment" 
                [(ngModel)]="newComment" 
                required 
                placeholder="Escribe aquí tu reseña sobre la jugabilidad, historia, gráficos..." 
                rows="4"
                style="width: 100%; padding: 12px; border-radius: 12px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
            </div>

            <div>
              <button type="submit" class="button-solid" style="padding: 10px 20px;">Publicar Reseña</button>
            </div>
          </form>
          <p *ngIf="reviewFeedback()" style="color: #10b981; margin: 10px 0 0 0; font-weight: bold;">
            ✔ ¡Reseña publicada con éxito!
          </p>
        </div>
        
        <ng-template #loginPrompt>
          <div style="background: rgba(255,255,255,0.02); border: 1px dashed var(--border); border-radius: 18px; padding: 24px; text-align: center; margin-bottom: 30px;">
            <p style="margin: 0; color: var(--muted);">
              Debes <a routerLink="/login" style="color: var(--accent-2); font-weight: 700; text-decoration: none;">iniciar sesión</a> para redactar y subir valoraciones.
            </p>
          </div>
        </ng-template>

        <!-- Reviews List -->
        <div *ngIf="resenas().length === 0" class="empty-message">
          <p>Aún no hay opiniones de este juego. ¡Sé el primero en dejar una!</p>
        </div>

        <div class="extra-list" *ngIf="resenas().length > 0">
          <div *ngFor="let rev of resenas()" class="requirements-card" style="padding: 18px;">
            <div style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 10px; margin-bottom: 10px;">
              <div>
                <strong style="color: white; font-size: 1.05rem;">👤 {{ rev.autor }}</strong>
                <span style="font-size: 0.8rem; color: var(--muted); margin-left: 10px;">@{{ rev.usuarioLogin }}</span>
              </div>
              <div style="color: #ffb800; font-weight: bold;">
                <span *ngFor="let star of [].constructor(rev.calificacion)">⭐</span>
              </div>
            </div>
            <p style="margin: 0; line-height: 1.6; color: var(--text); font-size: 0.95rem;">
              {{ rev.comentario }}
            </p>
          </div>
        </div>

      </section>

    </main>
  `
})
export class JuegoDetalleComponent implements OnInit {
  public juego = signal<Juego | null>(null);
  private authService = inject(AuthService);

  public dlcs = signal<Juego[]>([]);
  public noticias = signal<Noticia[]>([]);
  public guias = signal<Guia[]>([]);
  public trucos = signal<Truco[]>([]);
  public resenas = signal<Resena[]>([]);

  public activeTab = signal<string>('specs');
  public isFavorite = signal<boolean>(false);
  public user = this.authService.currentUser;

  // Form fields for reviews
  public newRating = 5;
  public newComment = '';
  
  // Feedback messages
  public cartFeedback = signal<boolean>(false);
  public reviewFeedback = signal<boolean>(false);

  constructor(
    private route: ActivatedRoute,
    private juegoService: JuegoService,
    private contenidoService: ContenidoService,
    private reviewService: ReviewService,
    private cartService: CartService,
    private favoriteService: FavoriteService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit() {
    this.route.paramMap.subscribe(params => {
      const id = Number(params.get('id'));
      if (id) {
        this.loadGameDetails(id);
      }
    });
  }

  loadGameDetails(id: number) {
    // 1. Fetch main game details
    this.juegoService.getJuego(id).subscribe({
      next: (data) => this.juego.set(data)
    });

    // 2. Fetch DLCs list
    this.juegoService.getDlcs(id).subscribe({
      next: (data) => this.dlcs.set(data)
    });

    // 3. Fetch news list
    this.contenidoService.getNoticias(id).subscribe({
      next: (data) => this.noticias.set(data)
    });

    // 4. Fetch guides list
    this.contenidoService.getGuias(id).subscribe({
      next: (data) => this.guias.set(data)
    });

    // 5. Fetch cheats list
    this.contenidoService.getTrucos(id).subscribe({
      next: (data) => this.trucos.set(data)
    });

    // 6. Fetch reviews list
    this.reviewService.getReviews(id).subscribe({
      next: (data) => this.resenas.set(data)
    });

    // 7. Check favorite status
    this.favoriteService.checkFavoriteStatus(id).subscribe({
      next: (res) => this.isFavorite.set(res.favorito)
    });
  }

  safeVideoUrl(): SafeResourceUrl {
    const url = this.juego()?.videoUrl || '';
    // Adapt standard Youtube URL to embed URL if necessary
    let embedUrl = url;
    if (url.includes('watch?v=')) {
      embedUrl = url.replace('watch?v=', 'embed/');
    }
    return this.sanitizer.bypassSecurityTrustResourceUrl(embedUrl);
  }

  setActiveTab(tabName: string) {
    this.activeTab.set(tabName);
  }

  addToCart() {
    const j = this.juego();
    if (j) {
      this.cartService.addToCart(j.id).subscribe({
        next: () => {
          this.cartFeedback.set(true);
          setTimeout(() => this.cartFeedback.set(false), 3000);
        }
      });
    }
  }

  addDlcToCart(dlcId: number) {
    this.cartService.addToCart(dlcId).subscribe({
      next: () => {
        this.cartFeedback.set(true);
        setTimeout(() => this.cartFeedback.set(false), 3000);
      }
    });
  }

  toggleFavorite() {
    const j = this.juego();
    if (j) {
      this.favoriteService.toggleFavorite(j.id).subscribe({
        next: (res) => {
          this.isFavorite.set(res.favorito);
        }
      });
    }
  }

  submitReview() {
    const j = this.juego();
    if (j && this.newComment.trim()) {
      this.reviewService.postReview(j.id, {
        comentario: this.newComment,
        calificacion: Number(this.newRating)
      }).subscribe({
        next: () => {
          this.newComment = '';
          this.newRating = 5;
          this.reviewFeedback.set(true);
          setTimeout(() => this.reviewFeedback.set(false), 3000);
          
          // Reload reviews list and average ratings
          this.reviewService.getReviews(j.id).subscribe(data => this.resenas.set(data));
          this.juegoService.getJuego(j.id).subscribe(data => this.juego.set(data));
        }
      });
    }
  }
}
