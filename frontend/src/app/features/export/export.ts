import { Component, inject, signal, OnInit } from '@angular/core';
import { FinanceService } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';

@Component({
  selector: 'app-export',
  standalone: true,
  imports: [],
  templateUrl: './export.html',
  styleUrl: './export.css',
})
export class ExportComponent implements OnInit {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  // States
  isGeneratingPDF = signal<boolean>(false);
  isGeneratingCSV = signal<boolean>(false);

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.getTransaccionesRecientes(userId, 100).subscribe();
    this.financeService.calcularAnalisisIa().subscribe();
  }

  exportPDF() {
    this.isGeneratingPDF.set(true);
    setTimeout(() => {
      this.isGeneratingPDF.set(false);
      
      const user = this.authService.currentUser();
      const userName = user?.nombre || 'Usuario';
      const userEmail = user?.email || 'demo@nocountry.com';
      const profile = this.financeService.analisisIaResult()?.perfil_financiero || this.financeService.riskProfile();
      const probability = ((this.financeService.analisisIaResult()?.probabilidad || this.financeService.probability()) * 100).toFixed(0);
      const kpi = this.financeService.kpiResumen();

      let recommendationsText = '';
      const recs = this.financeService.analisisIaResult()?.recomendaciones || this.financeService.recommendations();
      recs.forEach((r, idx) => {
        recommendationsText += `${idx + 1}. ${r}\n`;
      });

      const docContent = `====================================================\n` +
        `       REPORTE EJECUTIVO DE SALUD FINANCIERA        \n` +
        `                    FINANCE AI                      \n` +
        `====================================================\n\n` +
        `Fecha de Emisión: ${new Date().toLocaleString()}\n` +
        `Usuario: ${userName} (${userEmail})\n\n` +
        `--- RESUMEN DE DIAGNÓSTICO IA ---\n` +
        `Perfil Financiero: ${profile}\n` +
        `Probabilidad de Confianza: ${probability}%\n\n` +
        `--- MÉTRICAS CONSOLIDADAS ---\n` +
        `Ingresos Mensuales: $${kpi.ingresosMensuales.toFixed(2)}\n` +
        `Gastos Totales: $${kpi.gastosTotales.toFixed(2)}\n` +
        `Balance Neto: $${kpi.balanceNeto.toFixed(2)}\n` +
        `Tasa de Ahorro: ${kpi.tasaAhorro.toFixed(2)}%\n\n` +
        `--- RECOMENDACIONES DEL MOTOR ML ---\n` +
        `${recommendationsText}\n` +
        `====================================================\n`;

      const blob = new Blob([docContent], { type: 'text/plain;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Reporte_Ejecutivo_FinanceAI_${new Date().toISOString().split('T')[0]}.txt`;
      link.click();
      URL.revokeObjectURL(url);
    }, 1200);
  }

  exportCSV() {
    this.isGeneratingCSV.set(true);
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getTransaccionesRecientes(userId, 500).subscribe({
      next: (txs) => {
        this.isGeneratingCSV.set(false);
        const list = Array.isArray(txs) ? txs : this.financeService.transactions();
        
        let csvContent = 'id,fecha,descripcion,valor,categoria,tipo\n';
        list.forEach(tx => {
          csvContent += `"${tx.id}","${tx.fecha}","${tx.descripcion}",${tx.valor},"${tx.categoria}","${tx.tipo}"\n`;
        });

        const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
        const url = URL.createObjectURL(blob);
        const link = document.createElement('a');
        link.href = url;
        link.download = `Transacciones_FinanceAI_${new Date().toISOString().split('T')[0]}.csv`;
        link.click();
        URL.revokeObjectURL(url);
      },
      error: () => {
        this.isGeneratingCSV.set(false);
      }
    });
  }
}
