import { Routes } from '@angular/router';
import { LoginComponent } from './Components/login/login';
import { HomePage } from './Components/home-page/home-page';
import { Dashboard } from './Components/dashboard/dashboard';
import { authGuard } from './Service/auth.guard';
import { SignUpComponent } from './Components/sign-up/sign-up';

export const routes: Routes = [
    {
        path: "",
        component: SignUpComponent
    },
    {
        path:"login",
        component:LoginComponent
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
