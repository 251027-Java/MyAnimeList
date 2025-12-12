import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-SignUp',
  templateUrl: './sign-up.html',
  styleUrls: ['./sign-up.css'],
  imports: [CommonModule, FormsModule]
})
export class SignUpComponent {
  username!: string;
  password!: string;

  constructor(private http: HttpClient, private router: Router) {
    this.username = '';
    this.password = '';
  }
  signUp() {
    const body = {
      username: this.username,
      password: this.password
    };

    this.http.post('http://localhost:8080/signup', body)
      .subscribe(
        response => {
          // Redirect to dashboard or home page
          this.router.navigate(['/home']);
        },
        error => {
          console.error(error);
        }
      );
  }
}