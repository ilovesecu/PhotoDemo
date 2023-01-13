package com.example.ilovepc.common;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class FileType {
    //타입별 디렉토리
    private static final Map<String,Object> FILE_TYPE = new HashMap<>();

    static {
        //////////////////////////////////////
        // 공통 타입
        //////////////////////////////////////
        FILE_TYPE.put("temp"      , "yeo_tmp" );     // 임시저장용

        //////////////////////////////////////
        // 킹보 타입들
        //////////////////////////////////////
        FILE_TYPE.put("photo"      , "yeo_photo"       + File.separator);  // 여보야
        FILE_TYPE.put("blur"       , "yeo_photo_blur"  + File.separator);  // 여보야
        FILE_TYPE.put("msg"       , "yeo_mesg"  + File.separator);  // 여보야 메시지

        //////////////////////////////////////
        // 블레스 타입들
        //////////////////////////////////////
        FILE_TYPE.put("nbMsg"        , "yeo_nb_mesg"        + File.separator);  // 노블 메세지
    }

    /**
     * type에 해당하는 폴더명을 반환 한다.
     * 마지막은 항상 File.separator 가 붙는다.
     * @param type
     * @return
     */
    public static Object get(String type){
        return FILE_TYPE.get(type);
    }
    public static Map<String, Object> getMap(){
        return FILE_TYPE;
    }

    public static ServiceType getServiceType(String key){
        if(key == null)return ServiceType.YEOBOYA;

        switch(key){
            case "preview":
                return ServiceType.YEOBOYA;
            case "nbMsg":
                return ServiceType.NOBLESSE;
            default:
                return ServiceType.YEOBOYA;
        }
    }
}
