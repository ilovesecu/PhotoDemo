package com.example.ilovepc.common.utils;


public class CustomRandomUtils {
    /********************************************************************************************** 
     * @Method 설명 : 5자리 랜덤 숫자 반환
     * @작성일 : 2022-12-28 
     * @작성자 : 정승주
     * @변경이력 : 
     *********************************************************************************************/
    public static int rand5Integer(){
        //(int) Math.random() * (최댓값-최소값+1) + 최소값 이용~
        return (int) (Math.random() * (99999 - 10000 + 1)  + 10000);
    }

    /**********************************************************************************************
     * @Method 설명 : php microtime 방식 가져오기 변환 ,랜덤값 5자리 숫자 반환
     * @작성일 : 2022-12-28
     * @작성자 : 정승주
     * @변경이력 : IndexOutOfBoundsException 발생시 3번 재시도후 기본값(00000) 반환
     **********************************************************************************************/
    public static String getMillisTime(){
        String result="00000";
        int tryCnt = 0;
        while(true){
            if(tryCnt >= 3) break;
            long mTime = System.currentTimeMillis();
            long seconds = mTime / 1000;
            double decimal = (mTime - (seconds * 1000)) / 1000d;
            String s = String.valueOf(decimal * Math.random());
            try{
                result = s.split("\\.")[1].substring(0, 5);
                break;
            }catch(IndexOutOfBoundsException e){} // Retry
            tryCnt++; //시도횟수 증가
        }
        return result;
    }
}
