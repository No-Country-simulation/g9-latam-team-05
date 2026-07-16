import { Component, inject, signal } from '@angular/core';
import { FinanceService } from '../../core/services/finance';

@Component({
  selector: 'app-export',
  standalone: true,
  imports: [],
  templateUrl: './export.html',
  styleUrl: './export.css',
})
export class ExportComponent {
  protected financeService = inject(FinanceService);

  // States
  isGeneratingPDF = signal<boolean>(false);
  isGeneratingCSV = signal<boolean>(false);

  exportPDF() {
    this.isGeneratingPDF.set(true);
    setTimeout(() => {
      this.isGeneratingPDF.set(false);
      
      // Simulate file download by creating a fake text file representing PDF report
      const docContent = `REPORT OF FINANCIAL HEALTH - FINANCE AI\n\nProfile: ${this.financeService.riskProfile()}\nIncome: $${this.financeService.income()}\nTotal Expenses: $${this.financeService.totalExpenses()}\nSavings Rate: ${this.financeService.savingsRate()}%\n\n`;
      const blob = new Blob([docContent], { type: 'text/plain;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Reporte_FinanceAI_${new Date().toISOString().split('T')[0]}.pdf`;
      link.click();
      URL.revokeObjectURL(url);
    }, 1500);
  }

  exportCSV() {
    this.isGeneratingCSV.set(true);
    setTimeout(() => {
      this.isGeneratingCSV.set(false);
      
      let csvContent = 'fecha,descripcion,valor,categoria\n';
      this.financeService.transactions().forEach(tx => {
        csvContent += `"${tx.fecha}","${tx.descripcion}",${tx.valor},"${tx.categoria}"\n`;
      });

      const blob = new Blob([csvContent], { type: 'text/csv;charset=utf-8' });
      const url = URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `Transacciones_FinanceAI_${new Date().toISOString().split('T')[0]}.csv`;
      link.click();
      URL.revokeObjectURL(url);
    }, 1000);
  }
}
