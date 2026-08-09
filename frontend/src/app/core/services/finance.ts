import { Injectable, inject, signal, computed } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Transaction {
  id: string;
  descripcion: string;
  valor: number;
  categoria: string;
  fecha: string;
  tipo: string;
}

export interface DashboardResumen {
  ingresosMensuales: number;
  gastosTotales: number;
  balanceNeto: number;
  tasaAhorro: number;
}

export interface CategoriaDistribucion {
  categoria: string;
  montoTotal: number;
  porcentaje: number;
  color: string;
  icono: string;
}

export interface DistribucionResponse {
  modoContingencia: boolean;
  mensajeEstado: string;
  distribucion: CategoriaDistribucion[];
}

export interface AnalisisIaResponse {
  perfil_financiero: string;
  probabilidad: number;
  resumen_gastos?: Record<string, number>;
  recomendaciones: string[];
}

@Injectable({
  providedIn: 'root',
})
export class FinanceService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  // Real State Signals from API
  readonly kpiResumen = signal<DashboardResumen>({
    ingresosMensuales: 0,
    gastosTotales: 0,
    balanceNeto: 0,
    tasaAhorro: 0
  });

  readonly distribucionGastos = signal<CategoriaDistribucion[]>([]);
  readonly modoContingenciaIA = signal<boolean>(false);
  readonly mensajeEstadoIA = signal<string>('Servicio de IA Python Online');
  readonly analisisIaResult = signal<AnalisisIaResponse | null>(null);

  // Global State (Signals)
  readonly income = signal<number>(0);
  readonly debtRatio = signal<number>(0);
  readonly savingFrequency = signal<string>('');
  
  readonly transactions = signal<Transaction[]>([]);

  // Computed Values
  readonly totalExpenses = computed(() => {
    return this.transactions()
      .filter(t => t.tipo !== 'INGRESO')
      .reduce((acc, t) => acc + t.valor, 0);
  });

  readonly balance = computed(() => {
    // Total income from profile + any INGRESO transactions registered
    const totalIncomes = this.income() + this.transactions()
      .filter(t => t.tipo === 'INGRESO')
      .reduce((acc, t) => acc + t.valor, 0);
    return totalIncomes - this.totalExpenses();
  });

  readonly savingsRate = computed(() => {
    const left = this.balance();
    const totalIncomes = this.income() + this.transactions()
      .filter(t => t.tipo === 'INGRESO')
      .reduce((acc, t) => acc + t.valor, 0);
    if (totalIncomes <= 0) return 0;
    return Math.max(0, Math.round((left / totalIncomes) * 100));
  });

  readonly riskProfile = computed(() => {
    const debt = this.debtRatio();
    const rate = this.savingsRate();
    const totalIncomes = this.income() + this.transactions()
      .filter(t => t.tipo === 'INGRESO')
      .reduce((acc, t) => acc + t.valor, 0);
    const ratioExpenses = totalIncomes > 0 ? (this.totalExpenses() / totalIncomes) * 100 : 100;

    if (ratioExpenses > 95 || debt > 50 || rate < 0) {
      return 'En riesgo';
    } else if (ratioExpenses > 70 || debt > 30 || rate < 15) {
      return 'En observación';
    } else {
      return 'Saludable';
    }
  });

  readonly probability = computed(() => {
    const profile = this.riskProfile();
    if (profile === 'En riesgo') return 0.88;
    if (profile === 'En observación') return 0.74;
    return 0.95;
  });

  readonly categoryExpenses = computed(() => {
    const map: Record<string, number> = {};
    this.transactions()
      .filter(t => t.tipo !== 'INGRESO')
      .forEach(t => {
        const cat = t.categoria || 'Otros';
        map[cat] = (map[cat] || 0) + t.valor;
      });
    return map;
  });

  readonly recommendations = computed(() => {
    const list: string[] = [];
    const debt = this.debtRatio();
    const rate = this.savingsRate();
    const catMap = this.categoryExpenses();
    const currentIncome = this.income();

    if (rate < 10) {
      list.push('Tu tasa de ahorro es crítica. Intenta recortar al menos un 10% en gastos de Ocio.');
    }
    if (debt > 35) {
      list.push('Tu nivel de endeudamiento es elevado. Considera suspender compras a plazos o renegociar créditos.');
    }
    if (this.savingFrequency() === 'Baja') {
      list.push('Aumenta tu frecuencia de ahorro programando transferencias automáticas el día de pago.');
    }
    if (catMap['Ocio'] && catMap['Ocio'] > currentIncome * 0.15) {
      list.push('Detectamos gastos elevados en Ocio y Entretenimiento. Monitorea suscripciones mensuales duplicadas.');
    }
    if (catMap['Alimentación'] && catMap['Alimentación'] > currentIncome * 0.25) {
      list.push('Tus gastos en Alimentación superan el 25% de tu presupuesto. Planifica compras semanales en supermercado.');
    }

    if (list.length === 0) {
      list.push('Tus finanzas lucen estables y equilibradas. Mantén este patrón de consumo positivo.');
    }

    return list;
  });

  // Action Methods
  updateProfile(income: number, debtRatio: number, savingFrequency: string): Promise<void> {
    return new Promise((resolve) => {
      setTimeout(() => {
        this.income.set(income);
        this.debtRatio.set(debtRatio);
        this.savingFrequency.set(savingFrequency);
        resolve();
      }, 300);
    });
  }

  addTransaction(descripcion: string, valor: number, tipo: string = 'GASTO', categoryOverride?: string): Promise<Transaction> {
    return new Promise((resolve) => {
      setTimeout(() => {
        let categoria = categoryOverride || 'Otros';
        if (tipo === 'INGRESO') {
          categoria = 'Ingresos';
        } else if (!categoryOverride) {
          const desc = descripcion.toLowerCase();
          if (desc.includes('super') || desc.includes('comida') || desc.includes('cena') || desc.includes('restaurante') || desc.includes('mercado') || desc.includes('pizza')) {
            categoria = 'Alimentación';
          } else if (desc.includes('combustible') || desc.includes('gas') || desc.includes('uber') || desc.includes('taxi') || desc.includes('viaje')) {
            categoria = 'Transporte';
          } else if (desc.includes('netflix') || desc.includes('streaming') || desc.includes('cine') || desc.includes('ocio') || desc.includes('gimnasio') || desc.includes('juego')) {
            categoria = 'Ocio';
          } else if (desc.includes('farmacia') || desc.includes('medico') || desc.includes('salud') || desc.includes('clinica') || desc.includes('doctor')) {
            categoria = 'Salud';
          } else if (desc.includes('luz') || desc.includes('agua') || desc.includes('electricidad') || desc.includes('gas Natural') || desc.includes('internet') || desc.includes('telefono')) {
            categoria = 'Servicios';
          } else if (desc.includes('alquiler') || desc.includes('vivienda') || desc.includes('depa') || desc.includes('renta')) {
            categoria = 'Vivienda';
          } else if (desc.includes('curso') || desc.includes('escuela') || desc.includes('universidad') || desc.includes('libro')) {
            categoria = 'Educación';
          }
        }

        const newTx: Transaction = {
          id: Math.random().toString(36).substring(2, 9),
          descripcion,
          valor,
          categoria,
          fecha: new Date().toISOString().split('T')[0],
          tipo
        };

        this.transactions.update(txs => [newTx, ...txs]);
        resolve(newTx);
      }, 500);
    });
  }

  deleteTransaction(id: string) {
    this.transactions.update(txs => txs.filter(t => t.id !== id));
  }

  importCSV(fileContent: string): Promise<number> {
    return new Promise((resolve) => {
      setTimeout(() => {
        const lines = fileContent.split('\n');
        let importedCount = 0;
        const newTxs: Transaction[] = [];

        for (let i = 1; i < lines.length; i++) {
          const line = lines[i].trim();
          if (!line) continue;
          
          const parts = line.split(',');
          if (parts.length >= 2) {
            const descripcion = parts[0].replace(/"/g, '').trim();
            const valor = parseFloat(parts[1].trim());
            
            if (descripcion && !isNaN(valor)) {
              let categoria = 'Otros';
              const desc = descripcion.toLowerCase();
              if (desc.includes('super') || desc.includes('comida') || desc.includes('mercado')) categoria = 'Alimentación';
              else if (desc.includes('uber') || desc.includes('combustible')) categoria = 'Transporte';
              else if (desc.includes('netflix') || desc.includes('streaming')) categoria = 'Ocio';
              else if (desc.includes('luz') || desc.includes('internet')) categoria = 'Servicios';

              newTxs.push({
                id: Math.random().toString(36).substring(2, 9),
                descripcion,
                valor,
                categoria,
                fecha: new Date().toISOString().split('T')[0],
                tipo: 'GASTO'
              });
              importedCount++;
            }
          }
        }

        if (newTxs.length > 0) {
          this.transactions.update(txs => [...newTxs, ...txs]);
        }
        resolve(importedCount);
      }, 1000);
    });
  }

  // Real HTTP API Methods
  getDashboardResumen(usuarioId: number = 1): Observable<DashboardResumen> {
    return this.http.get<DashboardResumen>(`${this.apiUrl}/dashboard/resumen/${usuarioId}`).pipe(
      tap(res => this.kpiResumen.set(res))
    );
  }

  getDistribucionGastos(usuarioId: number = 1): Observable<DistribucionResponse> {
    return this.http.get<DistribucionResponse>(`${this.apiUrl}/transacciones/usuario/${usuarioId}/distribucion`).pipe(
      tap(res => {
        if (res && res.distribucion) {
          this.distribucionGastos.set(res.distribucion);
          this.modoContingenciaIA.set(res.modoContingencia);
          this.mensajeEstadoIA.set(res.mensajeEstado);
        }
      })
    );
  }

  calcularAnalisisIa(userId: number = 1): Observable<AnalisisIaResponse> {
    return this.http.post<AnalisisIaResponse>(`${this.apiUrl}/analisis-financiero`, { userId }).pipe(
      tap(res => {
        if (res) {
          this.analisisIaResult.set(res);
        }
      })
    );
  }

  getTransaccionesRecientes(usuarioId: number = 1, limit: number = 5): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/transacciones/usuario/${usuarioId}/recientes?limit=${limit}`).pipe(
      tap(res => {
        let content: any[] = [];
        if (Array.isArray(res)) {
          content = res;
        } else if (res && Array.isArray(res.content)) {
          content = res.content;
        }

        const mapped: Transaction[] = content.map((item: any) => ({
          id: String(item.id),
          descripcion: item.descripcion,
          valor: item.monto,
          categoria: item.categoriaNombre || 'Sin clasificar',
          fecha: item.fecha ? String(item.fecha).split('T')[0] : new Date().toISOString().split('T')[0],
          tipo: item.tipo || 'GASTO'
        }));

        this.transactions.set(mapped);
      })
    );
  }
}
