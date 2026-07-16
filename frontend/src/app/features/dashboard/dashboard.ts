import { Component, inject, ViewChild, ElementRef, AfterViewInit, OnDestroy, effect } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DecimalPipe, PercentPipe, NgClass } from '@angular/common';
import { FinanceService } from '../../core/services/finance';
import { Chart, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [RouterLink, DecimalPipe, PercentPipe],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})

export class DashboardComponent implements AfterViewInit, OnDestroy {
  protected financeService = inject(FinanceService);
  
  @ViewChild('chartCanvas') private chartCanvas!: ElementRef<HTMLCanvasElement>;
  private chartInstance: Chart | null = null;

  constructor() {
    // Reactive effect to update charts when state changes
    effect(() => {
      const expensesMap = this.financeService.categoryExpenses();
      if (this.chartInstance) {
        this.updateChart(expensesMap);
      }
    });
  }

  ngAfterViewInit(): void {
    const expensesMap = this.financeService.categoryExpenses();
    this.initChart(expensesMap);
  }

  ngOnDestroy(): void {
    if (this.chartInstance) {
      this.chartInstance.destroy();
    }
  }

  private initChart(dataMap: Record<string, number>) {
    const ctx = this.chartCanvas.nativeElement.getContext('2d');
    if (!ctx) return;

    const categories = Object.keys(dataMap);
    const values = Object.values(dataMap);

    this.chartInstance = new Chart(ctx, {
      type: 'doughnut',
      data: {
        labels: categories,
        datasets: [{
          data: values,
          backgroundColor: [
            '#6366f1', // Violet
            '#3b82f6', // Blue
            '#10b981', // Emerald
            '#f59e0b', // Amber
            '#ef4444', // Red
            '#ec4899', // Pink
            '#8b5cf6'  // Purple
          ],
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

  private updateChart(dataMap: Record<string, number>) {
    if (!this.chartInstance) return;
    
    const categories = Object.keys(dataMap);
    const values = Object.values(dataMap);

    this.chartInstance.data.labels = categories;
    this.chartInstance.data.datasets[0].data = values;
    this.chartInstance.update();
  }
}
