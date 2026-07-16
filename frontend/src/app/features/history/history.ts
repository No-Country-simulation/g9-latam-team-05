import { Component, inject, ViewChild, ElementRef, AfterViewInit, OnDestroy } from '@angular/core';
import { DecimalPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [DecimalPipe],
  templateUrl: './history.html',
  styleUrl: './history.css',
})

export class HistoryComponent implements AfterViewInit, OnDestroy {
  protected financeService = inject(FinanceService);

  @ViewChild('historyCanvas') private historyCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  ngAfterViewInit(): void {
    this.initChart();
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  private initChart() {
    const ctx = this.historyCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    this.chartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio (Proyección)'],
        datasets: [
          {
            label: 'Ingresos',
            data: [4200, 4200, 4200, 4500, 4500, 4500, this.financeService.income()],
            borderColor: '#10b981',
            backgroundColor: 'rgba(16, 185, 129, 0.05)',
            tension: 0.3,
            fill: true
          },
          {
            label: 'Gastos',
            data: [3100, 2900, 3600, 3100, 3400, 3900, this.financeService.totalExpenses()],
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.05)',
            tension: 0.3,
            fill: true
          }
        ]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            labels: {
              color: '#94a3b8',
              font: { family: 'Inter', size: 12 }
            }
          }
        },
        scales: {
          x: {
            grid: { color: 'rgba(255, 255, 255, 0.04)' },
            ticks: { color: '#64748b' }
          },
          y: {
            grid: { color: 'rgba(255, 255, 255, 0.04)' },
            ticks: { color: '#64748b' }
          }
        }
      }
    });
  }
}
