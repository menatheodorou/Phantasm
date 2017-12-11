package com.phantasm.phantasm.main.api.vma;

import java.util.ArrayList;

/**
 * Created by pete.s on 11/10/15.
 */
public class PTAudioCategoryResponse_unused {
    public ArrayList<PTAudioCategory_unused> record;

    public PTAudioCategory_unused getCategory(int position) {
        return record.get(position);
    }

    public void setCategories(PTAudioCategory_unused[] categories) {
        this.record = new ArrayList<>();
        for (PTAudioCategory_unused cat : categories) {
            this.record.add(cat);
        }
    }

    public int count() {
        return record.size();
    }
}
