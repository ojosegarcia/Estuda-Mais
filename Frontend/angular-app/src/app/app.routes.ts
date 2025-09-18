import { Routes } from '@angular/router';
import { Auth } from './pages/auth/auth';
import { Home } from './pages/home/home';

export const routes: Routes = [
  { path: '', component: Auth },
  { path: 'home', component: Home }
];
