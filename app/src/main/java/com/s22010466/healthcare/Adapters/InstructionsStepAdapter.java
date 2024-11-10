package com.s22010466.healthcare.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.s22010466.healthcare.Models.Step;
import com.s22010466.healthcare.R;

import java.util.List;

public class InstructionsStepAdapter extends RecyclerView.Adapter<InstructionsStepViewHolder> {
    Context context;
    List<Step> stepsList;

    public InstructionsStepAdapter(Context context, List<Step> stepsList) {
        this.context = context;
        this.stepsList = stepsList;
    }

    @NonNull
    @Override
    public InstructionsStepViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new InstructionsStepViewHolder(LayoutInflater.from(context).inflate(R.layout.list_instructions_steps,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull InstructionsStepViewHolder holder, int position) {
        // number is integer
        String stepNumber = String.valueOf(stepsList.get(position).number);
        holder.instructionStepNumber.setText(stepNumber + ".");
        holder.instructionStepName.setText(stepsList.get(position).step);
    }

    @Override
    public int getItemCount() {
        return stepsList.size();
    }
}

class InstructionsStepViewHolder extends RecyclerView.ViewHolder {
    TextView instructionStepNumber,instructionStepName;

    public InstructionsStepViewHolder(@NonNull View itemView) {
        super(itemView);
        instructionStepNumber = itemView.findViewById(R.id.textViewInstructionStepNumber);
        instructionStepName = itemView.findViewById(R.id.textViewInstructionStepName);
    }
}