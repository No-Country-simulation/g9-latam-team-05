import { Component, inject, signal, computed } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-simulator',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './simulator.html',
  styleUrl: './simulator.css',
})
export class SimulatorComponent {
  protected financeService = inject(FinanceService);

  // Simulation State (Sliders)
  simulatedIncome = signal<number>(this.financeService.income() || 4500);
  simulatedDebt = signal<number>(this.financeService.debtRatio() || 25);

  // Projections
  readonly projectedExpensesTotal = computed(() => {
    return this.financeService.totalExpenses();
  });

  readonly projectedBalance = computed(() => {
    return this.simulatedIncome() - this.projectedExpensesTotal();
  });

  readonly projectedSavingsRate = computed(() => {
    const income = this.simulatedIncome();
    if (income <= 0) return 0;
    return Math.max(0, Math.round((this.projectedBalance() / income) * 100));
  });

  readonly projectedProfile = computed(() => {
    const debt = this.simulatedDebt();
    const rate = this.projectedSavingsRate();
    const ratioExpenses = this.simulatedIncome() > 0 ? (this.projectedExpensesTotal() / this.simulatedIncome()) * 100 : 100;

    if (ratioExpenses > 95 || debt > 50 || rate < 0) {
      return 'En riesgo';
    } else if (ratioExpenses > 70 || debt > 30 || rate < 15) {
      return 'En observación';
    } else {
      return 'Saludable';
    }
  });

  applySimulation() {
    this.financeService.updateProfile(this.simulatedIncome(), this.simulatedDebt(), this.financeService.savingFrequency() || 'MEDIA').then(() => {
      alert('¡Simulación aplicada con éxito a tu perfil financiero!');
    });
  }

  resetSimulation() {
    this.simulatedIncome.set(this.financeService.income() || 4500);
    this.simulatedDebt.set(this.financeService.debtRatio() || 25);
  }
}
