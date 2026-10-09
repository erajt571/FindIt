package com.findit.service;

public interface TextSimilarityProvider {
    double similarity(String left, String right);
}
