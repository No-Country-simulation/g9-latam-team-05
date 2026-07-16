import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-onboarding',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './onboarding.html',
  styleUrl: './onboarding.css',
})
export class OnboardingComponent {
  private fb = inject(NonNullableFormBuilder);
  private router = inject(Router);
  private financeService = inject(FinanceService);

  // Stepper state
  currentStep = signal<number>(1);

  onboardingForm = this.fb.group({
    income: [4500, [Validators.required, Validators.min(100)]],
    debtRatio: [25, [Validators.required, Validators.min(0), Validators.max(100)]],
    savingFrequency: ['Media', [Validators.required]]
  });

  nextStep() {
    if (this.currentStep() < 3) {
      this.currentStep.update(s => s + 1);
    }
  }

  prevStep() {
    if (this.currentStep() > 1) {
      this.currentStep.update(s => s - 1);
    }
  }

  onSubmit() {
    if (this.onboardingForm.valid) {
      const values = this.onboardingForm.getRawValue();
      
      // Update global state in FinanceService
      this.financeService.income.set(values.income);
      this.financeService.debtRatio.set(values.debtRatio);
      this.financeService.savingFrequency.set(values.savingFrequency);

      // Redirect to dashboard
      this.router.navigate(['/dashboard']);
    }
  }
}
