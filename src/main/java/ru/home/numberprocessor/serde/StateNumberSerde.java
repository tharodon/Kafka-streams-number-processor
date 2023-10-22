package ru.home.numberprocessor.serde;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serializer;
import ru.home.numberprocessor.StateNumber;

import java.io.IOException;

public class StateNumberSerde implements Serde<StateNumber> {
    @Override
    public Serializer<StateNumber> serializer() {
        return (topic, data) -> {
            try {
                return new ObjectMapper().writeValueAsBytes(data);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        };
    }

    @Override
    public Deserializer<StateNumber> deserializer() {
        return (topic, data) -> {
            try {
                return new ObjectMapper().readValue(data, StateNumber.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }
}
