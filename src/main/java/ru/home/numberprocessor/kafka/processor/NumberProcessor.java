package ru.home.numberprocessor.kafka.processor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.state.WindowStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.home.numberprocessor.model.NumberProcessRuleModel;
import ru.home.numberprocessor.serde.StateNumberSerde;
import ru.home.numberprocessor.service.NumberProcessorRuleManager;
import ru.home.numberprocessor.service.StateNumber;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Service
@RequiredArgsConstructor
public class NumberProcessor {

    @Value("${spring.kafka.producer.topic}")
    private String topic;

    private final NumberProcessorRuleManager numberProcessorRuleManager;


    @Autowired
    public void buildPipeLine(StreamsBuilder builder) {
        KStream<String, String> numberStream = builder.stream(topic);
        Materialized<String, StateNumber, WindowStore<Bytes, byte[]>> materialized = createMaterialized();
        AtomicReference<NumberProcessRuleModel> numberProcessRuleModel = new AtomicReference<>(new NumberProcessRuleModel());
        numberStream
                .peek((key, value) -> numberProcessRuleModel.set(numberProcessorRuleManager.extractRule()))
                .peek((key, value) -> log.info(numberProcessRuleModel.get().getMessage() + " {}", value))//todo change to normally message
                .groupBy((key, value) -> "")
                .windowedBy(TimeWindows.ofSizeWithNoGrace(Duration.ofSeconds(getTimeOFWindowMinutes(numberProcessRuleModel))))
                .aggregate(
                        this::createStateNumber,
                        (key, value, aggregate) -> getProcess(value, aggregate),
                        materialized
                )
                .toStream()
                .mapValues(StateNumber::getAggregatedByTen)
                .flatMapValues(Map::entrySet)
                .peek((key, value) -> value.getValue().removeIf(list -> list.size() < numberProcessRuleModel.get().getLengthOfGroup()))
                .filterNot((key, value) -> value.getValue().isEmpty())
                .foreach((key, value) -> log.info("groups of ten: {}", value));
    }

    private Long getTimeOFWindowMinutes(AtomicReference<NumberProcessRuleModel> numberProcessRuleModel) {
        return numberProcessRuleModel.get().getTimeOFWindowMinutes();
    }

    private StateNumber createStateNumber() {
        return new StateNumber();
    }

    private StateNumber getProcess(String value, StateNumber aggregate) {
        return aggregate.process(Integer.valueOf(value));
    }

    private Materialized<String, StateNumber, WindowStore<Bytes, byte[]>> createMaterialized() {
        return Materialized.<String, StateNumber, WindowStore<Bytes, byte[]>>as("random-number-store")
                .withKeySerde(null)
                .withValueSerde(new StateNumberSerde());
    }

}
