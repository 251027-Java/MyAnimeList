import { Routes } from '@angular/router';
import { Login } from './Components/login/login';
import { HomePage } from './Components/home-page/home-page';
import { Dashboard } from './Components/dashboard/dashboard';
import { authGuard } from './Service/auth.guard';

export const routes: Routes = [
    {
        path:"",
        component:Login
    },
    {
        path:"home",
        component:HomePage,
        canActivate: [authGuard]
    },
    {
        path:"dashboard",
        component:Dashboard,
        canActivate: [authGuard]
    }
];
