import { Injectable, signal, computed } from '@angular/core';

export interface Transaction {
  id: string;
  descripcion: string;
  valor: number;
  categoria: string;
  fecha: string;
}

@Injectable({
  providedIn: 'root',
})
export class FinanceService {
  // Global State (Signals)
  readonly income = signal<number>(4500);
  readonly debtRatio = signal<number>(25);
  readonly savingFrequency = signal<string>('Media');
  
  readonly transactions = signal<Transaction[]>([
    { id: '1', descripcion: 'Supermercado Plaza', valor: 420, categoria: 'Alimentación', fecha: '2026-07-10' },
    { id: '2', descripcion: 'Combustible Puma', valor: 300, categoria: 'Transporte', fecha: '2026-07-11' },
    { id: '3', descripcion: 'Suscripción Streaming', valor: 40, categoria: 'Ocio', fecha: '2026-07-12' },
    { id: '4', descripcion: 'Alquiler Residencia', valor: 1200, categoria: 'Vivienda', fecha: '2026-07-01' },
    { id: '5', descripcion: 'Farmacia Ahorro', valor: 150, categoria: 'Salud', fecha: '2026-07-05' },
    { id: '6', descripcion: 'Electricidad y Luz', valor: 110, categoria: 'Servicios', fecha: '2026-07-08' },
    { id: '7', descripcion: 'Curso Angular', valor: 250, categoria: 'Educación', fecha: '2026-07-09' }
  ]);

  // Computed Values
  readonly totalExpenses = computed(() => {
    return this.transactions().reduce((acc, t) => acc + t.valor, 0);
  });

  readonly balance = computed(() => {
    return this.income() - this.totalExpenses();
  });

  readonly savingsRate = computed(() => {
    const left = this.balance();
    if (this.income() <= 0) return 0;
    return Math.max(0, Math.round((left / this.income()) * 100));
  });

  readonly riskProfile = computed(() => {
    const debt = this.debtRatio();
    const rate = this.savingsRate();
    const ratioExpenses = (this.totalExpenses() / this.income()) * 100;

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
    this.transactions().forEach(t => {
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

    if (rate < 10) {
      list.push('Tu tasa de ahorro es crítica. Intenta recortar al menos un 10% en gastos de Ocio.');
    }
    if (debt > 35) {
      list.push('Tu nivel de endeudamiento es elevado. Considera suspender compras a plazos o renegociar créditos.');
    }
    if (this.savingFrequency() === 'Baja') {
      list.push('Aumenta tu frecuencia de ahorro programando transferencias automáticas el día de pago.');
    }
    if (catMap['Ocio'] && catMap['Ocio'] > this.income() * 0.15) {
      list.push('Detectamos gastos elevados en Ocio y Entretenimiento. Monitorea suscripciones mensuales duplicadas.');
    }
    if (catMap['Alimentación'] && catMap['Alimentación'] > this.income() * 0.25) {
      list.push('Tus gastos en Alimentación superan el 25% de tu presupuesto. Planifica compras semanales en supermercado.');
    }

    if (list.length === 0) {
      list.push('Tus finanzas lucen estables y equilibradas. Mantén este patrón de consumo positivo.');
    }

    return list;
  });

  // Action Methods
  addTransaction(descripcion: string, valor: number, categoryOverride?: string): Promise<Transaction> {
    return new Promise((resolve) => {
      setTimeout(() => {
        // Simple mock NLP logic to predict category
        let categoria = categoryOverride || 'Otros';
        if (!categoryOverride) {
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
          fecha: new Date().toISOString().split('T')[0]
        };

        this.transactions.update(txs => [newTx, ...txs]);
        resolve(newTx);
      }, 500); // simulate API latency
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
                fecha: new Date().toISOString().split('T')[0]
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
}
