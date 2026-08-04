import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { FinanceService, Transaction } from '../../core/services/finance';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './transactions.html',
  styleUrl: './transactions.css',
})
export class TransactionsComponent {
  protected financeService = inject(FinanceService);
  private fb = inject(NonNullableFormBuilder);

  // UI state
  isSubmitting = signal<boolean>(false);
  isSavingProfile = signal<boolean>(false);
  sortColumn = signal<string>('fecha');
  sortAsc = signal<boolean>(false);

  // Gasto / Ingreso toggle switch
  tipoSelected = signal<string>('GASTO');

  transactionForm = this.fb.group({
    descripcion: ['', [Validators.required, Validators.minLength(3)]],
    valor: [0, [Validators.required, Validators.min(0.01)]]
  });

  profileForm = this.fb.group({
    ingreso: [this.financeService.income(), [Validators.required, Validators.min(0.01)]],
    endeudamiento: [this.financeService.debtRatio(), [Validators.required, Validators.min(0), Validators.max(100)]],
    frecuencia: [this.financeService.savingFrequency(), [Validators.required]]
  });

  // Computed sorted transactions
  get sortedTransactions(): Transaction[] {
    const list = [...this.financeService.transactions()];
    const col = this.sortColumn();
    const asc = this.sortAsc();

    return list.sort((a, b) => {
      let valA: any = a[col as keyof Transaction];
      let valB: any = b[col as keyof Transaction];

      if (typeof valA === 'string') {
        valA = valA.toLowerCase();
        valB = valB.toLowerCase();
      }

      if (valA < valB) return asc ? -1 : 1;
      if (valA > valB) return asc ? 1 : -1;
      return 0;
    });
  }

  toggleSort(col: string) {
    if (this.sortColumn() === col) {
      this.sortAsc.update(a => !a);
    } else {
      this.sortColumn.set(col);
      this.sortAsc.set(true);
    }
  }

  setTipo(tipo: string) {
    this.tipoSelected.set(tipo);
  }

  onSubmit() {
    if (this.transactionForm.valid) {
      this.isSubmitting.set(true);
      const vals = this.transactionForm.getRawValue();
      const tipo = this.tipoSelected();
      const categoria = tipo === 'INGRESO' ? 'Ingresos' : undefined;

      this.financeService.addTransaction(vals.descripcion, vals.valor, tipo, categoria)
        .then(() => {
          this.isSubmitting.set(false);
          this.transactionForm.reset({
            descripcion: '',
            valor: 0
          });
        });
    }
  }

  onUpdateProfile() {
    if (this.profileForm.valid) {
      this.isSavingProfile.set(true);
      const vals = this.profileForm.getRawValue();
      this.financeService.updateProfile(vals.ingreso, vals.endeudamiento, vals.frecuencia)
        .then(() => {
          this.isSavingProfile.set(false);
          alert('¡Perfil financiero actualizado correctamente!');
        });
    }
  }
}
