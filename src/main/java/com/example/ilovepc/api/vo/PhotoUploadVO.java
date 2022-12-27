package com.example.ilovepc.api.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class PhotoUploadVO {
    private int memNo; //사용자 일련번호
    //파일 확장자
    private String fileName = "";
    private String fileExt = "";

    //파일 추가경로
    private String temp="0";
    //MultipartFile 리스트
    @JsonIgnore
    private MultipartFile[] files;
}
