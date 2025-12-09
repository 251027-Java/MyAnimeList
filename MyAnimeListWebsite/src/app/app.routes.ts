import { Routes } from '@angular/router';
import { Login } from './Components/login/login';
import { HomePage } from './Components/home-page/home-page';


export const routes: Routes = [
    {
        path:"",
        component:Login
    },
    {
        path:"home",
        component:HomePage
    }
];
