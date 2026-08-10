import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { PerfilFinancieroData } from '../models/profile.model';

@Injectable({
  providedIn: 'root',
})
export class FinancialProfileService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly income = signal<number>(4500);
  readonly debtRatio = signal<number>(25);
  readonly savingFrequency = signal<string>('MEDIA');

  getPerfilFinanciero(usuarioId: number = 1): Observable<PerfilFinancieroData | null> {
    return this.http.get<PerfilFinancieroData>(`${this.apiUrl}/perfiles-financieros/usuario/${usuarioId}`).pipe(
      catchError(() => of(null)),
      tap(res => {
        if (res) {
          if (res.ingreso_mensual != null) this.income.set(res.ingreso_mensual);
          if (res.nivel_endeudamiento != null) this.debtRatio.set(res.nivel_endeudamiento);
          if (res.frecuencia_ahorro != null) this.savingFrequency.set(res.frecuencia_ahorro);
        }
      })
    );
  }

  guardarPerfilFinanciero(ingreso_mensual: number, nivel_endeudamiento: number, frecuencia_ahorro: string): Observable<PerfilFinancieroData> {
    const frecuenciaFormatted = (frecuencia_ahorro || 'MEDIA').toUpperCase();
    const body: PerfilFinancieroData = { ingreso_mensual, nivel_endeudamiento, frecuencia_ahorro: frecuenciaFormatted };

    // Intentamos PUT primero (Actualización de perfil existente). Si retorna error (ej. perfil no creado), ejecutamos POST.
    return this.http.put<PerfilFinancieroData>(`${this.apiUrl}/perfiles-financieros`, body).pipe(
      catchError(() => this.http.post<PerfilFinancieroData>(`${this.apiUrl}/perfiles-financieros`, body)),
      tap(res => {
        if (res) {
          if (res.ingreso_mensual != null) this.income.set(res.ingreso_mensual);
          if (res.nivel_endeudamiento != null) this.debtRatio.set(res.nivel_endeudamiento);
          if (res.frecuencia_ahorro != null) this.savingFrequency.set(res.frecuencia_ahorro);
        }
      })
    );
  }
}
