package com.example.ilovepc.api.vo;

import lombok.Data;

@Data
public class ExtensionResult {
    private boolean result;
    private String fullType; //fileType + extType (image/png)
    private String fileType; //image 등등
    private String extType; // png, jpg 등등
}
