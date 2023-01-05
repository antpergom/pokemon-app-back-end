package pokemonapp.backend.api.controllers;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import pokemonapp.backend.api.models.Pokemon;
import pokemonapp.backend.api.services.PokemonService;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/v1")
public class PokemonController {
	
	@Autowired
	private PokemonService serv;
	
	@GetMapping("/pokemon")
	public List<Pokemon> getAllPokemons(){
		return serv.getAllPokemons();
	}
	
	@GetMapping("/pokemon/{pokemonId}")
	public ResponseEntity<Pokemon> findPokemonById(@PathVariable("pokemonId") Long pokemonId){
		Optional<Pokemon> pok = Optional.ofNullable(serv.getPokemonByNumber(pokemonId));
		
		if (pok.isPresent()) {
			return new ResponseEntity<>(pok.get(), HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	
	@PostMapping("/users")
	public Pokemon addPokemon(@RequestBody Pokemon pok) {
		return serv.addPokemon(pok);
	}
	
	@DeleteMapping("/pokemon/{pokemonId}")
	public void deletePokemon(@PathVariable("pokemonId") Long pokemonId){
		serv.deletePokemon(pokemonId);
	}
}










