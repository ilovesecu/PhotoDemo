package com.example.ilovepc.api.vo;

import lombok.Data;

@Data
public class ExtensionResult {
    private boolean result;
    private String fullType; //fileType + extType
    private String fileType;
    private String extType;
}
