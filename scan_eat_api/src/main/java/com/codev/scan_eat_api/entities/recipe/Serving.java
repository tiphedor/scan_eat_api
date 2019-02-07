package com.codev.scan_eat_api.entities.recipe;

import java.util.ArrayList;
import java.util.List;

public class Serving {
    private List<RecipeContent> content;
    private int servingSizeKcal = 500;

    public Serving(Recipe recipe, int personAmount) {
        List<RecipeContent> baseRcList = constructBaseUnitMeasures(recipe.getIngredients());
        float totalKcal = baseRcList.stream()
                .map(i -> i.getIngredient().getKcal100g()*(i.getQuantity()/100)) //Divided by 100 because kcal100g/ml and the base units are g or ml
                .reduce(0f, (i1,i2) -> i1+i2);
        float servingPercentage = ((servingSizeKcal*recipe.getServingModifier())/totalKcal)*personAmount;

        content = new ArrayList<>();
        for(RecipeContent rc : recipe.getIngredients()) {
            RecipeContent newRc = new RecipeContent();
            newRc.setQuantity(rc.getQuantity()*servingPercentage);
            newRc.setIngredient(rc.getIngredient());
            newRc.setUnit(rc.getUnit());
            content.add(newRc);
        }
    }

    public List<RecipeContent> getContent() {
        return content;
    }

    public void setContent(List<RecipeContent> content) {
        this.content = content;
    }

    private List<RecipeContent> constructBaseUnitMeasures(List<RecipeContent> rcList) {
        List<RecipeContent> newRcList = new ArrayList<>();
        for(RecipeContent rc : rcList)
        {
            if(rc.getUnit().getBaseUnit() == null) {
                newRcList.add(new RecipeContent(rc));
            } else {
                RecipeContent newRc = new RecipeContent();
                newRc.setRecipe(rc.getRecipe());
                newRc.setIngredient(rc.getIngredient());
                newRc.setQuantity(rc.getQuantity()*rc.getUnit().getBaseUnitConversion());
                newRc.setUnit(rc.getUnit().getBaseUnit());
                newRcList.add(newRc);
            }
        }
        return newRcList;
    }
}
