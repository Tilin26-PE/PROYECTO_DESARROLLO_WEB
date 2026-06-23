import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivityService, ActivityLog } from '../../services/activity.service';

@Component({
  selector: 'app-actividad',
  standalone: true,
  imports: [CommonModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Tu Bitácora de Actividad</h2>
          <p>Consulta el historial de acciones y operaciones realizadas en tu cuenta dentro de GAMEXUS.</p>
        </div>
      </section>

      <div *ngIf="logs().length === 0" class="empty-message" style="padding: 40px 0;">
        <p>No se registran actividades en tu historial todavía.</p>
      </div>

      <div class="tabs-container" *ngIf="logs().length > 0" style="padding: 24px; display: flex; flex-direction: column; gap: 14px;">
        <div *ngFor="let log of logs()" 
             style="display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid var(--border); padding-bottom: 12px; gap: 14px; flex-wrap: wrap;">
          
          <div>
            <!-- Action Badge -->
            <span [class]="'chip action-badge ' + log.accion.toLowerCase()" 
                  style="font-weight: 800; font-size: 0.75rem; letter-spacing: 0.05em; padding: 4px 10px; border-radius: 6px; border: none;">
              {{ log.accion }}
            </span>
            <p style="margin: 8px 0 0 0; color: white; font-size: 0.95rem; line-height: 1.5;">
              {{ log.detalle }}
              <strong *ngIf="log.juegoNombre"> - {{ log.juegoNombre }}</strong>
            </p>
          </div>

          <div style="text-align: right;">
            <span style="color: var(--muted); font-size: 0.82rem;">
              📅 {{ log.fecha | date:'dd/MM/yyyy HH:mm:ss' }}
            </span>
          </div>

        </div>
      </div>
    </main>
  `,
  styles: [`
    .action-badge {
      display: inline-block;
      text-align: center;
    }
    .action-badge.login { background: rgba(59, 130, 246, 0.2); color: #60a5fa; }
    .action-badge.logout { background: rgba(239, 68, 68, 0.2); color: #f87171; }
    .action-badge.registro { background: rgba(16, 185, 129, 0.2); color: #34d399; }
    .action-badge.carrito_agregar { background: rgba(245, 158, 11, 0.2); color: #fbbf24; }
    .action-badge.carrito_quitar { background: rgba(251, 191, 36, 0.15); color: #fcd34d; }
    .action-badge.carrito_eliminar { background: rgba(239, 68, 68, 0.15); color: #f87171; }
    .action-badge.favorito_agregado { background: rgba(236, 72, 153, 0.2); color: #f472b6; }
    .action-badge.favorito_quitado { background: rgba(244, 114, 182, 0.1); color: #f472b6; }
    .action-badge.compra_confirmada { background: rgba(16, 185, 129, 0.2); color: #34d399; }
    .action-badge.comentario { background: rgba(139, 92, 246, 0.2); color: #a78bfa; }
    .action-badge.perfil_modificar { background: rgba(25, 211, 255, 0.2); color: #19d3ff; }
    .action-badge.password_modificar { background: rgba(124, 92, 255, 0.2); color: #a78bfa; }
  `]
})
export class ActividadComponent implements OnInit {
  public logs = signal<ActivityLog[]>([]);

  constructor(private activityService: ActivityService) {}

  ngOnInit() {
    this.activityService.getActivityLogs().subscribe({
      next: (data) => {
        // Sort by id desc (newest first)
        this.logs.set(data.sort((a, b) => b.id - a.id));
      }
    });
  }
}
