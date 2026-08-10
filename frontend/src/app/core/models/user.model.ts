export interface Usuario {
  id: number;
  nombre: string;
  email: string;
  estado?: string;
  fechaRegistro?: string;
}

export type User = Usuario;

export interface RegisterRequest {
  nombre: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  id: number;
  nombre: string;
  email: string;
  estado: string;
  fechaRegistro: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  usuario: Usuario;
}
