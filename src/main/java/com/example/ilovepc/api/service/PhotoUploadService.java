package com.example.ilovepc.api.service;

import com.example.ilovepc.api.vo.ExtensionResult;
import com.example.ilovepc.api.vo.PhotoDetail;
import com.example.ilovepc.api.vo.PhotoResult;
import com.example.ilovepc.api.vo.PhotoUploadVO;
import com.example.ilovepc.common.Const;
import com.example.ilovepc.common.FileType;
import com.example.ilovepc.common.external.AnimatedGifEncoder;
import com.example.ilovepc.common.external.GifDecoder;
import com.example.ilovepc.common.utils.CustomRandomUtils;
import com.example.ilovepc.common.utils.FileUtil;
import com.example.ilovepc.common.utils.MemberUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

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
        Map<String,Object> dateMap = fileUtil.getDateMap();
        ImageInputStream imageInputStream = null;
        InputStream inputStream = null;

        try{
            photoResult.setUploadTotCnt(files.length);
            String absolutPath = fileUtil.getAbsolutePath(serverType);
            String datePath = fileUtil.getUploadFolderWithDate(type, photoUploadVO.getTemp(), dateMap);

            File uploadFolder = new File(absolutPath+File.separator+datePath);
            if(uploadFolder.exists() == false){ // 업로드할 폴더 생성
                uploadFolder.mkdirs();
            }
            
            for(int i=0; i<files.length; i++){
                MultipartFile mUploadReqFile = files[i];
                PhotoDetail infoResult = new PhotoDetail();
                String fileName = mUploadReqFile.getOriginalFilename(); //사용자가 업로드한 원본이름

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
                ExtensionResult extensionResult = fileUtil.isPermissionFileType(mUploadReqFile.getInputStream());
                if(extensionResult.isResult() == false){
                    //파일 확장자가 문제일경우.
                    infoResult.setCode(100098);
                    infoResult.setMsg("지원하지 않은 파일입니다. : " + fileName);
                    photoResult.addErrorCnt();
                }else{
                    //업로드 실행
                    String resultDecimal = CustomRandomUtils.getMillisTime(); //현재 밀리초를 기반으로 랜덤 5숫자
                    Image image = null;
                    inputStream = mUploadReqFile.getInputStream();
                    imageInputStream = ImageIO.createImageInputStream(inputStream);
                    Optional<Image> imageOpt = Optional.ofNullable(ImageIO.read(imageInputStream));
                    image = imageOpt.orElse(null); //킹보는 gif랑 따로 처리하는데 PatchedGIFImageReader 객체를 못찾겠어서 같이 처리하자.

                    if(image == null){ //imageStream으로 못 읽었을 때
                        infoResult.setCode(100096);
                        infoResult.setMsg("이미지 파일이 아닙니다. [image==null]");
                        photoResult.addErrorCnt();
                    }else{
                        //서버 저장 file name = jpg 고정
                        // ▼ 해당 이름은 서버저장이름
                        String originalFileName = fileUtil.getNewFileName(memNo, dateMap, extensionResult.getExtType(), resultDecimal, "");
                        String originalFileName_o = fileUtil.getNewFileName(memNo, dateMap, extensionResult.getExtType(), resultDecimal, "o");

                        // GIF 원본 저장
                        if(extensionResult.getExtType().equals("gif")){
                            mUploadReqFile.transferTo(new File(uploadFolder+File.separator+originalFileName_o));
                        }
                        Map<String,Integer> saveSizeMap = Const.getSaveImageSizeInfo(FileType.getServiceType(type));
                        boolean uploadStatus = true;
                        if(extensionResult.getExtType().equals("gif") == false){
                            //GIF가 아닐 때 → 비율 처리 후 업로드 동시 진행
                            uploadStatus = this.imageSizeAndUpload(uploadFolder.getAbsolutePath(), originalFileName, extensionResult.getExtType(), image, 100, 100, originalFileName_o);
                        }

                        List<String> newFileNameList = new ArrayList<>(); //뭐하는건지 알아보자.
                        boolean isChatType = Const.CHAT_TYPE.contains(type);
                        Set<String> keySet = saveSizeMap.keySet();
                        for(String key : keySet){
                            String newFileName = fileUtil.getNewFileName(memNo, dateMap, extensionResult.getExtType(), resultDecimal, key);
                            int newImgSize = isChatType ? saveSizeMap.get(key) / 2 : saveSizeMap.get(key); //채팅방용 이미지 파일
                            uploadStatus = uploadStatus && this.imageSizeAndUpload(uploadFolder.getAbsolutePath(), newFileName, extensionResult.getExtType(), image, newImgSize,newImgSize, originalFileName_o);

                            if(uploadStatus) {
                                newFileNameList.add(newFileName);
                            }else{
                                break;
                            }
                        }

                        if(uploadStatus && extensionResult.getExtType().equals("gif")){
                            // move = 두 경로가 동일하다면 source 파일의 이름을 dest로 변경
                            // GIF의 _o -> 없애버리기 위함인듯.
                            Files.move(Paths.get(uploadFolder+File.separator+originalFileName_o), Paths.get(uploadFolder+File.separator+originalFileName));
                            newFileNameList.add(originalFileName_o);
                        }

                        if(uploadStatus){
                            String fileNameConvention = originalFileName+"|"+image.getWidth(null)+"|"+image.getHeight(null);
                            infoResult.setImageFile(fileNameConvention);
                        }else{
                            log.error("error- / extFileName={} / originalFilename={}",extensionResult.getExtType(), originalFileName);
                        }

                    }
                }
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }

        return photoResult;
    }
    
    /********************************************************************************************** 
     * @Method 설명 : 이미지 비율 처리 및 이미지 업로드 동시 진행
     * @작성일 : 2022-12-29 
     * @작성자 : 정승주
     * @변경이력 : 
     **********************************************************************************************/
    private boolean imageSizeAndUpload(String imgTargetPath, String fileName, String extFileName, Image image, int dstW, int dstH, String orgFilePath){
        Double scale;
        // 원본 이미지 사이즈 가져오기
        Integer imageWidth = image.getWidth(null);
        Integer imageHeight = image.getHeight(null);

        int resizeW;
        int resizeH;
        if (dstW == 0 || dstH == 0) { //목표 W,H 가 0이라면 원본 사이즈 그대로
            resizeW = imageWidth;
            resizeH = imageHeight;
        }else{
            scale = getScale(imageWidth, imageHeight, dstW, dstH); // 목표 사이즈로 줄이기 위한 배율 값 가져오기 (같은 배율로 줄여야함)
            resizeW = ((Double) (imageWidth.doubleValue() * scale)).intValue();
            resizeH = ((Double) (imageHeight.doubleValue() * scale)).intValue();
        }

        if(extFileName.equals("gif")){ //GIF 비율 따로 처리
            this.resizeGif(imgTargetPath+File.separator+orgFilePath, imgTargetPath+File.separator+fileName, resizeW, resizeH, false);
        }else{
            try{
                BufferedImage originBuffer = ((BufferedImage) image);
                BufferedImage bufferedImage = Thumbnails.of(originBuffer)
                        .size(resizeW, resizeH)
                        .outputFormat(extFileName)
                        .asBufferedImage();
                ImageIO.write(bufferedImage, extFileName, new File(imgTargetPath+File.separator+fileName));
            }catch(Exception e){
                e.printStackTrace();
                return false;
            }
        }


        return true;
    }

    /**********************************************************************************************
     * @Method 설명 : 상대적 비율 계산 (같은 비율로 w,h를 줄여야 하니까)
     * @작성일 : 2022-12-29
     * @작성자 : 정승주
     * @변경이력 :
     **********************************************************************************************/
    public Double getScale(int srcW, int srcH, int dstW, int dstH) {
        Double scale = new Double(1); //기본적으로 1배율
        if(srcW > dstW || srcH > dstH){ //목표값 중 하나라도 작을 때
            if(srcW > srcH){ //원본 width가 원본 height보다 클 때
                scale = dstW * 1.0 / srcW;
            }else{ //원본 height가 더 클때
                scale = dstH * 1.0 / srcH;
            }
        }
        return scale;
    }

    /**********************************************************************************************
     * @Method 설명 : GIF 리사이즈
     * @작성일 : 2023-01-12
     * @작성자 : 정승주
     * @변경이력 :
     * @Param :
     *  orgFile : originalFileName_o
     *  newFile : fileName (originalFileName)
     **********************************************************************************************/
    public void resizeGif(final String orgFile,final String newFile, final int width, final int height,final boolean isOrg){
        GifDecoder dec = new GifDecoder();
        dec.read(orgFile); //GIF 파일을 읽는다. (반환되는 값은 read status code)

        AnimatedGifEncoder enc = new AnimatedGifEncoder();
        enc.start(newFile); //생성할 GIF 이미지
        enc.setRepeat(0); // 0의 의미는 무기한 재생

        final int frameCount = isOrg ? dec.getFrameCount() : 1;
        for(int i=0; i<frameCount; i++){
            BufferedImage bi = dec.getFrame(i);
            int delay = dec.getDelay(i);

            BufferedImage destimg = new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR); //RGBA 인듯?
            Graphics2D g = destimg.createGraphics();
            g.drawImage(bi, 0, 0, width, height, null);
            enc.addFrame(destimg);
            enc.setDelay(delay);
        }
        enc.finish(); //Flushes
    }
}
