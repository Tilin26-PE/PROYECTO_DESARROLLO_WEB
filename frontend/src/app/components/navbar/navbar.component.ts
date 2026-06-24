import { Component, OnInit, signal, effect, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { CartService } from '../../services/cart.service';
import { FavoriteService } from '../../services/favorite.service';
import { Juego } from '../../services/juego.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  template: `
    <!-- Discount Banner at the top of Navbar -->
    <div *ngIf="user().authenticated && user().alertasDescuentos && discountedFavorites().length > 0" class="discount-banner">
      <div class="banner-content">
        <span class="banner-icon">🔥</span>
        <span class="banner-text">
          ¡Ofertas en tu lista de deseos! 
          <span *ngFor="let game of discountedFavorites(); let last = last">
            <strong>{{ game.nombre }}</strong> está con <strong>{{ game.descuento }}% de descuento</strong> (S/. {{ game.precioConDescuento }}){{ last ? '' : ', ' }}
          </span>
        </span>
      </div>
    </div>

    <header>
      <!-- Logo layout matching mockup -->
      <a routerLink="/" style="text-decoration: none; display: flex; align-items: center; gap: 8px;">
        <div class="logo-icon-wrapper">
          <svg viewBox="0 0 24 24" width="16" height="16" fill="currentColor">
            <path d="M21 6H3c-1.1 0-2 .9-2 2v8c0 1.1.9 2 2 2h18c1.1 0 2-.9 2-2V8c0-1.1-.9-2-2-2zm-10 7H8v3H6v-3H3v-2h3V8h2v3h3v2zm4.5 3c-.83 0-1.5-.67-1.5-1.5s.67-1.5 1.5-1.5 1.5.67 1.5 1.5-.67 1.5-1.5 1.5zm4-3c-.83 0-1.5-.67-1.5-1.5S18.67 9 19.5 9s1.5.67 1.5 1.5-.67 1.5-1.5 1.5z"/>
          </svg>
        </div>
        <h1 class="logo">GAMEXUS</h1>
      </a>
      
      <nav>
        <!-- Nav Links matching mockup -->
        <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Inicio</a>
        <a routerLink="/tienda" routerLinkActive="active">Juegos</a>
        <a routerLink="/catalogo" routerLinkActive="active">Noticias</a>
        <a routerLink="/top-juegos" routerLinkActive="active">Reseñas</a>
        <a routerLink="/actividad" routerLinkActive="active">Comunidad</a>
        <a routerLink="/tienda" routerLinkActive="active">Tienda</a>

        <!-- Cart Link with counter -->
        <a routerLink="/carrito" routerLinkActive="active" class="cart-nav-btn" style="margin-left: 6px;">
          🛒 <span class="badge" *ngIf="cartCount() > 0">{{ cartCount() }}</span>
        </a>

        <!-- Search box inside header -->
        <div class="nav-search-bar">
          <svg viewBox="0 0 24 24" width="13" height="13" fill="none" stroke="currentColor" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round" class="search-icon">
            <circle cx="11" cy="11" r="8"/>
            <line x1="21" y1="21" x2="16.65" y2="16.65"/>
          </svg>
          <input type="text" placeholder="Buscar..." (keyup.enter)="onSearch($event)">
        </div>

        <!-- Theme Toggle Button -->
        <button (click)="toggleTheme()" class="theme-toggle-btn" aria-label="Cambiar tema" style="width: 34px; height: 34px; font-size: 0.95rem;">
          {{ isLightTheme() ? '🌙' : '☀️' }}
        </button>

        <!-- Auth & Profile Section -->
        <ng-container *ngIf="!user().authenticated; else userMenu">
          <a routerLink="/login" routerLinkActive="active" style="padding: 6px 10px; font-size: 0.88rem;">Login</a>
          <a routerLink="/register" routerLinkActive="active" class="register-btn" style="padding: 6px 12px; font-size: 0.88rem; border-radius: 8px;">Registrarse</a>
        </ng-container>

        <ng-template #userMenu>
          <div class="user-dropdown-container">
            <a routerLink="/admin" routerLinkActive="active" *ngIf="user().role === 'ROLE_ADMIN'" class="admin-link">🔧 Admin</a>
            
            <!-- Notification bell (wishlist tracker) -->
            <button class="nav-icon-btn" routerLink="/favoritos" aria-label="Notificaciones" style="width: 34px; height: 34px;">
              <svg viewBox="0 0 24 24" width="18" height="18" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M18 8A6 6 0 0 0 6 8c0 7-3 9-3 9h18s-3-2-3-9"/>
                <path d="M13.73 21a2 2 0 0 1-3.46 0"/>
              </svg>
              <span class="bell-dot" *ngIf="discountedFavorites().length > 0"></span>
            </button>

            <!-- Circular profile avatar link -->
            <a routerLink="/perfil" class="nav-avatar-link" title="Mi Perfil" 
   style="width:auto;min-width:unset;height:36px;border-radius:24px;padding:0 12px 0 4px;display:flex;align-items:center;gap:8px;overflow:visible;">
  <img [src]="user().avatarUrl || '/imgs/default-avatar.png'" alt="Avatar" class="nav-avatar-img" 
       style="width:36px;height:36px;min-width:36px;border-radius:50%;object-fit:cover;">
  <span style="color:var(--text);font-size:0.85rem;font-weight:600;white-space:nowrap;">{{ user().displayName }}</span>
</a>

            <button (click)="logout()" class="logout-btn" style="padding: 6px 12px; font-size: 0.85rem; border-radius: 8px;">Salir</button>
          </div>
        </ng-template>
      </nav>
    </header>
  `,
  styles: [`
    .discount-banner {
      background: linear-gradient(90deg, var(--accent-3), #ff3d6b);
      color: white;
      text-align: center;
      padding: 8px 16px;
      font-size: 0.85rem;
      font-weight: 600;
      box-shadow: 0 4px 12px rgba(255, 139, 61, 0.2);
      animation: slideDown 0.4s ease;
      z-index: 100;
      width: 100%;
    }
    .banner-content {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 8px;
    }
    .badge {
      background: var(--accent-3);
      color: white;
      font-weight: bold;
      border-radius: 99px;
      padding: 2px 6px;
      font-size: 0.72rem;
      margin-left: 4px;
    }
    .theme-toggle-btn {
      background: rgba(255, 255, 255, 0.03);
      border: 1px solid var(--border);
      border-radius: 50%;
      cursor: pointer;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      transition: all 0.2s ease;
      color: var(--text);
    }
    .theme-toggle-btn:hover {
      background: rgba(112, 0, 255, 0.1);
      transform: scale(1.05);
    }
    @keyframes slideDown {
      from { transform: translateY(-100%); }
      to { transform: translateY(0); }
    }
  `]
})
export class NavbarComponent implements OnInit {
  private authService = inject(AuthService);
  private cartService = inject(CartService);
  private favoriteService = inject(FavoriteService);
  private router = inject(Router);

  public user = this.authService.currentUser;
  public cartCount = this.cartService.cartItemsCount;
  public isLightTheme = signal<boolean>(false);
  public discountedFavorites = signal<Juego[]>([]);

  constructor() {
    // Synchronize discounted items count on wishlist changes
    effect(() => {
      const list = this.favoriteService.favoritesList();
      const discounted = list.filter(j => j.descuento && j.descuento > 0);
      this.discountedFavorites.set(discounted);
    }, { allowSignalWrites: true });
  }

  ngOnInit() {
    // Retrieve theme preferences
    if (typeof window !== 'undefined') {
      const isLight = localStorage.getItem('theme') === 'light';
      this.isLightTheme.set(isLight);
      this.applyTheme(isLight);
    }

    // Restore user session data
    this.authService.checkSession().subscribe({
      next: (user) => {
        if (user.authenticated) {
          this.cartService.getCart().subscribe();
          this.favoriteService.getFavorites().subscribe();
        }
      }
    });
  }

  toggleTheme() {
    const nextState = !this.isLightTheme();
    this.isLightTheme.set(nextState);
    if (typeof window !== 'undefined') {
      localStorage.setItem('theme', nextState ? 'light' : 'dark');
      this.applyTheme(nextState);
    }
  }

  private applyTheme(isLight: boolean) {
    if (typeof document !== 'undefined') {
      const htmlEl = document.documentElement;
      if (isLight) {
        htmlEl.classList.add('light-theme');
      } else {
        htmlEl.classList.remove('light-theme');
      }
    }
  }

  onSearch(event: any) {
    const query = event.target.value;
    if (query && query.trim()) {
      this.router.navigate(['/catalogo'], { queryParams: { q: query.trim() } });
      event.target.value = ''; // Clean navbar search input
    }
  }

  logout() {
    this.authService.logout().subscribe({
      next: () => {
        this.router.navigate(['/login']);
      }
    });
  }
}
