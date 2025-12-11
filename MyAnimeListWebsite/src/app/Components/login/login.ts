import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login-component',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})

export class Login {

  constructor(private router: Router) { }

  username: string = ""
  password: string = ""

  login() {
    if (this.username === "username" && this.password === "password") {
      //switch URLs
      this.router.navigateByUrl("/home")
    } else {
      alert("Invalid username or password")
    }
  }

}
