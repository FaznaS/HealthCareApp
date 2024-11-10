package com.s22010466.healthcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s22010466.healthcare.Models.ExtendedIngredient;
import com.s22010466.healthcare.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsViewHolder>{
    Context context;
    List<ExtendedIngredient> list;

    public IngredientsAdapter(Context context, List<ExtendedIngredient> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public IngredientsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IngredientsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_recipe_ingredients,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsViewHolder holder, int position) {
        holder.ingredient.setText(list.get(position).name);
        holder.ingredient.setSelected(true);
        holder.quantity.setText(list.get(position).original);
        holder.quantity.setSelected(true);
        Picasso.get().load("https://spoonacular.com/cdn/ingredients_100x100/"+list.get(position).image).into(holder.ingredientImg);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class IngredientsViewHolder extends RecyclerView.ViewHolder {
    TextView ingredient,quantity;
    ImageView ingredientImg;
    public IngredientsViewHolder(@NonNull View itemView) {
        super(itemView);
        ingredient = itemView.findViewById(R.id.textViewIngredient);
        quantity = itemView.findViewById(R.id.textViewQuantity);
        ingredientImg = itemView.findViewById(R.id.imageViewIngredients);
    }
}