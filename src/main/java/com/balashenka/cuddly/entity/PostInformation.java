package com.balashenka.cuddly.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class PostInformation {
    private Map<String, Boolean> urls;
    private String caption;
}
