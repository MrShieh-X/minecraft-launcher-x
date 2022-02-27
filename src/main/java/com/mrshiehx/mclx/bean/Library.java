package com.mrshiehx.mclx.bean;

import org.json.JSONObject;

import java.util.Objects;

public record Library(JSONObject libraryJSONObject) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Library library = (Library) o;
        return libraryJSONObject.optString("name").equals(library.libraryJSONObject.optString("name"));
    }

    @Override
    public int hashCode() {
        return Objects.hash(libraryJSONObject.optString("name"));
    }
}
