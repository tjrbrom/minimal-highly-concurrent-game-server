package com.gameserver.util.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.vertx.core.Vertx;
import io.vertx.core.json.jackson.DatabindCodec;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collection;

/**
 * Marker interface for objects that are allowed to be sent over the Vert.x event bus
 * using a Jackson-based message codec.
 *
 * <p>
 * Classes implementing this interface can be automatically discovered at startup
 * and registered with Vert.x so they can be serialized and deserialized transparently
 * when passed through the event bus.
 * </p>
 *
 * <p>
 * Registration is performed once at application startup and is not expected to
 * have any runtime performance impact.
 * </p>
 *
 * <p>
 * This interface does not define behavior; it exists solely to opt-in classes
 * to Vert.x serialization.
 * </p>
 */
public interface VxSerializable {

    static void registerDefaults() {
        ObjectMapper mapper = DatabindCodec.mapper();

        SimpleModule module = new SimpleModule();

        module.addSerializer(BigDecimal.class, new ToStringSerializer());
        module.addDeserializer(BigDecimal.class, new NumberDeserializers.BigDecimalDeserializer());
        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer());
        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
        mapper.registerModule(module);
    }

    static <T> void registerClass(Vertx vertx, Class<T> clazz) {

        if (VxSerializableRegistered.classes.contains(clazz)) return;

        vertx.eventBus().registerDefaultCodec(clazz, new SimpleJacksonCodec<>(clazz));
        VxSerializableRegistered.classes.add(clazz);
    }

    static void registerClasses(Vertx vertx, Collection<Class<?>> classList) {
        classList.forEach(clazz -> {
            if (VxSerializable.class.isAssignableFrom(clazz)) {
                registerClass(vertx, clazz);
            }
        });
    }

}
