package com.example.ilovepc.api.vo;

import lombok.Data;

/********************************************************************************************** 
 * @FileName : PhotoDetail.java 
 * @Date : 2022-12-27 
 * @작성자 : 정승주 
 * @설명 : 이미지 파일 하나하나에 대한 업로드
 **********************************************************************************************/ 
@Data
public class PhotoDetail extends UploadResult{
    private String imageFile; //업로드된 이미지 파일 요약? (파일명|WIDTH|HEIGHT)
    private String encImageName; //인코딩 명
}
