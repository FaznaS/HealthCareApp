package com.s22010466.healthcare.Listeners;

import com.s22010466.healthcare.Models.InstructionsResponse;

import java.util.List;

public interface InstructionsListener {
    void didFetch(List<InstructionsResponse> response, String message);
    void didError(String message);
}
