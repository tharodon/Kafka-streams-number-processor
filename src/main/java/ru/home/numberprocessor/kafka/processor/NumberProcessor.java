package ru.home.numberprocessor.kafka.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.home.numberprocessor.StateNumber;
import ru.home.numberprocessor.serde.StateNumberSerde;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NumberProcessor {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    @Value("${spring.kafka.streams.grouping-time.min}")
    private Long minutes;

    @Autowired
    public void buildPipeLine(StreamsBuilder builder) {
        KStream<String, String> numberStream = builder.stream(topic);
        numberStream
                .peek((key, value) -> log.info("starting process... {}", value))
                .groupBy((key, value) -> "")
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofMinutes(minutes)))
                .aggregate(
                        StateNumber::new,
                        (key, value, aggregate) -> aggregate.process(Integer.valueOf(value)),
                        Materialized.with(null, new StateNumberSerde())
                )
                .toStream()
                .mapValues(StateNumber::getAggregatedByTen)
                .flatMapValues(Map::entrySet)
                .peek((key, value) -> value.getValue().removeIf(list -> list.size() < 3))
                .filterNot((key, value) -> value.getValue().isEmpty())
                .foreach(((key, value) -> log.info("groups of ten: {}", value)));
    }

}
