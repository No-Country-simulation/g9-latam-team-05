import { Component, inject } from '@angular/core';
import { KeyValuePipe, DecimalPipe, PercentPipe, NgClass } from '@angular/common';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [KeyValuePipe, DecimalPipe, PercentPipe],
  templateUrl: './profile.html',
  styleUrl: './profile.css',
})

export class ProfileComponent {
  protected financeService = inject(FinanceService);
}
