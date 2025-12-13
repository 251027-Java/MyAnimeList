import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, NavigationEnd } from '@angular/router';
import { SearchService } from '../../Service/search.service';
import { AuthService } from '../../Service/auth.service';
import { CommonModule } from '@angular/common';
import { filter } from 'rxjs/operators';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [RouterModule, FormsModule, CommonModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar implements OnInit, OnDestroy {
  searchQuery: string = '';
  isAnimeListPage: boolean = false;
  private routerSubscription?: Subscription;

  constructor(private searchService: SearchService, private authService: AuthService, private router: Router) {}

  ngOnInit() {
    // Check initial route
    this.checkRoute(this.router.url);
    
    // Subscribe to route changes
    this.routerSubscription = this.router.events.pipe(
      filter(event => event instanceof NavigationEnd)
    ).subscribe((event: any) => {
      this.checkRoute(event.url);
    });
  }

  ngOnDestroy() {
    this.routerSubscription?.unsubscribe();
  }

  private checkRoute(url: string) {
    const wasAnimeListPage = this.isAnimeListPage;
    this.isAnimeListPage = url === '/animelist' || url.startsWith('/animelist?') || url.startsWith('/animelist#');
    
    // Clear search bar when:
    // 1. Navigating away from anime list page
    // 2. Navigating TO anime list page from another page (to start fresh)
    if (this.searchQuery && ((!this.isAnimeListPage) || (!wasAnimeListPage && this.isAnimeListPage))) {
      this.searchQuery = '';
      this.searchService.setSearchQuery('');
    }
  }

  onSearch() {
    // Only apply search when on anime list page
    if (this.isAnimeListPage) {
      this.searchService.setSearchQuery(this.searchQuery);
    }
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
