package ru.home.numberprocessor.kafka.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadLocalRandom;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class NumberProducer {

    @Value("${spring.kafka.producer.topic}")
    private String topic;
    private final KafkaTemplate<String, String> template;

    @Scheduled(fixedDelay = 5 * 1000)
    public void sendRandomNumbers() {
        int randomNumber = ThreadLocalRandom.current().nextInt(1, 100);
        send(randomNumber);
    }

    private void send(int randomNumber) {
        Message<String> message = MessageBuilder.withPayload(String.valueOf(randomNumber))
                .setHeader(KafkaHeaders.TOPIC, topic)
                .build();
        template.send(message);
    }

}
