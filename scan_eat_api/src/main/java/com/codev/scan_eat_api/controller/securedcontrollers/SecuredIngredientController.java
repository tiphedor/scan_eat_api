package com.codev.scan_eat_api.controller.securedcontrollers;

import com.codev.scan_eat_api.GeneralResponses;
import com.codev.scan_eat_api.Utils;
import com.codev.scan_eat_api.entities.Ingredient;
import com.codev.scan_eat_api.repository.IngredientRepository;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.json.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.io.IOException;
import java.util.Optional;

import static lombok.AccessLevel.PACKAGE;
import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping(value={"public/ingredients", "ingredients"}) //public/ingredient is only for testing
@FieldDefaults(level = PRIVATE, makeFinal = true)
@AllArgsConstructor(access = PACKAGE)
final class SecuredIngredientController {
    private long timeBetweenRefreshes = 3600000; //Time in milliseconds between an ingredient should be reverified in the foodfact database
    private final IngredientRepository ingredientRepository;
    /*@NonNull
    private UserAuthenticationService authentication;*/

    @PostMapping("/find")
    ResponseEntity<Object> find(@RequestParam("barcode") final long barcode) {
        try {
            Optional<Ingredient> ingredientOpt = ingredientRepository.findByBarcode(barcode);
            if(ingredientOpt.isPresent() && ingredientOpt.get().getLastRefresh()+timeBetweenRefreshes > System.currentTimeMillis())
            {
                return ResponseEntity.ok().body(ingredientOpt.get());
            }
            JSONObject jsonIngredient = Utils.getJsonResponseFromUrlStr("https://fr.openfoodfacts.org/api/v0/produit/" + barcode + ".json");
            Ingredient ingredient = new Ingredient();
            ingredient.setBarcode(jsonIngredient.getLong("code"));
            ingredient.setName(jsonIngredient.getJSONObject("product").getString("generic_name"));
            ingredient.setLastRefresh(System.currentTimeMillis());
            ingredient.setKcal100g((int)Math.round(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getInt("energy_100g")*0.239006));
            ingredient.setProteins100g(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getDouble("proteins_100g"));
            ingredient.setSugars100g(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getDouble("sugars_100g"));
            ingredient.setFat100g(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getDouble("fat_100g"));
            ingredient.setSalt100g(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getDouble("salt_100g"));
            ingredient.setFiber100g(jsonIngredient.getJSONObject("product").getJSONObject("nutriments").getDouble("fiber_100g"));

            ingredientRepository.save(ingredient);
            return ResponseEntity.ok().body(ingredient);
        } catch (IOException e) {
            e.printStackTrace();
            return GeneralResponses.internalServerError();
        }
    }
}
