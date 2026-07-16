import { Component, inject } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive, Router } from '@angular/router';
import { FinanceService } from '../core/services/finance';

@Component({
  selector: 'app-shell',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: './shell.html',
  styleUrl: './shell.css',
})
export class ShellComponent {
  private router = inject(Router);
  protected financeService = inject(FinanceService);

  logout() {
    this.router.navigate(['/login']);
  }
}
