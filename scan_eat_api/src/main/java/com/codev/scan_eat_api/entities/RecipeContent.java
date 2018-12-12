package com.codev.scan_eat_api.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Map;

@Entity
@Table(name = "recipe_ingredient")
public class RecipeContent{

    @EmbeddedId
    private RecipeContentId recipeContentId;


    @Column(name = "quantity")
    private float quatity;

    public RecipeContent() {
    }

    public RecipeContentId getRecipeContentId() {
        return recipeContentId;
    }

    public void setRecipeContentId(RecipeContentId recipeContentId) {
        this.recipeContentId = recipeContentId;
    }

    public float getQuatity() {
        return quatity;
    }

    public void setQuatity(float quatity) {
        this.quatity = quatity;
    }

    
}
