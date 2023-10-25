package ru.home.numberprocessor.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.home.numberprocessor.document.NumberProcessRule;
import ru.home.numberprocessor.model.NumberProcessRuleModel;
import ru.home.numberprocessor.repository.NumberProcessRuleRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class NumberProcessorRuleManager {

    private final NumberProcessRuleRepository numberProcessRuleRepository;

    private final List<NumberProcessRuleModel> numberProcessRuleModelsCache = new ArrayList<>();

    private LocalDateTime startedTime;

    private int offset = 0;


    public NumberProcessRuleModel extractRule() {
        if (numberProcessRuleModelsCache.isEmpty()) {
            updateCache();
        }
        if (isExpireRule()) {
            offset = offset + 1 >= numberProcessRuleModelsCache.size() ? 0 : offset + 1;
            updateStartedTime();
        }
        return numberProcessRuleModelsCache.get(offset);
    }

    private boolean isExpireRule() {
        NumberProcessRuleModel currentRule = numberProcessRuleModelsCache.get(offset);
        return LocalDateTime.now().minusSeconds/*todo minutes*/(currentRule.getTimeOFWindowMinutes()).isAfter(startedTime);
    }

    private void updateCache() {
        List<NumberProcessRule> numberProcessRules = numberProcessRuleRepository.findAll();
        numberProcessRules.stream()
                .map(this::toNumberProcessRuleModel)
                .forEach(numberProcessRuleModelsCache::add);
        updateStartedTime();
    }

    private void updateStartedTime() {
        startedTime = LocalDateTime.now();
    }

    private NumberProcessRuleModel toNumberProcessRuleModel(NumberProcessRule numberProcessRule) {
        return NumberProcessRuleModel.builder()
                .lengthOfGroup(numberProcessRule.getLengthOfGroup())
                .message(numberProcessRule.getMessage())
                .timeOFWindowMinutes(numberProcessRule.getTimeOFWindowMinutes())
                .build();
    }
}
