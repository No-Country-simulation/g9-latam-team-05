export interface DashboardResumen {
  ingresosMensuales: number;
  gastosTotales: number;
  balanceNeto: number;
  tasaAhorro: number;
}

export interface AnalisisIaResponse {
  perfil_financiero: string;
  probabilidad: number;
  resumen_gastos?: Record<string, number>;
  recomendaciones: string[];
}

export interface AnalisisHistorialItem {
  id: number;
  userId: number;
  ingresoMensual: number;
  nivelEndeudamiento: number;
  frecuenciaAhorro: string;
  perfilResultado: string;
  probabilidad: number;
  fechaAnalisis: string;
  recomendaciones: string[];
}
