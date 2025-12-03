import { Routes } from '@angular/router';
import { LoginComponent } from './components/login-component/login-component';
import { Dashboard } from './components/dashboard/dashboard';
import { Catch } from './components/catch/catch';

export const routes: Routes = [
    {
        path:"",
        component:LoginComponent
    },
    {
        path:"dashboard",
        component:Dashboard
    },
    {
        path:"catch",
        component:Catch
    }
];
