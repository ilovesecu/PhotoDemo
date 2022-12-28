package com.example.ilovepc.api.vo;

import lombok.Data;

@Data
public class PhotoResult  extends UploadResult{
    //이미지 업로드 수
    private int uploadTotCnt = 0;
    private int uploadErrorCnt = 0;

    /********************************************************************************************** 
     * @Method 설명 : 에러 카운트 추가 (현재 에러카운트 반환)
     * @작성일 : 2022-12-28 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    public int addErrorCnt(){
        this.uploadErrorCnt++;
        return uploadErrorCnt;
    }
}
