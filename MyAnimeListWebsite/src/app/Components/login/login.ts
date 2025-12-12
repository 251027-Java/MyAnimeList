import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../Service/auth.service';
import { UserService } from '../../Service/user.service';

@Component({
  selector: 'app-login-component',
  imports: [FormsModule],
  templateUrl: './login.html',
  styleUrl: './login.css',
})
export class Login {

  constructor(private router: Router, private authService: AuthService, private userService: UserService) { }

  username: string = ""
  password: string = ""

  login() {
    this.authService.login(this.username, this.password).subscribe({
      next: (user) => {
        console.log("Login successful", user);
        this.router.navigateByUrl("/dashboard");
      },
      error: (err) => {
        console.error("Login failed", err);
        alert("Invalid username or password");
      }
    });
  }
}
