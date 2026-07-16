import { Component, inject } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './settings.html',
  styleUrl: './settings.css',
})
export class SettingsComponent {
  protected financeService = inject(FinanceService);
  private fb = inject(NonNullableFormBuilder);

  settingsForm = this.fb.group({
    income: [this.financeService.income(), [Validators.required, Validators.min(100)]],
    debtRatio: [this.financeService.debtRatio(), [Validators.required, Validators.min(0), Validators.max(100)]],
    savingFrequency: [this.financeService.savingFrequency(), [Validators.required]]
  });

  onSave() {
    if (this.settingsForm.valid) {
      const vals = this.settingsForm.getRawValue();
      this.financeService.income.set(vals.income);
      this.financeService.debtRatio.set(vals.debtRatio);
      this.financeService.savingFrequency.set(vals.savingFrequency);
      alert('¡Configuración guardada y perfil recalculado con éxito!');
    }
  }
}
