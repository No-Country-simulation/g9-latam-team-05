import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { FinanceService } from '../core/services/finance';
import { AuthService } from '../core/services/auth';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class ShellComponent {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  logout() {
    this.authService.logout();
  }
}
