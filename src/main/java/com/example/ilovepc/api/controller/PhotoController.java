package com.example.ilovepc.api.controller;

import com.example.ilovepc.api.service.PhotoUploadService;
import com.example.ilovepc.api.vo.PhotoResult;
import com.example.ilovepc.api.vo.PhotoUploadVO;
import com.example.ilovepc.common.FileType;
import com.example.ilovepc.common.ServiceType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class PhotoController {

    @Value("${spring.profiles.active}") private String serverType;
    final private PhotoUploadService photoUploadService;

    @Autowired
    public PhotoController(PhotoUploadService photoUploadService) {
        this.photoUploadService = photoUploadService;
    }

    @RequestMapping(value = "/{type}/{subType}/upload")
    public Object upload(@PathVariable(name="type")String type,
                         @PathVariable(name = "subType")String subType, PhotoUploadVO photoUploadVO, BindingResult bindingResult){

        ServiceType serviceType = FileType.getServiceType(type); //YEOBOYA
        if(subType.equals("image")){
            if(bindingResult.hasErrors()){
                PhotoResult photoResult = new PhotoResult();
                photoResult.setCode(100100);
                photoResult.setMsg("올바른 접근이 아닙니다. (필수파라미터 누락)");
                return photoResult;
            }
        }

        photoUploadService.setUploadImageMmbr(type, photoUploadVO);

        return null;
    }
}
