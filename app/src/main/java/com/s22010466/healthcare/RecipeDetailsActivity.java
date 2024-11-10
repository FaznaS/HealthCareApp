package com.s22010466.healthcare;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Html;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.s22010466.healthcare.Adapters.IngredientsAdapter;
import com.s22010466.healthcare.Adapters.InstructionsAdapter;
import com.s22010466.healthcare.Listeners.InstructionsListener;
import com.s22010466.healthcare.Listeners.RecipeDetailsListener;
import com.s22010466.healthcare.Models.InstructionsResponse;
import com.s22010466.healthcare.Models.RecipeDetailsResponse;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecipeDetailsActivity extends AppCompatActivity {
    int id;
    TextView textViewMealName,textViewMealSource,textViewMealSummary;
    ImageView imageViewMealImg;
    RecyclerView recyclerViewIngredients,recyclerViewInstructions;
    RequestManager manager;
    IngredientsAdapter ingredientsAdapter;
    InstructionsAdapter instructionsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        textViewMealName = findViewById(R.id.textViewMealName);
        textViewMealSource = findViewById(R.id.textViewMealSource);
        textViewMealSummary = findViewById(R.id.textViewMealSummary);
        imageViewMealImg = findViewById(R.id.imageViewMealImg);
        recyclerViewIngredients = findViewById(R.id.recyclerViewIngredients);
        recyclerViewInstructions = findViewById(R.id.recyclerViewInstructions);

        id = Integer.parseInt(getIntent().getStringExtra("id"));

        manager = new RequestManager(this);
        manager.getRecipeDetails(recipeDetailsListener,id);
        manager.getInstructions(instructionsListener,id);
    }

    private final RecipeDetailsListener recipeDetailsListener = new RecipeDetailsListener() {
        @Override
        public void didFetch(RecipeDetailsResponse response, String message) {
            textViewMealName.setText(response.title);
            textViewMealSource.setText("By: " + response.sourceName);

            // Convert HTML summary to a Spanned object and set it to the TextView
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                textViewMealSummary.setText(Html.fromHtml(response.summary, Html.FROM_HTML_MODE_LEGACY));
            } else {
                textViewMealSummary.setText(Html.fromHtml(response.summary));
            }

            Picasso.get().load(response.image).into(imageViewMealImg);

            //Initialize Recycler View to display ingredients
            recyclerViewIngredients.setHasFixedSize(true);

            //Displaying the ingredients in a grid view with 2 columns
            recyclerViewIngredients.setLayoutManager(new GridLayoutManager(RecipeDetailsActivity.this,2));

            ingredientsAdapter = new IngredientsAdapter(RecipeDetailsActivity.this,response.extendedIngredients);
            recyclerViewIngredients.setAdapter(ingredientsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    };

    private final InstructionsListener instructionsListener = new InstructionsListener() {
        @Override
        public void didFetch(List<InstructionsResponse> response, String message) {
            // Initialize Recycler View to display the method of preparation
            recyclerViewInstructions.setHasFixedSize(true);

            // Displaying the instructions in a Linear Layout
            recyclerViewInstructions.setLayoutManager(new LinearLayoutManager(RecipeDetailsActivity.this,LinearLayoutManager.VERTICAL,false));

            instructionsAdapter = new InstructionsAdapter(RecipeDetailsActivity.this, response);
            recyclerViewInstructions.setAdapter(instructionsAdapter);
        }

        @Override
        public void didError(String message) {
            Toast.makeText(RecipeDetailsActivity.this,message,Toast.LENGTH_SHORT).show();
        }
    };
}