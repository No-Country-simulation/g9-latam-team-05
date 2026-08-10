import { Component, inject, OnInit, ViewChild, ElementRef, AfterViewInit, OnDestroy, effect } from '@angular/core';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [DecimalPipe, PercentPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);

  @ViewChild('chartCanvas') private chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  constructor() {
    // Escuchar reactivamente cambios en la señal de distribución de gastos
    effect(() => {
      const distribucion = this.financeService.distribucionGastos();
      if (distribucion && distribucion.length > 0 && this.chartCanvas) {
        this.updateChart(distribucion);
      }
    });
  }

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.getDistribucionGastos(userId).subscribe({
      next: (res) => {
        if (res && res.distribucion) {
          this.updateChart(res.distribucion);
        }
      }
    });
    this.financeService.getTransaccionesRecientes(userId, 5).subscribe();
    this.financeService.calcularAnalisisIa().subscribe();
  }

  ngAfterViewInit(): void {
    const distribucion = this.financeService.distribucionGastos();
    if (distribucion && distribucion.length > 0) {
      this.updateChart(distribucion);
    }
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  private updateChart(distribucion: any[]): void {
    if (!this.chartCanvas) return;
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    if (this.chartInstance) {
      this.chartInstance.destroy();
    }

    const labels = distribucion.map(d => d.categoria);
    const data = distribucion.map(d => d.montoTotal);
    const colors = distribucion.map((d, i) => d.color || this.getDistribucionColor(i));

    this.chartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: labels,
        datasets: [{
          data: data,
          backgroundColor: colors,
          borderColor: '#1e293b',
          borderWidth: 2,
          hoverOffset: 6
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            display: false
          },
          tooltip: {
            callbacks: {
              label: (context) => {
                const val = context.raw as number;
                const total = data.reduce((a, b) => a + b, 0);
                const pct = total > 0 ? ((val / total) * 100).toFixed(1) : 0;
                return ` ${context.label}: $${val.toFixed(2)} (${pct}%)`;
              }
            }
          }
        },
        cutout: '70%'
      }
    });
  }

  getDistribucionColor(index: number): string {
    const colors = ['#3b82f6', '#10b981', '#f59e0b', '#ef4444', '#8b5cf6', '#ec4899', '#6366f1'];
    return colors[index % colors.length];
  }
}
