package com.example.ilovepc.common.utils;

import com.example.ilovepc.api.vo.PhotoUploadVO;
import org.springframework.stereotype.Component;

@Component
public class MemberUtil {
    
    /********************************************************************************************** 
     * @Method 설명 : 비회원 업로드일경우 랜덤값 5자리 / 회원의 경우 회원번호 반환
     * @작성일 : 2022-12-23 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    public int getMemNo(PhotoUploadVO vo, boolean checkMem){
        if(checkMem){
            return vo.getMemNo();
        }else{
            //(int) Math.random() * (최댓값-최소값+1) + 최소값 이용~
            return (int) Math.random() * (99999 - 10000 + 1)  + 10000;
        }
    }
    
}
