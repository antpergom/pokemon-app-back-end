package pokemonapp.backend.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import pokemonapp.backend.api.models.Pokemon;

@Repository
public interface PokemonRepository extends JpaRepository<Pokemon, Long> {

	//@Query(value = "SELECT pok.pok_iadesc FROM tpokemon pok WHERE pok.name LIKE %?1%", nativeQuery = true)
	//public List<Pokemon> test1(String name);
	
	//@Query("SELECT pok.description FROM tpokemon WHERE pok.name LIKE %?1%")
	//public String findDescriptionOfPokemonByName(String name);
	
}
