import { Routes } from '@angular/router';
import { LoginComponent } from './Components/login/login';
import { HomePage } from './Components/home-page/home-page';
import { SignUpComponent } from './Components/sign-up/sign-up';

export const routes: Routes = [
    {
        path: "",
        component: SignUpComponent
    },
    {
        path:"",
        component:LoginComponent
    },
    {
        path:"home",
        component:HomePage
    }
];
