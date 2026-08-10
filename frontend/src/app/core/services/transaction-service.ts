import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { environment } from '../../../environments/environment';
import { Transaction, CategoriaDistribucion, DistribucionResponse } from '../models/transaction.model';

@Injectable({
  providedIn: 'root',
})
export class TransactionService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  readonly transactions = signal<Transaction[]>([]);
  readonly distribucionGastos = signal<CategoriaDistribucion[]>([]);
  readonly modoContingenciaIA = signal<boolean>(false);
  readonly mensajeEstadoIA = signal<string>('Servicio de IA Python Online');

  getTransaccionesRecientes(usuarioId: number = 1, limit: number = 20): Observable<any> {
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

  registrarTransaccion(descripcion: string, monto: number, tipo: string, usuarioId: number = 1): Observable<any> {
    const body = { descripcion, monto, tipo };
    return this.http.post<any>(`${this.apiUrl}/transacciones/registrar`, body).pipe(
      tap(() => {
        this.getTransaccionesRecientes(usuarioId, 20).subscribe();
        this.getDistribucionGastos(usuarioId).subscribe();
      })
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

  eliminarTransaccion(id: number, usuarioId: number = 1): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/transacciones/${id}`).pipe(
      tap(() => {
        this.getTransaccionesRecientes(usuarioId, 20).subscribe();
        this.getDistribucionGastos(usuarioId).subscribe();
      })
    );
  }
}
