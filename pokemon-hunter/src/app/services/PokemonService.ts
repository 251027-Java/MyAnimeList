import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { map, Observable } from 'rxjs';
import { signal, WritableSignal } from '@angular/core';
import { Pokemon } from "../interfaces/pokemon";


@Injectable({
  providedIn: 'root',
})
export class PokemonService {

  caughtPokemon:WritableSignal<Pokemon[]> = signal<Pokemon[]>([])
  searchTerm:WritableSignal<string> = signal('')

  //TODO: Array that holds caught pokemon 

  //Inject HTTPClient so we can make HTTP requests 
  constructor(private http:HttpClient){}


  getPokemon():Observable<Pokemon>{
    const randomNum:number = Math.floor(Math.random()*1025) +1;


    return this.http.get<Pokemon>(`https://pokeapi.co/api/v2/pokemon/${randomNum}`).pipe(
      map<any, Pokemon>(data => ({
        id:data.id,
        name:data.name,
        sprite:data.sprites.front_default
      }))
    )
  }

}
