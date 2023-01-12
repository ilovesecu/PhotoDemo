package com.example.ilovepc.common.utils;

import com.example.ilovepc.api.vo.ExtensionResult;
import com.example.ilovepc.common.Const;
import com.example.ilovepc.common.FileType;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Component
public class FileUtil {
    @Value("${inforex.root.directory}")
    String UPLOAD_ROOT_DIR;

    /**********************************************************************************************
     * @Method 설명 : 루트 경로 반환
     * @작성일 : 2022-12-23
     * @작성자 : 정승주
     * @변경이력 :
     **********************************************************************************************/
    public String getAbsolutePath(String serverType){
        if(serverType.equals("local")){
            String absolutePath = new File("./").getAbsolutePath();
            return absolutePath.substring(0, absolutePath.length() - 2);
        }else{
            return UPLOAD_ROOT_DIR;
        }
    }

    /**********************************************************************************************
     * @Method 설명 : 현재날짜 맵 반환
     * @작성일 : 2022-12-29
     * @작성자 : 정승주
     * @변경이력 :
     **********************************************************************************************/
    public Map<String,Object> getDateMap(){
        Map<String,Object> dateMap = new HashMap<>();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatted = sdf.format(date);
        String[] splited = formatted.split("-");
        String yyyy = splited[0];
        String MM = splited[1];
        String dd = splited[2];
        String HH = splited[3];
        String mm = splited[4];
        String ss = splited[5];

        dateMap.put("yyyy",yyyy);
        dateMap.put("MM",MM);
        dateMap.put("dd",dd);
        dateMap.put("HH",HH);
        dateMap.put("mm",mm);
        dateMap.put("ss",ss);

        int HHIntParse = Integer.parseInt(HH);
        if(HHIntParse < 10) dateMap.put("hh", "0");
        else if(HHIntParse >= 10 && HHIntParse <= 19) dateMap.put("hh","1");
        else dateMap.put("hh",2);

        return dateMap;
    }
    
    /********************************************************************************************** 
     * @Method 설명 : 현재 날짜를 기반으로 업로드 폴더 반환
     * @작성일 : 2022-12-23 
     * @작성자 : 정승주
     * @Params : temp = 임시저장 여부
     * @변경이력 : 
     **********************************************************************************************/
    public String getUploadFolderWithDate(String type, String temp, Map<String,Object> dateMap){
        String yyyy = (String)dateMap.get("yyyy");
        String MM = (String)dateMap.get("MM");
        String dd = (String)dateMap.get("dd");

        //임시저장 여부
        String tempDir = "";
        if( "1".equals(temp) ){
            tempDir = File.separator + FileType.get("temp");
        }
        String folderPath = tempDir
                + File.separator + FileType.get(type)
                + File.separator + yyyy
                + File.separator + MM
                + File.separator + dd
                ;
        return folderPath;
    }

    /**********************************************************************************************
     * @Method 설명 : 파일명 생성 (회원번호_날짜+밀리초랜덤_타입.확장자)
     * @작성일 : 2022-12-29
     * @작성자 : 정승주
     * @변경이력 :
     **********************************************************************************************/
    public String getNewFileName(int memNo, Map<String,Object>dateMap, String extType, String randInt, String type){
        return memNo + "_"
                +dateMap.get("yyyy")
                +dateMap.get("MM")
                +dateMap.get("dd")
                +dateMap.get("HH")
                +dateMap.get("mm")
                +dateMap.get("ss")
                +randInt + (type.equals("") ? "" : "_" + type) + "." + extType;
    }


    /********************************************************************************************** 
     * @Method 설명 : 파일 확장자 검사 (true : 허용 / false : 비허용)
     * @작성일 : 2022-12-28 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    public ExtensionResult isPermissionFileType(InputStream inputStream){
        ExtensionResult result = new ExtensionResult();
        try{
            String mimeType = new Tika().detect(inputStream);
            String[] mimeTypeSplited = mimeType.split("/");
            result.setFullType(mimeType);
            result.setFileType(mimeTypeSplited[0]);
            result.setExtType(mimeTypeSplited[1]);

            if(mimeTypeSplited[0].equals("image") == false){
                result.setResult(false);
            } else if(Const.IMAGE_FILE_CONTAINS.contains(mimeTypeSplited[1].toLowerCase(Locale.ROOT))){
                result.setResult(true);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
