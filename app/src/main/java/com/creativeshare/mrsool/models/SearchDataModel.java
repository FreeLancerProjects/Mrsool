package com.creativeshare.mrsool.models;

import java.io.Serializable;
import java.util.List;

public class SearchDataModel implements Serializable {
    private List<SearchModel> candidates;

    public List<SearchModel> getCandidates() {
        return candidates;
    }
}
