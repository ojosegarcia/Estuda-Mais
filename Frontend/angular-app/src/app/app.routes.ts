import { Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { PerfilComponent } from './pages/perfil/perfil';
import { MyClassesComponent } from './pages/my-classes/my-classes';
import { MainLayoutComponent } from './shared/layouts/main-layout/main-layout';
import { authGuard } from './core/guards/auth.guard';
import { BuscaComponent } from './pages/busca/busca';
import { ProfessorDetalheComponent } from './pages/professor-detalhe/professor-detalhe';
import { PerfilEditComponent } from './pages/perfil-edit/perfil-edit';

export const routes: Routes = [
  {
    path: 'auth',
    loadChildren: () => import('./auth/auth.routes').then(m => m.AUTH_ROUTES)
  },
  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [authGuard],
    children: [
      {
        path: '',
        redirectTo: 'home',
        pathMatch: 'full'
      },
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'perfil',
        component: PerfilComponent
      },
      {
        path: 'minhas-aulas',
        component: MyClassesComponent
      },
      {
        path: 'busca',
        component: BuscaComponent
      },
      {
        path: 'professor-detalhe/:id',
        component: ProfessorDetalheComponent
      },
      {
      path: 'perfil/editar', 
      component: PerfilEditComponent
    },
    ]
  },
  {
    path: '**',
    redirectTo: 'auth/login'
  }
];