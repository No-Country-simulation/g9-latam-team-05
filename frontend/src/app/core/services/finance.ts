import { Injectable, inject, computed } from '@angular/core';
import { Observable } from 'rxjs';
import { TransactionService } from './transaction-service';
import { FinancialProfileService } from './financial-profile-service';
import { DashboardDomainService } from './dashboard-domain-service';
import { Transaction, CategoriaDistribucion, DistribucionResponse } from '../models/transaction.model';
import { DashboardResumen, AnalisisIaResponse, AnalisisHistorialItem } from '../models/dashboard.model';

@Injectable({
  providedIn: 'root',
})
export class FinanceService {
  private readonly transactionService = inject(TransactionService);
  private readonly profileService = inject(FinancialProfileService);
  private readonly dashboardService = inject(DashboardDomainService);

  // Delegated State Signals
  readonly kpiResumen = this.dashboardService.kpiResumen;
  readonly distribucionGastos = this.transactionService.distribucionGastos;
  readonly modoContingenciaIA = this.transactionService.modoContingenciaIA;
  readonly mensajeEstadoIA = this.transactionService.mensajeEstadoIA;
  readonly analisisIaResult = this.dashboardService.analisisIaResult;
  readonly historialIaList = this.dashboardService.historialIaList;

  readonly income = this.profileService.income;
  readonly debtRatio = this.profileService.debtRatio;
  readonly savingFrequency = this.profileService.savingFrequency;
  readonly transactions = this.transactionService.transactions;

  // Computed Values - Delegated
  readonly totalExpenses = computed(() => this.kpiResumen().gastosTotales);
  readonly totalIncome = computed(() => this.kpiResumen().ingresosMensuales);
  readonly netBalance = computed(() => this.kpiResumen().balanceNeto);
  readonly balance = computed(() => this.kpiResumen().balanceNeto);
  readonly savingsRate = computed(() => this.kpiResumen().tasaAhorro);

  readonly riskProfile = computed(() => {
    const res = this.analisisIaResult();
    return res ? res.perfil_financiero : 'En observación';
  });

  readonly probability = computed(() => {
    const res = this.analisisIaResult();
    return res ? res.probabilidad : 0;
  });

  readonly recommendations = computed(() => {
    const res = this.analisisIaResult();
    return res && res.recomendaciones ? res.recomendaciones : [];
  });

  readonly categoryExpenses = computed(() => {
    const res = this.analisisIaResult();
    if (res && res.resumen_gastos) {
      return res.resumen_gastos;
    }
    const map: Record<string, number> = {};
    for (const d of this.distribucionGastos()) {
      map[d.categoria] = d.montoTotal;
    }
    return map;
  });

  // Delegated HTTP methods to Domain Services
  getDashboardResumen(usuarioId: number = 1): Observable<DashboardResumen> {
    return this.dashboardService.getDashboardResumen(usuarioId);
  }

  getDistribucionGastos(usuarioId: number = 1): Observable<DistribucionResponse> {
    return this.transactionService.getDistribucionGastos(usuarioId);
  }

  calcularAnalisisIa(): Observable<AnalisisIaResponse> {
    return this.dashboardService.calcularAnalisisIa();
  }

  getHistorialAnalisis(): Observable<AnalisisHistorialItem[]> {
    return this.dashboardService.getHistorialAnalisis();
  }

  getTransaccionesRecientes(usuarioId: number = 1, limit: number = 20): Observable<any> {
    return this.transactionService.getTransaccionesRecientes(usuarioId, limit);
  }

  registrarTransaccionManual(descripcion: string, monto: number, tipo: string, usuarioId: number = 1): Observable<any> {
    return this.transactionService.registrarTransaccion(descripcion, monto, tipo, usuarioId);
  }

  addTransaction(descripcion: string, valor: number, tipo: string = 'GASTO', categoria?: string): Promise<any> {
    return this.transactionService.registrarTransaccion(descripcion, valor, tipo).toPromise();
  }

  deleteTransaction(id: string): Promise<any> {
    return this.transactionService.eliminarTransaccion(Number(id)).toPromise();
  }

  updateProfile(income: number, debtRatio: number, savingFrequency: string): Promise<any> {
    return this.profileService.guardarPerfilFinanciero(income, debtRatio, savingFrequency).toPromise();
  }

  getPerfilFinanciero(usuarioId: number = 1): Observable<any> {
    return this.profileService.getPerfilFinanciero(usuarioId);
  }

  guardarPerfilFinanciero(ingreso_mensual: number, nivel_endeudamiento: number, frecuencia_ahorro: string, usuarioId: number = 1): Observable<any> {
    return this.profileService.guardarPerfilFinanciero(ingreso_mensual, nivel_endeudamiento, frecuencia_ahorro);
  }
}
