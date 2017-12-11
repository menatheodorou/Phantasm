package com.phantasm.phantasm.main.api.lorempixel;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevinfinn on 5/21/15.
 */
public class PTCategoryResponse {
    private List<PTCategory> categories;

    public PTCategory getCategory(int position) {
        return categories.get(position);
    }

    public void setCategories(PTCategory[] categories) {
        this.categories = new ArrayList<>();
        for (PTCategory cat : categories) {
            this.categories.add(cat);
        }
    }

    public int count() {
        return categories.size();
    }
}
