import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./shell/shell').then(m => m.ShellComponent),
    children: [
      { path: 'dashboard', loadComponent: () => import('./features/dashboard/dashboard').then(m => m.DashboardComponent) },
      { path: 'transacciones', loadComponent: () => import('./features/transactions/transactions').then(m => m.TransactionsComponent) },
      { path: 'simulador', loadComponent: () => import('./features/simulator/simulator').then(m => m.SimulatorComponent) },
      { path: 'perfil-financiero', loadComponent: () => import('./features/profile/profile').then(m => m.ProfileComponent) },
      { path: 'recomendaciones', loadComponent: () => import('./features/recommendations/recommendations').then(m => m.RecommendationsComponent) },
      { path: 'historial', loadComponent: () => import('./features/history/history').then(m => m.HistoryComponent) },
      { path: 'configuracion', loadComponent: () => import('./features/settings/settings').then(m => m.SettingsComponent) },
      { path: 'exportar', loadComponent: () => import('./features/export/export').then(m => m.ExportComponent) },
      { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
    ]
  },
  { path: 'login', loadComponent: () => import('./features/auth/login/login').then(m => m.LoginComponent) },
  { path: 'register', loadComponent: () => import('./features/auth/register/register').then(m => m.RegisterComponent) },
  { path: 'onboarding', loadComponent: () => import('./features/onboarding/onboarding').then(m => m.OnboardingComponent) },
  { path: '**', redirectTo: 'dashboard' }
];

