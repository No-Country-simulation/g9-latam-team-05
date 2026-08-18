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

  /**
   * Consulta las transacciones recientes paginadas desde el backend.
   */
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
          categoria: item.categoriaNombre || item.categoria || 'Sin clasificar',
          fecha: item.fecha ? String(item.fecha).split('T')[0] : new Date().toISOString().split('T')[0],
          tipo: item.tipo || 'GASTO'
        }));

        this.transactions.set(mapped);
      })
    );
  }

  /**
   * Registra una transacción y la clasifica inmediatamente con IA.
   */
  registrarTransaccion(descripcion: string, monto: number, tipo: string, usuarioId: number = 1): Observable<any> {
    const body = { descripcion, monto, tipo };
    return this.http.post<any>(`${this.apiUrl}/transacciones/registrar`, body).pipe(
      tap((res: any) => {
        if (res) {
          const newTx: Transaction = {
            id: String(res.id || Date.now()),
            descripcion: res.descripcion || descripcion,
            valor: res.monto || monto,
            categoria: res.categoriaNombre || res.categoria || 'Sin clasificar',
            fecha: res.fecha ? String(res.fecha).split('T')[0] : new Date().toISOString().split('T')[0],
            tipo: res.tipo || tipo
          };
          // Actualización reactiva inmediata en la UI (Optimistic UI)
          this.transactions.update(current => [newTx, ...current.filter(t => t.id !== newTx.id)]);
        }
        // Sincronización completa de transacciones y distribución de gráficos
        this.getTransaccionesRecientes(usuarioId, 20).subscribe();
        this.getDistribucionGastos(usuarioId).subscribe();
      })
    );
  }

  /**
   * Obtiene la distribución agrupada por categorías calculada por el backend.
   */
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

  /**
   * Elimina una transacción y sincroniza el estado.
   */
  eliminarTransaccion(id: number, usuarioId: number = 1): Observable<any> {
    return this.http.delete<any>(`${this.apiUrl}/transacciones/${id}`).pipe(
      tap(() => {
        this.transactions.update(current => current.filter(t => t.id !== String(id)));
        this.getTransaccionesRecientes(usuarioId, 20).subscribe();
        this.getDistribucionGastos(usuarioId).subscribe();
      })
    );
  }
}
