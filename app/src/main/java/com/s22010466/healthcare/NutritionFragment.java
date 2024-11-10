package com.s22010466.healthcare;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.s22010466.healthcare.Adapters.RandomRecipeAdapter;
import com.s22010466.healthcare.Listeners.RandomRecipeResponseListener;
import com.s22010466.healthcare.Listeners.RecipeClickListener;
import com.s22010466.healthcare.Models.RandomRecipeApiResponse;

import java.util.ArrayList;
import java.util.List;


public class NutritionFragment extends Fragment {
    //ProgressDialog dialog;
    RequestManager manager;
    RandomRecipeAdapter randomRecipeAdapter;
    RecyclerView recyclerView;
    List<String> tags = new ArrayList<>();
    SearchView searchView;

    public NutritionFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_nutrition, container, false);

//        dialog = new ProgressDialog(this);
//        dialog.setTitle("Loading...");

        searchView = view.findViewById(R.id.searchView);

        manager = new RequestManager(requireContext());
        manager.getRandomRecipes(randomRecipeResponseListener,tags);

        recyclerView = view.findViewById(R.id.recyclerViewRecipes);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),1));
        //dialog.show();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                tags.clear();
                tags.add(query);
                manager.getRandomRecipes(randomRecipeResponseListener,tags);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return view;
    }

    private final RandomRecipeResponseListener randomRecipeResponseListener = new RandomRecipeResponseListener() {
        @Override
        public void didFetch(RandomRecipeApiResponse response, String message) {
            randomRecipeAdapter = new RandomRecipeAdapter(getContext(),response.recipes,recipeClickListener);
            recyclerView.setAdapter(randomRecipeAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(getContext(),message,Toast.LENGTH_SHORT).show();
        }
    };

    private final RecipeClickListener recipeClickListener = new RecipeClickListener() {
        @Override
        public void onRecipeClicked(String id) {
            startActivity(new Intent(requireContext(), RecipeDetailsActivity.class)
                    .putExtra("id",id));
        }
    };

}