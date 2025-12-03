import { Component, computed } from '@angular/core';
import { PokemonService } from '../../services/PokemonService';
import { CommonModule } from '@angular/common';
import { Pokemon } from '../../interfaces/pokemon'

@Component({
  selector: 'app-dashboard',
  imports: [CommonModule],
  templateUrl: './dashboard.html',
  styleUrl: './dashboard.css',
})
export class Dashboard {

  // constructor inject the service
  constructor(public pokemonService:PokemonService){}

  filteredPokemon = computed(() => {
    const searchTerm = this.pokemonService.searchTerm().toLowerCase().trim();
    const caughtPokemon = this.pokemonService.caughtPokemon();

    if(!searchTerm){
      return caughtPokemon;
    }

    return caughtPokemon.filter((pokemon: Pokemon) => {
      const nameMatch = pokemon.name.toLowerCase().includes(searchTerm);
      const idMatch = pokemon.id.toString().includes(searchTerm);

      return nameMatch || idMatch

    });
  });

}
