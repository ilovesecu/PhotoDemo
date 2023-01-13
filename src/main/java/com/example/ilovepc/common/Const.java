package com.example.ilovepc.common;
import java.util.HashMap;
import java.util.Map;

public class Const {
    public static final String IMAGE_FILE_CONTAINS = "gif|jpg|jpeg|png";
    public static final String CHAT_TYPE = "msg|nbMsg|tradeChat";

    private static final Map<String,Integer> kingBoSizeMap = new HashMap<>();
    private static final Map<String,Integer> porBoSizeMap = new HashMap<>();

    static {
        kingBoSizeMap.put("b", 320);
        kingBoSizeMap.put("m", 160);

        porBoSizeMap.put("w", 720);
        porBoSizeMap.put("b", 320);
        porBoSizeMap.put("m", 160);
    }

    public static Map<String,Integer> getSaveImageSizeInfo(ServiceType serviceType){
        switch (serviceType){
            case YEOBOYA:
                return kingBoSizeMap;
            case NOBLESSE:
                return porBoSizeMap;
            default:
                return kingBoSizeMap;
        }
    }
}
