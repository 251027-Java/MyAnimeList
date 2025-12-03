import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-component',
  imports: [FormsModule],
  templateUrl: './login-component.html',
  styleUrl: './login-component.css',
})
export class LoginComponent {

  constructor(private router:Router){}

  username:string=""
  password:string=""

  login(){
    if(this.username === "user" && this.password === "password"){
      //switch URLs
      this.router.navigateByUrl("/dashboard")
    } else {
      alert("Invalid username or password")
    }
  }

}
