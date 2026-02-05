package com.server.util.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.MessageCodec;
import io.vertx.core.json.jackson.DatabindCodec;

import java.io.IOException;

public class SimpleJacksonCodec<T> implements MessageCodec<T, T> {

    private final ObjectMapper mapper = DatabindCodec.mapper();
    private final Class<T> clazz;

    public SimpleJacksonCodec(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void encodeToWire(Buffer buffer, T t) {
        try {
            buffer.appendBytes(mapper.writeValueAsBytes(t));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T decodeFromWire(int pos, Buffer buffer) {
        try {
            return mapper.readValue(buffer.getBytes(), clazz);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public T transform(T t) {
        return t; // used for local delivery; can be shallow copy if needed
    }

    @Override
    public String name() {
        return clazz.getSimpleName();
    }

    @Override
    public byte systemCodecID() {
        return -1; // custom codec
    }
}

