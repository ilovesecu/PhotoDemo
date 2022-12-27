package com.example.ilovepc.api.vo;

import lombok.Data;

@Data
public class PhotoResult  extends UploadResult{
    //이미지 업로드 수
    private int uploadTotCnt = 0;
}
