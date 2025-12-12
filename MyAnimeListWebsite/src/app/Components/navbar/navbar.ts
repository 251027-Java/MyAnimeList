import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { SearchService } from '../../Service/search.service';
import { AuthService } from '../../Service/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {
  searchQuery: string = '';

  constructor(private searchService: SearchService, private authService: AuthService, private router: Router) {}

  onSearch() {
    this.searchService.setSearchQuery(this.searchQuery);
  }

  logout() {
    // If not logged in, stay on sign up page
    if (!this.authService.isLoggedIn()) {
      return;
    }

    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
