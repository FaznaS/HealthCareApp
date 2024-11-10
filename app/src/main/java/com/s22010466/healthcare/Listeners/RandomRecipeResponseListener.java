package com.s22010466.healthcare.Listeners;

import com.s22010466.healthcare.Models.RandomRecipeApiResponse;

public interface RandomRecipeResponseListener {
    void didFetch(RandomRecipeApiResponse response, String message);
    void didError(String message);
}
