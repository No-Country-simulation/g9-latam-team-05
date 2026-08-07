import { Component, inject, signal } from '@angular/core';
import { NonNullableFormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './register.html',
  styleUrl: './register.css',
})
export class RegisterComponent {
  private fb = inject(NonNullableFormBuilder);
  private router = inject(Router);
  private authService = inject(AuthService);

  readonly isLoading = signal<boolean>(false);
  readonly errorMessage = signal<string | null>(null);
  readonly successMessage = signal<string | null>(null);

  registerForm = this.fb.group({
    name: ['', [Validators.required, Validators.minLength(2)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  onSubmit(): void {
    if (this.registerForm.invalid) return;

    this.isLoading.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    const { name, email, password } = this.registerForm.getRawValue();

    this.authService.register({ nombre: name, email, password }).subscribe({
      next: (res) => {
        // Auto-login after successful registration
        this.authService.login({ email, password }).subscribe({
          next: () => {
            this.isLoading.set(false);
            this.router.navigate(['/dashboard']);
          },
          error: () => {
            this.isLoading.set(false);
            this.successMessage.set('Cuenta registrada exitosamente. Inicia sesión para continuar.');
            setTimeout(() => this.router.navigate(['/login']), 1500);
          }
        });
      },
      error: (err) => {
        this.isLoading.set(false);
        if (err.status === 409 || err.status === 400) {
          this.errorMessage.set('El correo electrónico ya se encuentra registrado o los datos son inválidos.');
        } else {
          this.errorMessage.set('Error al conectar con el servidor backend Java. Verifica la conexión.');
        }
      }
    });
  }
}
