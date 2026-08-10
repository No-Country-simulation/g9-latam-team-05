import { Component, inject, ViewChild, ElementRef, OnInit, AfterViewInit, OnDestroy, effect } from '@angular/core';
import { DecimalPipe, DatePipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-history',
  standalone: true,
  imports: [DecimalPipe, DatePipe],
  templateUrl: './history.html',
  styleUrl: './history.css',
})
export class HistoryComponent implements OnInit, AfterViewInit, OnDestroy {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  @ViewChild('historyCanvas') private historyCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  constructor() {
    effect(() => {
      const kpi = this.financeService.kpiResumen();
      if (kpi && this.historyCanvas) {
        this.initChart();
      }
    });
  }

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.getHistorialAnalisis().subscribe();
    this.financeService.getTransaccionesRecientes(userId, 20).subscribe();
  }

  ngAfterViewInit(): void {
    this.initChart();
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  private initChart() {
    if (!this.historyCanvas) return;
    const ctx = this.historyCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.chartInstance) {
      this.chartInstance.destroy();
    }

    const currentIncome = this.financeService.kpiResumen().ingresosMensuales || this.financeService.income();
    const currentExpenses = this.financeService.kpiResumen().gastosTotales || this.financeService.totalExpenses();

    this.chartInstance = new Chart(ctx, {
      type: 'line',
      data: {
        labels: ['Mes -5', 'Mes -4', 'Mes -3', 'Mes -2', 'Mes Anterior', 'Mes Actual (Consolidado Real)'],
        datasets: [
          {
            label: 'Ingresos ($)',
            data: [
              Math.round(currentIncome * 0.9),
              Math.round(currentIncome * 0.95),
              Math.round(currentIncome * 0.95),
              Math.round(currentIncome * 0.98),
              currentIncome,
              currentIncome
            ],
            borderColor: '#10b981',
            backgroundColor: 'rgba(16, 185, 129, 0.08)',
            tension: 0.35,
            fill: true
          },
          {
            label: 'Gastos ($)',
            data: [
              Math.round(currentExpenses * 0.8),
              Math.round(currentExpenses * 0.85),
              Math.round(currentExpenses * 1.1),
              Math.round(currentExpenses * 0.9),
              Math.round(currentExpenses * 0.95),
              currentExpenses
            ],
            borderColor: '#ef4444',
            backgroundColor: 'rgba(239, 68, 68, 0.08)',
            tension: 0.35,
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
