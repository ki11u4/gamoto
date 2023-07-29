package com.example.gamotolastminute;

import android.content.SearchRecentSuggestionsProvider;

public class ProductSuggestionProvider extends SearchRecentSuggestionsProvider {
    public static final String AUTHORITY = "com.example.gamotolastminute.provider.ProductSuggestionProvider";
    public static final int MODE = DATABASE_MODE_QUERIES;

    //comment

    public ProductSuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
