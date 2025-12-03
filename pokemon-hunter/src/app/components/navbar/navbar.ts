import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { PokemonService } from '../../services/PokemonService';

@Component({
  selector: 'app-navbar',
  imports: [RouterModule, FormsModule],
  templateUrl: './navbar.html',
  styleUrl: './navbar.css',
})
export class Navbar {

  constructor(public pokemonService:PokemonService){}

  onSearch(searchVal:string){
    this.pokemonService.searchTerm.set(searchVal);
  }

}
