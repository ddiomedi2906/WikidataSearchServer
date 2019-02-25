package com.server.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonConverter {

    private final Gson gson;

    public JsonConverter() {

        gson = new GsonBuilder().create();
    }
    public String getJson() {

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("query", "hola mundo");
        jsonObject.addProperty("matching", 34534);
        jsonObject.addProperty("topResults", 1);

        JsonArray array = new JsonArray();
        JsonObject subresult = new JsonObject();
        subresult.addProperty("number", 1);
        subresult.addProperty("entity", "Q12312");
        subresult.addProperty("prefLabel", "\"Papastruka\"");
        subresult.addProperty("altLabels", "\"Papas\" \"struka\"");
        array.add(subresult);

        jsonObject.add("documents", subresult);

/*
        JsonArray jarray = gson.toJsonTree(cities).getAsJsonArray();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("cities", jarray);
*/
        return jsonObject.toString();
    }

}
