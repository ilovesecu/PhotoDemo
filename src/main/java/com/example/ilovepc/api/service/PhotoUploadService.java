package com.example.ilovepc.api.service;

import com.example.ilovepc.api.vo.PhotoDetail;
import com.example.ilovepc.api.vo.PhotoResult;
import com.example.ilovepc.api.vo.PhotoUploadVO;
import com.example.ilovepc.common.utils.CustomRandomUtils;
import com.example.ilovepc.common.utils.FileUtil;
import com.example.ilovepc.common.utils.MemberUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@AllArgsConstructor
public class PhotoUploadService {
    @Value("${spring.profiles.active}") private String serverType;
    private final MemberUtil memberUtil;
    private final FileUtil fileUtil;

    @Autowired
    public PhotoUploadService(MemberUtil memberUtil, FileUtil fileUtil){
        this.memberUtil = memberUtil;
        this.fileUtil = fileUtil;
    }

    /********************************************************************************************** 
     * @Method 설명 : 이미지 파일 업로드 (비회원)
     * @작성일 : 2022-12-22
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    public PhotoResult setUploadImageMmbr(String type, PhotoUploadVO photoUploadVO){
        return this.setUploadImageFiles(type,photoUploadVO, false);
    }
    
    /********************************************************************************************** 
     * @Method 설명 : 이미지 파일 업로드
     * @작성일 : 2022-12-22 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    private PhotoResult setUploadImageFiles(String type, PhotoUploadVO photoUploadVO, boolean checkMem){
        PhotoResult photoResult = new PhotoResult();
        int memNo = memberUtil.getMemNo(photoUploadVO, checkMem);

        //업로드 요청된 파일들
        MultipartFile[] files = photoUploadVO.getFiles();
        if(files == null || files.length == 0 || memNo <= 0 || type == null){
            photoResult.setCode(100100);
            photoResult.setMsg("올바른 접근이 아닙니다. [File or MemNo or Type Error]");
            return photoResult;
        }
        //TODO : 회원인경우 , DB에서 회원번호 있는지 검사
        Map<String, Map<String,Object>> detailMap = new HashMap<>();
        ImageInputStream imageInputStream = null;
        InputStream inputStream = null;

        try{
            photoResult.setUploadTotCnt(files.length);
            String absolutPath = fileUtil.getAbsolutePath(serverType);
            String datePath = fileUtil.getUploadFolderWithDate(type, photoUploadVO.getTemp());
            log.error("ab={}, up={}",absolutPath,datePath);

            File uploadFolder = new File(absolutPath+File.separator+datePath);
            if(uploadFolder.exists() == false){ // 업로드할 폴더 생성
                uploadFolder.mkdirs();
            }
            
            for(int i=0; i<files.length; i++){
                MultipartFile mUploadReqFile = files[i];
                PhotoDetail infoResult = new PhotoDetail();
                String fileName = mUploadReqFile.getOriginalFilename();

                //요청한 확장자
                String extFileNameParam = photoUploadVO.getFileExt();
                if(extFileNameParam != null || extFileNameParam.equals("") == false){
                    fileName += "." + extFileNameParam;
                }
                /* ==> 해당 코드는 파일명에서 확장자를 가져와서 검사하는것이므로 취약함.
                String[] splitFileName = fileName.split("\\."); //확장자 추출을 위한 '.'split
                String extFileName = "jpg"; //기본확장자는 jpg
                if (splitFileName.length > 1)
                    extFileName = splitFileName[splitFileName.length - 1].trim().toLowerCase();
                 */
                //파일 확장자 검사
                File uploadReqFile = new File(uploadFolder+File.separator+mUploadReqFile.getOriginalFilename());
                boolean isPermissionType = fileUtil.isPermissionFileType(uploadReqFile);
                if(isPermissionType == false){
                    //파일 확장자가 문제일경우.
                    infoResult.setCode(100098);
                    infoResult.setMsg("지원하지 않은 파일입니다. : " + fileName);
                    photoResult.addErrorCnt();
                }else{
                    String resultDecimal = CustomRandomUtils.getMillisTime();
                    Image image = null;

                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return photoResult;
    }
}
