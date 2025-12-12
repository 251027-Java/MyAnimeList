import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserWatchlistRequest {
    userId: number;
    animeId: number;
}

export interface UserRatingRequest {
    userId: number;
    animeId: number;
    rating: number;
}

export interface UserAnimeWatchedRequest {
    userId: number;
    animeId: number;
    watched: boolean;
}

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private apiUrl = 'http://localhost:8080/user';

    constructor(private http: HttpClient) { }

    register(username: string, password: string): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, { username, password });
    }

    addToWatchlist(userId: number, animeId: number): Observable<void> {
        const request: UserWatchlistRequest = { userId, animeId };
        return this.http.post<void>(`${this.apiUrl}/watchlist/add`, request);
    }

    removeFromWatchlist(userId: number, animeId: number): Observable<void> {
        const request: UserWatchlistRequest = { userId, animeId };
        return this.http.post<void>(`${this.apiUrl}/watchlist/remove`, request);
    }

    getWatchlist(userId: number): Observable<any[]> {
        return this.http.get<any[]>(`${this.apiUrl}/watchlist/${userId}`);
    }

    setRating(userId: number, animeId: number, rating: number): Observable<void> {
        const request: UserRatingRequest = { userId, animeId, rating };
        return this.http.post<void>(`${this.apiUrl}/rating`, request);
    }

    getRating(userId: number, animeId: number): Observable<any> {
        return this.http.get<any>(`${this.apiUrl}/rating/${userId}/${animeId}`);
    }

    setWatched(userId: number, animeId: number, watched: boolean): Observable<void> {
        const request: UserAnimeWatchedRequest = { userId, animeId, watched };
        return this.http.post<void>(`${this.apiUrl}/watched`, request);
    }

    isWatched(userId: number, animeId: number): Observable<boolean> {
        return this.http.get<boolean>(`${this.apiUrl}/watched/${userId}/${animeId}`);
    }
}
