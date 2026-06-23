import { Component, OnInit, signal, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService, User } from '../../services/auth.service';

@Component({
  selector: 'app-perfil',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <main class="page-shell">
      <section class="store-toolbar">
        <div>
          <h2 class="section-title">Configuración de la Cuenta</h2>
          <p>Gestiona los detalles de tu perfil de usuario, prefiere alertas de descuento y cambia tu contraseña.</p>
        </div>
      </section>

      <div class="detail-layout" style="grid-template-columns: 1fr 1fr;">
        
        <!-- Section 1: Profile Customization -->
        <div class="tabs-container" style="padding: 24px;">
          <h3 style="margin: 0 0 16px 0; color: white; border-bottom: 1px solid var(--border); padding-bottom: 10px;">👤 Editar Perfil</h3>
          
          <form (ngSubmit)="saveProfile()" class="admin-form" style="display: flex; flex-direction: column; gap: 14px;">
            <div>
              <label for="profUser" style="display: block; margin-bottom: 6px; font-weight: 600; color: var(--muted);">Nombre de Usuario (no modificable)</label>
              <input type="text" id="profUser" [value]="user().username" readonly disabled
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: rgba(255,255,255,0.02); color: var(--muted); cursor: not-allowed;">
            </div>

            <div>
              <label for="profAlias" style="display: block; margin-bottom: 6px; font-weight: 600;">Alias / Nombre Visible</label>
              <input type="text" id="profAlias" name="profAlias" [(ngModel)]="displayName" required
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div>
              <label for="profEmail" style="display: block; margin-bottom: 6px; font-weight: 600;">Correo Electrónico</label>
              <input type="email" id="profEmail" name="profEmail" [(ngModel)]="email" required
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div>
              <label for="profAvatar" style="display: block; margin-bottom: 6px; font-weight: 600;">URL del Avatar / Foto de Perfil</label>
              <input type="url" id="profAvatar" name="profAvatar" [(ngModel)]="avatarUrl"
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div>
              <label for="profBio" style="display: block; margin-bottom: 6px; font-weight: 600;">Biografía</label>
              <textarea id="profBio" name="profBio" [(ngModel)]="bio" rows="3"
                        style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text); font-family: inherit;"></textarea>
            </div>

            <!-- Discount Alert Preferences Toggle -->
            <div style="display: flex; align-items: center; gap: 10px; background: rgba(25, 211, 255, 0.05); border: 1px solid rgba(25, 211, 255, 0.15); padding: 12px; border-radius: 10px; margin-top: 6px;">
              <input type="checkbox" id="profAlerts" name="profAlerts" [(ngModel)]="alertasDescuentos"
                     style="width: 18px; height: 18px; cursor: pointer;">
              <label for="profAlerts" style="cursor: pointer; font-weight: 600; font-size: 0.9rem;">
                🔔 Recibir alertas visuales de ofertas en favoritos
              </label>
            </div>

            <div *ngIf="profileFeedback()" style="color: #10b981; font-weight: bold; font-size: 0.88rem;">
              ✔ ¡Perfil actualizado correctamente!
            </div>

            <div style="margin-top: 10px;">
              <button type="submit" class="button-solid" style="padding: 10px 18px;">Guardar Cambios</button>
            </div>
          </form>
        </div>

        <!-- Section 2: Security & Password Update -->
        <div class="tabs-container" style="padding: 24px;">
          <h3 style="margin: 0 0 16px 0; color: white; border-bottom: 1px solid var(--border); padding-bottom: 10px;">🔑 Cambiar Contraseña</h3>
          
          <form (ngSubmit)="changePassword()" class="admin-form" style="display: flex; flex-direction: column; gap: 14px;">
            <div>
              <label for="currPass" style="display: block; margin-bottom: 6px; font-weight: 600;">Contraseña Actual</label>
              <input type="password" id="currPass" name="currPass" [(ngModel)]="currentPassword" required placeholder="••••••••"
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div>
              <label for="newPass" style="display: block; margin-bottom: 6px; font-weight: 600;">Nueva Contraseña</label>
              <input type="password" id="newPass" name="newPass" [(ngModel)]="newPassword" required placeholder="Mínimo 4 caracteres"
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div>
              <label for="confNewPass" style="display: block; margin-bottom: 6px; font-weight: 600;">Confirmar Nueva Contraseña</label>
              <input type="password" id="confNewPass" name="confNewPass" [(ngModel)]="confirmNewPassword" required placeholder="Repetir nueva contraseña"
                     style="width: 100%; padding: 10px; border-radius: 8px; border: 1px solid var(--border); background: var(--bg-soft); color: var(--text);">
            </div>

            <div *ngIf="passwordError()" style="color: #ef4444; font-weight: bold; font-size: 0.88rem;">
              ⚠️ {{ passwordError() }}
            </div>

            <div *ngIf="passwordSuccess()" style="color: #10b981; font-weight: bold; font-size: 0.88rem;">
              ✔ ¡Contraseña cambiada con éxito!
            </div>

            <div style="margin-top: 10px;">
              <button type="submit" class="button-solid" style="padding: 10px 18px; background: linear-gradient(135deg, var(--accent-3), #ffaa77); box-shadow: 0 8px 20px rgba(255,139,61,0.25);">
                Actualizar Contraseña
              </button>
            </div>
          </form>
        </div>

      </div>
    </main>
  `
})
export class PerfilComponent implements OnInit {
  private authService = inject(AuthService);

  public user = this.authService.currentUser;

  // Profile fields
  public displayName = '';
  public email = '';
  public avatarUrl = '';
  public bio = '';
  public alertasDescuentos = true;

  // Password change fields
  public currentPassword = '';
  public newPassword = '';
  public confirmNewPassword = '';

  // Feedback states
  public profileFeedback = signal<boolean>(false);
  public passwordSuccess = signal<boolean>(false);
  public passwordError = signal<string>('');

  constructor() {}

  ngOnInit() {
    this.authService.checkSession().subscribe({
      next: (user) => {
        this.displayName = user.displayName || '';
        this.email = user.email || '';
        this.avatarUrl = user.avatarUrl || '';
        this.bio = user.bio || '';
        this.alertasDescuentos = user.alertasDescuentos !== undefined ? user.alertasDescuentos : true;
      }
    });
  }

  saveProfile() {
    this.profileFeedback.set(false);
    this.authService.updateProfile({
      displayName: this.displayName,
      email: this.email,
      avatarUrl: this.avatarUrl,
      bio: this.bio,
      alertasDescuentos: this.alertasDescuentos
    }).subscribe({
      next: () => {
        this.profileFeedback.set(true);
        setTimeout(() => this.profileFeedback.set(false), 3000);
      }
    });
  }

  changePassword() {
    this.passwordError.set('');
    this.passwordSuccess.set(false);

    if (this.newPassword.length < 4) {
      this.passwordError.set('La nueva contraseña debe tener al menos 4 caracteres.');
      return;
    }

    if (this.newPassword !== this.confirmNewPassword) {
      this.passwordError.set('Las nuevas contraseñas ingresadas no coinciden.');
      return;
    }

    this.authService.changePassword({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    }).subscribe({
      next: () => {
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmNewPassword = '';
        this.passwordSuccess.set(true);
        setTimeout(() => this.passwordSuccess.set(false), 3000);
      },
      error: (err) => {
        this.passwordError.set(err.error?.error || 'No se pudo cambiar la contraseña.');
      }
    });
  }
}
