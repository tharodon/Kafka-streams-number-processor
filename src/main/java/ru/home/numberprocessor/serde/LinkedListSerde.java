package ru.home.numberprocessor.serde;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.Serializer;

import java.util.LinkedList;
import java.util.Map;

public class LinkedListSerde extends Serdes.WrapperSerde<LinkedList<Integer>> {

    public LinkedListSerde() {
        super(new Serializer<>() {
            @Override
            public byte[] serialize(String topic, LinkedList<Integer> data) {
                try {
                    return new ObjectMapper().writeValueAsBytes(data);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {
            }
        }, new Deserializer<>() {
            @Override
            public void configure(Map<String, ?> configs, boolean isKey) {
            }

            @Override
            public LinkedList<Integer> deserialize(String topic, byte[] data) {
                if (data == null) {
                    return null;
                }
                try {
                    return new ObjectMapper().readValue(data, new TypeReference<>() {
                    });
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void close() {
            }
        });
    }
}
