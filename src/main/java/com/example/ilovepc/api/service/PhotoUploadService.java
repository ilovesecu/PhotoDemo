package com.example.ilovepc.api.service;

import com.example.ilovepc.api.vo.PhotoResult;
import com.example.ilovepc.api.vo.PhotoUploadVO;
import com.example.ilovepc.common.utils.FileUtil;
import com.example.ilovepc.common.utils.MemberUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.stream.ImageInputStream;
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
            String uploadPath = fileUtil.getUploadFolderWithDate(type, photoUploadVO.getTemp());
            log.error("ab={}, up={}",absolutPath,uploadPath);
        }catch(Exception e){

        }

        return photoResult;
    }
}
