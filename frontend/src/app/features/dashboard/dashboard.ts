import { Component, inject, ViewChild, ElementRef, AfterViewInit, OnDestroy, OnInit, effect } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe, PercentPipe } from '@angular/common';
import { FinanceService, CategoriaDistribucion } from '../../core/services/finance';
import { AuthService } from '../../core/services/auth';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, DecimalPipe, PercentPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class DashboardComponent implements OnInit, AfterViewInit, OnDestroy {
  protected financeService = inject(FinanceService);
  protected authService = inject(AuthService);
  
  @ViewChild('chartCanvas') private chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  constructor() {
    // Effect to update chart when API distribution signal changes
    effect(() => {
      const items = this.financeService.distribucionGastos();
      if (this.chartInstance && items.length > 0) {
        this.updateChart(items);
      }
    });
  }

  ngOnInit(): void {
    const userId = this.authService.currentUser()?.id || 1;
    this.financeService.getDashboardResumen(userId).subscribe();
    this.financeService.calcularAnalisisIa(userId).subscribe();
    this.financeService.getTransaccionesRecientes(userId, 5).subscribe();
    this.financeService.getDistribucionGastos(userId).subscribe({
      next: (res) => {
        if (this.chartInstance && res && res.distribucion) {
          this.updateChart(res.distribucion);
        }
      }
    });
  }

  ngAfterViewInit(): void {
    const items = this.financeService.distribucionGastos();
    this.initChart(items);
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  private initChart(items: CategoriaDistribucion[]) {
    if (!this.chartCanvas) return;
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const categories = items.map(i => i.categoria);
    const values = items.map(i => i.montoTotal);
    const colors = items.map(i => i.color || '#3b82f6');

    this.chartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: categories.length ? categories : ['Sin datos'],
        datasets: [{
          data: values.length ? values : [1],
          backgroundColor: colors.length ? colors : ['#64748b'],
          borderColor: 'rgba(7, 10, 19, 0.6)',
          borderWidth: 2,
        }]
      },
      options: {
        responsive: true,
        maintainAspectRatio: false,
        plugins: {
          legend: {
            position: 'right',
            labels: {
              color: '#94a3b8',
              font: {
                family: 'Inter',
                size: 12
              },
              padding: 15
            }
          }
        },
        cutout: '70%'
      }
    });
  }

  private updateChart(items: CategoriaDistribucion[]) {
    if (!this.chartInstance) return;
    
    const categories = items.map(i => i.categoria);
    const values = items.map(i => i.montoTotal);
    const colors = items.map(i => i.color || '#3b82f6');

    this.chartInstance.data.labels = categories;
    this.chartInstance.data.datasets[0].data = values;
    this.chartInstance.data.datasets[0].backgroundColor = colors;
    this.chartInstance.update();
  }
}
