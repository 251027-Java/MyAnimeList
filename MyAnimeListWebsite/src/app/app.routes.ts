import { Routes } from '@angular/router';
import { LoginComponent } from './Components/login/login';
import { AnimeList } from './Components/anime-list/anime-list';
import { Dashboard } from './Components/dashboard/dashboard';
import { authGuard } from './Service/auth.guard';
import { SignUpComponent } from './Components/sign-up/sign-up';
import { Home } from './Components/home/home';

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
        path:"animelist",
        component:AnimeList,
        canActivate: [authGuard]
    },
    {
        path:"dashboard",
        component:Dashboard,
        canActivate: [authGuard]
    },
       {
        path:"home",
        component:Home,
        canActivate: [authGuard]
    }
];
