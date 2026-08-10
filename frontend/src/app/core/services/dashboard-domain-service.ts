import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, of } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DashboardResumen, AnalisisIaResponse, AnalisisHistorialItem } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class DashboardDomainService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly kpiResumen = signal<DashboardResumen>({
    ingresosMensuales: 0,
    gastosTotales: 0,
    balanceNeto: 0,
    tasaAhorro: 0
  });

  readonly analisisIaResult = signal<AnalisisIaResponse | null>(null);
  readonly historialIaList = signal<AnalisisHistorialItem[]>([]);

  getDashboardResumen(usuarioId: number = 1): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${this.apiUrl}/dashboard/resumen/${usuarioId}`).pipe(
      tap(res => this.kpiResumen.set(res))
    );
  }

  calcularAnalisisIa(): Observable<AnalisisIaResponse> {
    return this.http.post<AnalisisIaResponse>(`${this.apiUrl}/analisis-financiero`, {}).pipe(
      tap(res => {
        if (res) {
          this.analisisIaResult.set(res);
        }
      })
    );
  }

  getHistorialAnalisis(): Observable<AnalisisHistorialItem[]> {
    return this.http.get<AnalisisHistorialItem[]>(`${this.apiUrl}/analisis-financiero/historial`).pipe(
      catchError(() => of([])),
      tap(res => {
        if (res) {
          this.historialIaList.set(res);
        }
      })
    );
  }
}
