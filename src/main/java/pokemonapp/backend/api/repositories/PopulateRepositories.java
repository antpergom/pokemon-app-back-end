package pokemonapp.backend.api.repositories;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import pokemonapp.backend.api.models.Pokemon;
import pokemonapp.backend.api.services.PokemonService;

@Component
public class PopulateRepositories implements CommandLineRunner {

	@Autowired
	private PokemonService serv;

	@Override
	public void run(String... args) throws Exception {
		try {
			// populatePokemon();
			// writePokemonDescriptionAI();
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

	}

	public void populatePokemon() {
		try {
			// Create the HttpURLConnection
			URL url = new URL("https://pokeapi.co/api/v2/pokemon/?limit=1200&offset=0");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			// Set the request method and headers
			connection.setRequestMethod("GET");
			connection.setRequestProperty("Accept", "application/json");

			// Read the response
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = reader.readLine()) != null) {
				response.append(line);
			}

			List<Pokemon> pokToPopulate = JsonToListOfPokemon(response.toString());
			for (Pokemon pok : pokToPopulate) {
				serv.addPokemon(pok);
			}
			System.out.println(pokToPopulate);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	private void writePokemonDescriptionAI() {
		try {
			List<Pokemon> pokemons = serv.getAllPokemons();
			for(/* Pokemon pok : pokemons */ int i = 0; i<150; i++) {
				HttpURLConnection connection = null;
				try {
					if(pokemons.get(i).getDescription() == null || pokemons.get(i).getDescription() == "") {
						Pokemon p = pokemons.get(i);
						String token = "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"; 
					    URL url = new URL("https://api.openai.com/v1/completions");
					    connection = (HttpURLConnection) url.openConnection();
					    connection.setRequestMethod("POST");
					    connection.setRequestProperty("Content-Type", "application/json");
					    connection.setRequestProperty("Authorization", "Bearer " + token);

					    //String temp = "\"temperature\":0.9";
					    String prompt = "Escribe una curiosidad sobre el pokemon " + p.getName() + " en menos de 60 palabras";
					    String requestData = buildPrompt(prompt, 1.0f, 80);// "{\"prompt\":\"Write a curiosity about the pokemon " + p.getName() + " in less than 60 words\"," + temp + ", \"max_tokens\": 80}";
					    connection.setDoOutput(true);
					    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
					    wr.writeBytes(requestData);
					    wr.flush();
					    wr.close();

					    int responseCode = connection.getResponseCode();
					    if(String.valueOf(responseCode).startsWith("2")) {
							// Read the response
							BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
							StringBuilder response = new StringBuilder();
							String line;
							while ((line = reader.readLine()) != null) {
								response.append(line);
							}
							//TODO: hay que formatear la respuesta para la descripción
							String newDescription = JsonToOpenAIResponse(response.toString());
							serv.addPokemon(new Pokemon(p.getId(), p.getNumber(), p.getName(), p.getType(), newDescription.trim()));
					    }
					}

				} catch (Exception e) {
				      e.printStackTrace();
				} finally {
				      connection.disconnect();
				}
			}
		}catch(Exception e) {
			
		}
	}

	private List<Pokemon> JsonToListOfPokemon(String jsonString) {
		List<Pokemon> res = new ArrayList<Pokemon>();
		Map<String, Object> objectsMap = new HashMap<String, Object>();
		// List<> resultsMap = new HashMap<String, Object>();
		try {
			ObjectMapper mapper = new ObjectMapper();
			mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
			List<Map<String, Object>> listObjectsArray = mapper.readValue(jsonString, List.class);
			if (listObjectsArray.size() > 0) {
				objectsMap = listObjectsArray.get(0);
				if (objectsMap.get("results") instanceof List) {
					List<Object> listPokGeneric = (List) objectsMap.get("results");
					for (int i = 0; i < listPokGeneric.size(); i++) {
						Map<String, Object> pokemonEntry = (Map) listPokGeneric.get(i);
						Pokemon pok = new Pokemon(Integer.toUnsignedLong(i), i, pokemonEntry.get("name").toString(),
								null, "");
						res.add(pok);
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return res;
	}
	
	private String JsonToOpenAIResponse(String jsonString) {
		String res = "";
		try {
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> objRes = mapper.readValue(jsonString, Map.class);
			List<Map<String, Object>> aux = (List) objRes.get("choices");
			res += aux.get(0).get("text");
			System.out.println(res);
			//res += choices.get("text").toString();
		}catch(Exception e) {
			e.printStackTrace();
		}
		return res;

	}
	
	// values not normalized would be capped
	private String buildPrompt(String prompt, Float temperature, Integer maxTokens) {
		String res = "";
		try {
			res += "{";
			res += "\"model\": \"text-davinci-003\"";
			res += ", \"prompt\": \"" + prompt + "\"";
			Float tempNormalized = (temperature == null || temperature > 1.0f) ? 1.0f : (temperature < 0.0f) ? 0.0f : temperature;
			res += ", \"temperature\": " + tempNormalized.toString();
			if(maxTokens < 0 || maxTokens > 600) {
				throw new Exception();
			}
			res += ", \"max_tokens\": " + maxTokens;
			
			res += "}";
		}catch(Exception e) {
			System.out.println("Too many or not enough tokens");
			e.printStackTrace();
		}

		return res;
	}

	/*
	 * private List<Pokemon> JsonToListOfPokemon(String jsonString){ List<Pokemon>
	 * res = new ArrayList<Pokemon>(); Map<String, Object> listObjects = new
	 * HashMap<String, Object>(); try { ObjectMapper mapper = new ObjectMapper();
	 * mapper.enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
	 * List<Map<String, Object>> listObjectsArray = mapper.readValue(jsonString,
	 * List.class); if(listObjectsArray.size() > 0 ) { listObjects =
	 * listObjectsArray.get(0); Object testAux = listObjects.get("abilities");
	 * Object testAux2 = listObjects.get(""); } }catch(Exception e) {
	 * System.out.println(e.getMessage()); } return res; }
	 * 
	 */

}
