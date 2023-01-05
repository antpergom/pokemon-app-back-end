package pokemonapp.backend.api.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pokemonapp.backend.api.models.Pokemon;
import pokemonapp.backend.api.repositories.PokemonRepository;

@Service
public class PokemonService {

    @Autowired
    PokemonRepository repo;

    public Long getPokemonCount() {
    	return repo.count();
    }
    
    public List<Pokemon> getAllPokemons(){
        return repo.findAll();
    }
    
    public Pokemon getPokemonByNumber(Long number) {
    	return repo.getReferenceById(number);
    }
    
    
    public Pokemon addPokemon(Pokemon pok) {
    	return repo.save(pok);
    }
    
    public List<Pokemon> addAllPokemon(List<Pokemon> pokList) {
    	return repo.saveAll(pokList);
    }
    
    public void deletePokemon(Long pokemonId) {
    	repo.deleteById(pokemonId);
    }
            

}
