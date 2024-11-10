package com.s22010466.healthcare.Listeners;

import com.s22010466.healthcare.Models.RecipeDetailsResponse;

public interface RecipeDetailsListener {
    void didFetch(RecipeDetailsResponse response,String message);
    void didError(String message);
}
