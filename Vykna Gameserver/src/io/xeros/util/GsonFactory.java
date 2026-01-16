package io.xeros.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

public final class GsonFactory {
    private GsonFactory() {}

    public static Gson create() {
        return new GsonBuilder()
                .registerTypeAdapter(Instant.class, new InstantAdapter())
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .create();
    }
}
