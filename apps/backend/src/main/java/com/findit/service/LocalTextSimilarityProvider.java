package com.findit.service;

import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LocalTextSimilarityProvider implements TextSimilarityProvider {
    @Override
    public double similarity(String left, String right) {
        Set<String> first = tokens(left);
        Set<String> second = tokens(right);
        if (first.isEmpty() || second.isEmpty()) return 0.0;
        long common = first.stream().filter(second::contains).count();
        return (double) common / (first.size() + second.size() - common);
    }

    private Set<String> tokens(String text) {
        return Arrays.stream(text.toLowerCase(Locale.ROOT).split("[^a-z0-9]+"))
                .filter(token -> token.length() > 2)
                .collect(Collectors.toSet());
    }
}
