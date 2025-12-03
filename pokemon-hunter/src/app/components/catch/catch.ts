import { Component, signal, WritableSignal } from '@angular/core';
import { PokemonService } from '../../services/PokemonService';
import { Pokemon } from '../../interfaces/pokemon';
import { CommonModule, TitleCasePipe } from '@angular/common';

@Component({
  selector: 'app-catch',
  imports: [TitleCasePipe, CommonModule],
  templateUrl: './catch.html',
  styleUrl: './catch.css',
})
export class Catch {

  constructor(private pokemonService:PokemonService){}

  pokemon: WritableSignal<Pokemon> = signal({id:0, name:"", sprite:""});

  // ngOnInit - a component lifecycle method that lets us
  // define logic to fire when the component renders
  ngOnInit() {
    this.getPokemon()
  }

  getPokemon(){

    this.pokemonService.getPokemon().subscribe(data => {
      console.log(data)
      
      this.pokemon.set(data)
    })

  }

  catchPokemon(){
    this.pokemonService.caughtPokemon.update(pokemon => [...pokemon, this.pokemon()])
    this.pokemon.set({id:0, name:"", sprite:""})
  }

}
