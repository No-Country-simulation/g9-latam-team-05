import { Component, inject } from '@angular/core';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-recommendations',
  standalone: true,
  imports: [DecimalPipe, PercentPipe],
  templateUrl: './recommendations.html',
  styleUrl: './recommendations.css',
})

export class RecommendationsComponent {
  protected financeService = inject(FinanceService);
}
