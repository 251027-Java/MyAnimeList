import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { tap } from 'rxjs/operators';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Store userId
  private userId: number | null = null;
  private username: string | null = null;
  private password: string | null = null;
  private apiUrl = 'http://localhost:8080/user';

  constructor(private http: HttpClient) {
    // Load credentials from sessionStorage if they exist
    this.username = sessionStorage.getItem('username');
    this.password = sessionStorage.getItem('password');
    const storedId = sessionStorage.getItem('userId');
    if (storedId) {
      this.userId = parseInt(storedId, 10);
    }
  }

  // Changed to return Observable to handle async login
  login(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { username, password }).pipe(
      tap(user => {
        this.username = user.username;
        this.password = user.password;
        this.userId = user.userId;
        sessionStorage.setItem('username', user.username);
        sessionStorage.setItem('password', user.password); // Note: storing password in session storage is not secure but keeping valid for now as per existing pattern
        sessionStorage.setItem('userId', user.userId.toString());
        // Mark that user has logged in before
        localStorage.setItem('hasLoggedInBefore', 'true');
      })
    );
  }

  logout(): void {
    this.username = null;
    this.password = null;
    this.userId = null;
    sessionStorage.removeItem('username');
    sessionStorage.removeItem('password');
    sessionStorage.removeItem('userId');
    // Note: hasLoggedInBefore flag is intentionally NOT cleared, so returning users go to login instead of signup
  }

  isLoggedIn(): boolean {
    return this.userId !== null;
  }

  hasLoggedInBefore(): boolean {
    return localStorage.getItem('hasLoggedInBefore') === 'true';
  }

  getUserId(): number | null {
    return this.userId;
  }

  getAuthHeader(): string | null {
    if (this.username && this.password) {
      const credentials = btoa(`${this.username}:${this.password}`);
      return `Basic ${credentials}`;
    }
    return null;
  }
}
