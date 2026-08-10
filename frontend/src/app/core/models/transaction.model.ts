export interface Transaction {
  id: string;
  descripcion: string;
  valor: number;
  categoria: string;
  fecha: string;
  tipo: string;
}

export type TransactionItem = Transaction;

export interface CategoriaDistribucion {
  categoria: string;
  montoTotal: number;
  porcentaje: number;
  color: string;
  icono: string;
}

export interface DistribucionResponse {
  modoContingencia: boolean;
  mensajeEstado: string;
  distribucion: CategoriaDistribucion[];
}
