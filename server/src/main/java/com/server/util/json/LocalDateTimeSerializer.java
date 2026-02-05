package com.server.util.json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

public class LocalDateTimeSerializer extends StdSerializer<LocalDateTime> {

    private static final ZoneOffset DEFAULT_ZONE = ZoneOffset.UTC;

    public LocalDateTimeSerializer() {
        super(LocalDateTime.class);
    }

    @Override
    public void serialize(final LocalDateTime value, final JsonGenerator generator, final SerializerProvider provider) throws IOException {

        if (value == null)
            generator.writeNumber(0);
        else
            generator.writeNumber(value.toInstant(DEFAULT_ZONE).toEpochMilli());

    }

}
