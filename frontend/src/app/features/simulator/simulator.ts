import { Component, inject, signal, computed } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService, Transaction } from '../../core/services/finance';

interface SimulatedExpense {
  descripcion: string;
  valor: number;
  categoria: string;
}

@Component({
  selector: 'app-simulator',
  standalone: true,
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './simulator.html',
  styleUrl: './simulator.css',
})

export class SimulatorComponent {
  protected financeService = inject(FinanceService);
  private fb = inject(NonNullableFormBuilder);

  // Simulation State (Sliders and list of hypothetical expenses)
  simulatedIncome = signal<number>(this.financeService.income());
  simulatedDebt = signal<number>(this.financeService.debtRatio());
  simulatedExpensesList = signal<SimulatedExpense[]>([]);

  simulatorForm = this.fb.group({
    descripcion: ['', [Validators.required, Validators.minLength(3)]],
    valor: [0, [Validators.required, Validators.min(0.01)]],
    categoria: ['Ocio', [Validators.required]]
  });

  // Projections
  readonly projectedExpensesTotal = computed(() => {
    const baselineExpenses = this.financeService.totalExpenses();
    const simulatedExtra = this.simulatedExpensesList().reduce((acc, e) => acc + e.valor, 0);
    return baselineExpenses + simulatedExtra;
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
    const ratioExpenses = (this.projectedExpensesTotal() / this.simulatedIncome()) * 100;

    if (ratioExpenses > 95 || debt > 50 || rate < 0) {
      return 'En riesgo';
    } else if (ratioExpenses > 70 || debt > 30 || rate < 15) {
      return 'En observación';
    } else {
      return 'Saludable';
    }
  });

  // Methods
  addSimulatedExpense() {
    if (this.simulatorForm.valid) {
      const val = this.simulatorForm.getRawValue();
      this.simulatedExpensesList.update(list => [...list, {
        descripcion: val.descripcion,
        valor: val.valor,
        categoria: val.categoria
      }]);
      this.simulatorForm.reset({
        descripcion: '',
        valor: 0,
        categoria: 'Ocio'
      });
    }
  }

  removeSimulatedExpense(index: number) {
    this.simulatedExpensesList.update(list => list.filter((_, i) => i !== index));
  }

  applySimulation() {
    // Write simulated state to persistent core state
    this.financeService.income.set(this.simulatedIncome());
    this.financeService.debtRatio.set(this.simulatedDebt());
    
    // Convert simulated list into real transactions in the service
    const promises = this.simulatedExpensesList().map(e => 
      this.financeService.addTransaction(e.descripcion, e.valor, e.categoria)
    );

    Promise.all(promises).then(() => {
      this.simulatedExpensesList.set([]);
      alert('¡Simulación aplicada con éxito! Los datos ahora son parte del estado real.');
    });
  }

  resetSimulation() {
    this.simulatedIncome.set(this.financeService.income());
    this.simulatedDebt.set(this.financeService.debtRatio());
    this.simulatedExpensesList.set([]);
  }
}
