package ru.home.numberprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.system.SystemProperties;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableKafka
@EnableKafkaStreams
@EnableScheduling
@SpringBootApplication
public class KafkaStreamsNumberProcessorApplication {

    public static void main(String[] args) {
        System.setProperty("java.io.tmpdir", "C:\\Users\\tosha\\Projects\\kafka-streams-number-processor\\build");
        SpringApplication.run(KafkaStreamsNumberProcessorApplication.class, args);
    }

}
