package ru.home.numberprocessor.model;

import lombok.*;

@With
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NumberProcessRuleModel {

    @Builder.Default
    private Long lengthOfGroup = 3L;

    @Builder.Default
    private Long timeOFWindowMinutes = 120L;

    private String message;

}
