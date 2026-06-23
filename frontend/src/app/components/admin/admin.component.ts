import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AdminService, AdminDashboardData } from '../../services/admin.service';
import { AuthService } from '../../services/auth.service';
import { Juego } from '../../services/juego.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <main class="page-shell">
      
      <!-- Authorization Guard Message -->
      <div *ngIf="!user().authenticated || user().role !== 'ROLE_ADMIN'" class="tabs-container" style="max-width: 600px; margin: 40px auto; text-align: center; padding: 40px;">
        <span style="font-size: 3rem;">⚠️</span>
        <h3 style="color: #ef4444; font-size: 1.6rem; margin: 10px 0 6px 0;">Acceso Denegado</h3>
        <p style="color: var(--muted); margin-bottom: 20px;">
          Solo los usuarios con rol de administrador pueden acceder a este panel de control.
        </p>
        <a routerLink="/login" class="button-solid">Iniciar Sesión</a>
      </div>

      <!-- Dashboard Panel -->
      <div *ngIf="user().authenticated && user().role === 'ROLE_ADMIN'">
        <section class="store-toolbar">
          <div>
            <h2 class="section-title">Panel de Control de Administrador</h2>
            <p>Gestiona los videojuegos del catálogo, edita especificaciones de sistema y modera las reseñas de los usuarios.</p>
          </div>
        </section>

        <!-- Subsection Tabs -->
        <div class="tabs-nav" style="margin-top: 20px;">
          <button (click)="activeSubTab.set('juegos')" class="tab-btn" [class.active]="activeSubTab() === 'juegos'">🎮 Gestionar Juegos</button>
          <button (click)="activeSubTab.set('resenas')" class="tab-btn" [class.active]="activeSubTab() === 'resenas'">⭐ Moderar Reseñas</button>
        </div>

        <!-- 1. Games Management Tab -->
        <div *ngIf="activeSubTab() === 'juegos'" class="tab-content active" style="display: grid; grid-template-columns: 1.1fr 0.9fr; gap: 24px; align-items: start;">
          
          <!-- Games Table List -->
          <div class="tabs-container" style="padding: 20px; overflow-x: auto;">
            <h3 style="color: white; margin: 0 0 16px 0;">Catálogo de Juegos</h3>
            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border); color: var(--muted); font-weight: bold;">
                  <th style="padding: 10px;">ID</th>
                  <th style="padding: 10px;">Juego</th>
                  <th style="padding: 10px;">Categoría</th>
                  <th style="padding: 10px;">Plataforma</th>
                  <th style="padding: 10px;">Precio</th>
                  <th style="padding: 10px; text-align: center;">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let g of juegos()" style="border-bottom: 1px solid var(--border);">
                  <td style="padding: 10px; font-weight: bold;">{{ g.id }}</td>
                  <td style="padding: 10px; color: white;">
                    <div style="display: flex; align-items: center; gap: 8px;">
                      <img [src]="g.imagenUrl" style="width: 32px; height: 42px; object-fit: cover; border-radius: 4px;">
                      <span>{{ g.nombre }}</span>
                    </div>
                  </td>
                  <td style="padding: 10px;">{{ g.categoria }}</td>
                  <td style="padding: 10px;">{{ g.plataforma }}</td>
                  <td style="padding: 10px;">
                    <strong style="color: #10b981;">S/ {{ g.precio | number:'1.2-2' }}</strong>
                    <span *ngIf="g.descuento" style="display: block; font-size: 0.75rem; color: #ef4444;">-{{ g.descuento }}% Off</span>
                  </td>
                  <td style="padding: 10px; text-align: center;">
                    <button (click)="selectJuegoForEdit(g)" class="button-ghost" style="padding: 4px 8px; font-size: 0.8rem; margin-right: 6px;">✏</button>
                    <button (click)="deleteJuego(g.id)" class="logout-btn" style="padding: 4px 8px; font-size: 0.8rem; border-radius: 6px;">🗑</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Game Form -->
          <div class="tabs-container" style="padding: 24px;">
            <h3 style="color: white; margin: 0 0 16px 0;">
              {{ gameForm.id ? 'Editar Juego (ID: ' + gameForm.id + ')' : 'Agregar Nuevo Videojuego' }}
            </h3>
            
            <form (ngSubmit)="saveJuego()" class="admin-form" style="display: flex; flex-direction: column; gap: 12px;">
              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Nombre del Juego</label>
                <input type="text" [(ngModel)]="gameForm.nombre" name="nombre" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              </div>

              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                <div>
                  <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Categoría / Género</label>
                  <input type="text" [(ngModel)]="gameForm.categoria" name="categoria" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                </div>
                <div>
                  <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Plataforma</label>
                  <input type="text" [(ngModel)]="gameForm.plataforma" name="plataforma" placeholder="Ej. PC, PS5, Switch" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                </div>
              </div>

              <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 10px;">
                <div>
                  <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Precio Original (S/.)</label>
                  <input type="number" step="0.01" [(ngModel)]="gameForm.precio" name="precio" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                </div>
                <div>
                  <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Descuento (%)</label>
                  <input type="number" [(ngModel)]="gameForm.descuento" name="descuento" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                </div>
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Fecha de Lanzamiento</label>
                <input type="date" [(ngModel)]="gameForm.fechaLanzamiento" name="fechaLanzamiento" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Portada (URL de imagen)</label>
                <input type="text" [(ngModel)]="gameForm.imagenUrl" name="imagenUrl" placeholder="Ej. /imgs/GTAV.jpg" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Tráiler (URL de Youtube)</label>
                <input type="text" [(ngModel)]="gameForm.videoUrl" name="videoUrl" placeholder="Ej. https://www.youtube.com/watch?v=..." style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Descripción</label>
                <textarea [(ngModel)]="gameForm.descripcion" name="descripcion" rows="2" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Requisitos Mínimos</label>
                <textarea [(ngModel)]="gameForm.requisitosMinimos" name="requisitosMinimos" rows="2" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Requisitos Recomendados</label>
                <textarea [(ngModel)]="gameForm.requisitosRecomendados" name="requisitosRecomendados" rows="2" style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
              </div>

              <div *ngIf="gameFeedback()" style="color: #10b981; font-weight: bold; font-size: 0.82rem; text-align: center;">
                {{ gameFeedback() }}
              </div>

              <div style="display: flex; gap: 10px; margin-top: 10px;">
                <button type="submit" class="button-solid" style="flex: 1; padding: 10px;">Guardar Juego</button>
                <button type="button" *ngIf="gameForm.id" (click)="resetGameForm()" class="button-ghost" style="padding: 10px;">Cancelar</button>
              </div>
            </form>
          </div>

        </div>

        <!-- 2. Reviews Moderation Tab -->
        <div *ngIf="activeSubTab() === 'resenas'" class="tab-content active" style="display: grid; grid-template-columns: 1.1fr 0.9fr; gap: 24px; align-items: start;">
          
          <!-- Reviews Table -->
          <div class="tabs-container" style="padding: 20px; overflow-x: auto;">
            <h3 style="color: white; margin: 0 0 16px 0;">Reseñas de Usuarios</h3>
            <table style="width: 100%; border-collapse: collapse; text-align: left; font-size: 0.9rem;">
              <thead>
                <tr style="border-bottom: 1px solid var(--border); color: var(--muted); font-weight: bold;">
                  <th style="padding: 10px;">ID</th>
                  <th style="padding: 10px;">Autor</th>
                  <th style="padding: 10px;">Juego</th>
                  <th style="padding: 10px;">Calificación</th>
                  <th style="padding: 10px;">Comentario</th>
                  <th style="padding: 10px; text-align: center;">Acciones</th>
                </tr>
              </thead>
              <tbody>
                <tr *ngFor="let r of resenas()" style="border-bottom: 1px solid var(--border);">
                  <td style="padding: 10px; font-weight: bold;">{{ r.id }}</td>
                  <td style="padding: 10px; color: white;">{{ r.autor }}</td>
                  <td style="padding: 10px;">{{ r.juegoNombre }}</td>
                  <td style="padding: 10px; color: #ffb800; font-weight: bold;">{{ r.calificacion }} ⭐</td>
                  <td style="padding: 10px; max-width: 200px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap;">
                    {{ r.comentario }}
                  </td>
                  <td style="padding: 10px; text-align: center;">
                    <button (click)="selectResenaForEdit(r)" class="button-ghost" style="padding: 4px 8px; font-size: 0.8rem; margin-right: 6px;">✏</button>
                    <button (click)="deleteResena(r.id)" class="logout-btn" style="padding: 4px 8px; font-size: 0.8rem; border-radius: 6px;">🗑</button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <!-- Review Form -->
          <div class="tabs-container" style="padding: 24px;">
            <h3 style="color: white; margin: 0 0 16px 0;">
              {{ reviewForm.id ? 'Editar Reseña (ID: ' + reviewForm.id + ')' : 'Modificar Reseña' }}
            </h3>
            
            <form (ngSubmit)="saveResena()" class="admin-form" style="display: flex; flex-direction: column; gap: 14px;">
              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Autor</label>
                <input type="text" [(ngModel)]="reviewForm.autor" name="autor" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Calificación (1 a 5 estrellas)</label>
                <select [(ngModel)]="reviewForm.calificacion" name="calificacion" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                  <option [value]="5">5 estrellas</option>
                  <option [value]="4">4 estrellas</option>
                  <option [value]="3">3 estrellas</option>
                  <option [value]="2">2 estrellas</option>
                  <option [value]="1">1 estrella</option>
                </select>
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Asociar al Juego</label>
                <select [(ngModel)]="reviewForm.juegoId" name="juegoId" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
                  <option *ngFor="let g of juegos()" [value]="g.id">{{ g.nombre }}</option>
                </select>
              </div>

              <div>
                <label style="display: block; margin-bottom: 4px; font-weight: bold; font-size: 0.82rem;">Comentario</label>
                <textarea [(ngModel)]="reviewForm.comentario" name="comentario" rows="4" required style="width: 100%; padding: 8px 12px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
              </div>

              <div *ngIf="reviewFeedback()" style="color: #10b981; font-weight: bold; font-size: 0.82rem; text-align: center;">
                {{ reviewFeedback() }}
              </div>

              <div style="display: flex; gap: 10px; margin-top: 10px;">
                <button type="submit" [disabled]="!reviewForm.id" class="button-solid" style="flex: 1; padding: 10px;">Guardar Reseña</button>
                <button type="button" *ngIf="reviewForm.id" (click)="resetReviewForm()" class="button-ghost" style="padding: 10px;">Cancelar</button>
              </div>
            </form>
          </div>

        </div>

      </div>
    </main>
  `
})
export class AdminComponent implements OnInit {
  private adminService = inject(AdminService);
  private authService = inject(AuthService);
  private router = inject(Router);

  public user = this.authService.currentUser;
  
  public juegos = signal<Juego[]>([]);
  public resenas = signal<any[]>([]);
  public categories = signal<string[]>([]);
  public activeSubTab = signal<string>('juegos');

  // Game Form fields
  public gameForm: Partial<Juego> = {
    id: undefined,
    nombre: '',
    categoria: '',
    descripcion: '',
    precio: 0,
    imagenUrl: '',
    videoUrl: '',
    plataforma: '',
    fechaLanzamiento: '',
    descuento: 0,
    requisitosMinimos: '',
    requisitosRecomendados: ''
  };

  // Review Form fields
  public reviewForm = {
    id: null as number | null,
    autor: '',
    comentario: '',
    calificacion: 5,
    juegoId: 0
  };

  // Feedbacks
  public gameFeedback = signal<string>('');
  public reviewFeedback = signal<string>('');

  constructor() {}

  ngOnInit() {
    this.authService.checkSession().subscribe(u => {
      if (u.authenticated && u.role === 'ROLE_ADMIN') {
        this.loadDashboardData();
      }
    });
  }

  loadDashboardData() {
    this.adminService.getDashboard().subscribe({
      next: (data) => {
        this.juegos.set(data.juegos);
        this.resenas.set(data.resenas);
        this.categories.set(data.categorias);
      }
    });
  }

  // Games Actions
  selectJuegoForEdit(game: Juego) {
    this.gameForm = { ...game };
  }

  resetGameForm() {
    this.gameForm = {
      id: undefined,
      nombre: '',
      categoria: '',
      descripcion: '',
      precio: 0,
      imagenUrl: '',
      videoUrl: '',
      plataforma: '',
      fechaLanzamiento: '',
      descuento: 0,
      requisitosMinimos: '',
      requisitosRecomendados: ''
    };
    this.gameFeedback.set('');
  }

  saveJuego() {
    this.gameFeedback.set('');
    this.adminService.saveJuego(this.gameForm).subscribe({
      next: () => {
        this.gameFeedback.set('¡Juego guardado correctamente!');
        this.resetGameForm();
        this.loadDashboardData();
        setTimeout(() => this.gameFeedback.set(''), 3000);
      },
      error: (err) => {
        this.gameFeedback.set('Error: ' + (err.error?.error || 'No se pudo guardar el juego'));
      }
    });
  }

  deleteJuego(id: number) {
    if (confirm('¿Estás seguro de que deseas eliminar este videojuego de la base de datos?')) {
      this.adminService.deleteJuego(id).subscribe({
        next: () => {
          this.loadDashboardData();
        }
      });
    }
  }

  // Reviews Actions
  selectResenaForEdit(resena: any) {
    this.reviewForm = {
      id: resena.id,
      autor: resena.autor,
      comentario: resena.comentario,
      calificacion: resena.calificacion,
      juegoId: resena.juegoId
    };
  }

  resetReviewForm() {
    this.reviewForm = {
      id: null,
      autor: '',
      comentario: '',
      calificacion: 5,
      juegoId: 0
    };
    this.reviewFeedback.set('');
  }

  saveResena() {
    this.reviewFeedback.set('');
    if (this.reviewForm.id) {
      this.adminService.saveResena(this.reviewForm).subscribe({
        next: () => {
          this.reviewFeedback.set('¡Reseña moderada con éxito!');
          this.resetReviewForm();
          this.loadDashboardData();
          setTimeout(() => this.reviewFeedback.set(''), 3000);
        },
        error: (err) => {
          this.reviewFeedback.set('Error: ' + (err.error?.error || 'No se pudo guardar la reseña'));
        }
      });
    }
  }

  deleteResena(id: number) {
    if (confirm('¿Estás seguro de que deseas borrar esta reseña?')) {
      this.adminService.deleteResena(id).subscribe({
        next: () => {
          this.loadDashboardData();
        }
      });
    }
  }
}
