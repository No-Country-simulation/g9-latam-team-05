import { Component, inject, OnInit } from '@angular/core';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [DecimalPipe, PercentPipe],
  templateUrl: './recommendations.html',
  styleUrl: './recommendations.css',
})
export class RecommendationsComponent implements OnInit {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.getPerfilFinanciero(userId).subscribe();
    this.financeService.calcularAnalisisIa().subscribe();
  }
}
