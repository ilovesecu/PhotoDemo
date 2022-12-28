package com.example.ilovepc.common.utils;

import com.example.ilovepc.common.Const;
import com.example.ilovepc.common.FileType;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
     * @Method 설명 : 현재 날짜를 기반으로 업로드 폴더 반환
     * @작성일 : 2022-12-23 
     * @작성자 : 정승주
     * @Params : temp = 임시저장 여부
     * @변경이력 : 
     **********************************************************************************************/
    public String getUploadFolderWithDate(String type, String temp){
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String formatted = sdf.format(date);
        String[] splited = formatted.split("-");
        String yyyy = splited[0];
        String MM = splited[1];
        String dd = splited[2];

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
     * @Method 설명 : 파일 확장자 검사 (true : 허용 / false : 비허용)
     * @작성일 : 2022-12-28 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    public boolean isPermissionFileType(InputStream inputStream){
        boolean result = false;
        try{
            String mimeType = new Tika().detect(inputStream);
            String[] mimeTypeSplited = mimeType.split("/");
            if(mimeTypeSplited[0].equals("image") == false){
                return false;
            }
            if(Const.IMAGE_FILE_CONTAINS.contains(mimeTypeSplited[1].toLowerCase(Locale.ROOT))){
                result = true;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return result;
    }
}
