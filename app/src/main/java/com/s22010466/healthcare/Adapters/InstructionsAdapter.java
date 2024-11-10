package com.s22010466.healthcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.s22010466.healthcare.Models.AnalyzedInstruction;
import com.s22010466.healthcare.Models.InstructionsResponse;
import com.s22010466.healthcare.R;

import java.util.List;

public class InstructionsAdapter extends RecyclerView.Adapter<InstructionsViewHolder> {
    Context context;
    List<InstructionsResponse> list;

    public InstructionsAdapter(Context context, List<InstructionsResponse> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public InstructionsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsViewHolder(LayoutInflater.from(context).inflate(R.layout.list_recipe_instructions,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsViewHolder holder, int position) {
        holder.instruction_name.setText(list.get(position).name);
        holder.instruction_steps.setHasFixedSize(true);
        holder.instruction_steps.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.VERTICAL,false));
        InstructionsStepAdapter stepAdapter = new InstructionsStepAdapter(context,list.get(position).steps);
        holder.instruction_steps.setAdapter(stepAdapter);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
}

class InstructionsViewHolder extends RecyclerView.ViewHolder {
    TextView instruction_name;
    RecyclerView instruction_steps;

    public InstructionsViewHolder(@NonNull View itemView) {
        super(itemView);
        instruction_name = itemView.findViewById(R.id.textViewInstructionName);
        instruction_steps = itemView.findViewById(R.id.recyclerViewInstructionStep);
    }
}