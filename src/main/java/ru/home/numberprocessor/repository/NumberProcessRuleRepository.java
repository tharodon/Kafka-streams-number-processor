package ru.home.numberprocessor.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.home.numberprocessor.document.NumberProcessRule;

public interface NumberProcessRuleRepository extends MongoRepository<NumberProcessRule, String> {
}
