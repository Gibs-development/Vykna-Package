package io.xeros.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.Instant;

public final class InstantAdapter implements JsonSerializer<Instant>, JsonDeserializer<Instant> {
    @Override public JsonElement serialize(Instant src, Type t, JsonSerializationContext c) {
        return new JsonPrimitive(src.toString());
    }
    @Override public Instant deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
        return Instant.parse(json.getAsString());
    }
}
