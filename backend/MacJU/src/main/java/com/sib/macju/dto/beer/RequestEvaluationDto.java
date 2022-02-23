package com.sib.macju.dto.beer;


import lombok.Data;

import java.util.List;

@Data
public class RequestEvaluationDto {
    private int rate;
    private List<Long> aromaHashTags;
    private List<Long> flavorHashTags;
}
