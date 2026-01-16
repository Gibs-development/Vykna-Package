package io.xeros.util;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;

public final class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
    @Override public JsonElement serialize(LocalDate src, Type t, JsonSerializationContext c) {
        return new JsonPrimitive(src.toString());
    }
    @Override public LocalDate deserialize(JsonElement json, Type t, JsonDeserializationContext c) {
        return LocalDate.parse(json.getAsString());
    }
}
