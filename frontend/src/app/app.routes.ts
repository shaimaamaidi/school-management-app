import { Routes } from '@angular/router';
import { Signup } from './pages/signup/signup';
import { Login } from './pages/login/login';
import { Home } from './pages/home/home';
import { authGuard } from './services/guard/auth-guard';

export const routes: Routes = [
    { path: '', redirectTo: '/login', pathMatch: 'full' },
    { path: 'login', component: Login },
    { path: 'admin', component: Home , canActivate: [authGuard]},
    { path: 'signup', component: Signup },
    { path: '**', redirectTo: '/login' }
];


