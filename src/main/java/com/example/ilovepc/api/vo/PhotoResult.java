package com.example.ilovepc.api.vo;

import lombok.Data;

@Data
public class PhotoResult {
    /*
     * code return 정의
     *  1 : 업로드 성공
     *  0 : 접근 오류 ( 필수 파라메터 누락)
     * -1 : 회원정보 오류( DB 매칭 실패 )
     * -2 : 파일 포멧 오류
     * -3 : 시스템 오류
     * */
    private int code = 100101;
    private String msg ="";

    //이미지 업로드 수
    private int uploadTotCnt = 0;
}
