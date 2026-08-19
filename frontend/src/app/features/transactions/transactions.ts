import { Component, inject, OnInit, signal } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { DecimalPipe } from '@angular/common';
import { TransactionService } from '../../core/services/transaction-service';
import { FinancialProfileService } from '../../core/services/financial-profile-service';
import { Transaction } from '../../core/models/transaction.model';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-transactions',
  standalone: true,
  imports: [ReactiveFormsModule, DecimalPipe],
  templateUrl: './transactions.html',
  styleUrl: './transactions.css',
})
export class TransactionsComponent implements OnInit {
  protected transactionService = inject(TransactionService);
  protected profileService = inject(FinancialProfileService);
  protected authService = inject(AuthService);
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
    ingreso: [this.profileService.income() || 4500, [Validators.required, Validators.min(0.01)]],
    endeudamiento: [this.profileService.debtRatio() || 25, [Validators.required, Validators.min(0), Validators.max(100)]],
    frecuencia: [this.profileService.savingFrequency() || 'Media', [Validators.required]]
  });

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.transactionService.getTransaccionesRecientes(userId, 20).subscribe();
    this.profileService.getPerfilFinanciero(userId).subscribe({
      next: () => this.syncProfileForm()
    });
  }

  private syncProfileForm() {
    this.profileForm.patchValue({
      ingreso: this.profileService.income() || 4500,
      endeudamiento: this.profileService.debtRatio() || 25,
      frecuencia: this.profileService.savingFrequency() || 'Media'
    });
  }

  // Computed sorted transactions
  get sortedTransactions(): Transaction[] {
    const list = [...this.transactionService.transactions()];
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
      const userId = this.authService.currentUser()?.id || 1;

      this.transactionService.registrarTransaccion(vals.descripcion, vals.valor, tipo, userId).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.transactionForm.reset({
            descripcion: '',
            valor: 0
          });

          // Se esperan 2.5 segundos a que el backend reciba la respuesta de FastAPI/Python 
          // y actualice el registro en Oracle, luego re-ejecuta la consulta silenciosamente.
          setTimeout(() => {
            this.transactionService.getTransaccionesRecientes(userId, 20).subscribe();
          }, 2500);
        },
        error: (err) => {
          this.isSubmitting.set(false);
          console.error('Error al registrar transacción:', err);
        }
      });
    }
  }

  onUpdateProfile() {
    if (this.profileForm.valid) {
      this.isSavingProfile.set(true);
      const vals = this.profileForm.getRawValue();

      this.profileService.guardarPerfilFinanciero(vals.ingreso, vals.endeudamiento, vals.frecuencia).subscribe({
        next: () => {
          this.isSavingProfile.set(false);
          alert('¡Perfil financiero actualizado correctamente!');
        },
        error: (err) => {
          this.isSavingProfile.set(false);
          console.error('Error al actualizar perfil:', err);
        }
      });
    }
  }
}
