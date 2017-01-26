package com.jaynewstrom.json.runtime.internal;

import com.fasterxml.jackson.core.JsonGenerator;
import com.jaynewstrom.json.runtime.JsonSerializer;
import com.jaynewstrom.json.runtime.JsonSerializerFactory;

import java.io.IOException;
import java.util.Map;

public final class MapSerializer<T> implements JsonSerializer<Map<String, T>> {
    private final JsonSerializer<T> valueSerializer;

    public MapSerializer(JsonSerializer<T> valueSerializer) {
        this.valueSerializer = valueSerializer;
    }

    @Override public void serialize(Map<String, T> map, JsonGenerator jg, JsonSerializerFactory serializerFactory) throws IOException {
        jg.writeStartObject();
        for (Map.Entry<String, T> entry : map.entrySet()) {
            jg.writeFieldName(entry.getKey());
            valueSerializer.serialize(entry.getValue(), jg, serializerFactory);
        }
        jg.writeEndObject();
    }

    @Override public Class<?> modelClass() {
        throw new UnsupportedOperationException("The modelClass method shouldn't be used on MapSerializer.");
    }
}
