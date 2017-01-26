package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.jaynewstrom.json.runtime.JsonDeserializer;
import com.jaynewstrom.json.runtime.JsonDeserializerFactory;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public final class MapDeserializer<T> implements JsonDeserializer<Map<String, T>> {
    private final JsonDeserializer<T> valueDeserializer;

    public MapDeserializer(JsonDeserializer<T> valueDeserializer) {
        this.valueDeserializer = valueDeserializer;
    }

    @Override public Map<String, T> deserialize(JsonParser jp, JsonDeserializerFactory deserializerFactory) throws IOException {
        // Ensure we are in the correct state.
        if (jp.currentToken() != JsonToken.START_OBJECT) {
            throw new IOException("Expected data to start with an Object");
        }
        Map<String, T> map = new LinkedHashMap<>();
        // Parse fields as they come.
        while (jp.nextToken() != JsonToken.END_OBJECT) {
            String fieldName = jp.getCurrentName();
            jp.nextToken();
            map.put(fieldName, valueDeserializer.deserialize(jp, deserializerFactory));
        }
        return map;
    }

    @Override public Class<?> modelClass() {
        throw new UnsupportedOperationException("The modelClass method shouldn't be used on MapDeserializer.");
    }
}
