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
  isImporting = signal<boolean>(false);
  sortColumn = signal<string>('fecha');
  sortAsc = signal<boolean>(false);

  transactionForm = this.fb.group({
    descripcion: ['', [Validators.required, Validators.minLength(3)]],
    valor: [0, [Validators.required, Validators.min(0.01)]],
    categoria: ['']
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

  onSubmit() {
    if (this.transactionForm.valid) {
      this.isSubmitting.set(true);
      const vals = this.transactionForm.getRawValue();
      
      this.financeService.addTransaction(vals.descripcion, vals.valor, vals.categoria || undefined)
        .then(() => {
          this.isSubmitting.set(false);
          this.transactionForm.reset({
            descripcion: '',
            valor: 0,
            categoria: ''
          });
        });
    }
  }

  onDelete(id: string) {
    this.financeService.deleteTransaction(id);
  }

  onCSVUpload(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files[0]) {
      this.isImporting.set(true);
      const file = input.files[0];
      const reader = new FileReader();

      reader.onload = (e) => {
        const text = e.target?.result as string;
        this.financeService.importCSV(text)
          .then((count) => {
            this.isImporting.set(false);
            input.value = ''; // Reset input
            alert(`Se importaron con éxito ${count} transacciones clasificados por IA.`);
          });
      };

      reader.readAsText(file);
    }
  }
}
