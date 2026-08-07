import { Component, inject, OnInit } from '@angular/core';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [DecimalPipe, PercentPipe],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})
export class ProfileComponent implements OnInit {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.calcularAnalisisIa(userId).subscribe();
    this.financeService.getDistribucionGastos(userId).subscribe();
  }
}
