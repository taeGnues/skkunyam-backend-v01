package com.skkudteam3.skkusirenorder.src.service;

import com.skkudteam3.skkusirenorder.common.ImageStore;
import com.skkudteam3.skkusirenorder.common.exceptions.CafeteriaNotFoundException;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.entity.CafeteriaImage;
import com.skkudteam3.skkusirenorder.src.repository.CafeteriaImageRepository;
import com.skkudteam3.skkusirenorder.src.repository.CafeteriaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CafeteriaImageService {

    private final CafeteriaRepository cafeteriaRepository;
    private final CafeteriaImageRepository cafeteriaImageRepository;
    private final ImageStore imageStore;

    @Value("${file.dir.cafeteria}")
    private String CafeteriaImageDir;

    public String getFullPath(String filename){
        return CafeteriaImageDir + filename;
    }

    @Transactional
    public void storeCafeteriaImages(List<MultipartFile> multipartFiles, Long cafeteriaId) throws IOException {

        Cafeteria cafeteria = cafeteriaRepository.findById(cafeteriaId).orElseThrow(CafeteriaNotFoundException::new);
        log.info("CafeteriaID={}", cafeteria.getId());
        for(MultipartFile multipartFile : multipartFiles){
            if(!multipartFile.isEmpty()){
                CafeteriaImage uploadImage = storeImage(multipartFile);
                cafeteriaImageRepository.save(uploadImage);
                cafeteria.addCafeteriaImage(uploadImage);
            }
        }
    }


    public CafeteriaImage storeImage(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 이름
        String storeFilename = imageStore.createStoreFileName(originalFilename); // 저장된 이름
        multipartFile.transferTo(new File(getFullPath(storeFilename))); // 디렉토리에 파일 넘어가서 만들어짐.


        return new CafeteriaImage(originalFilename, storeFilename);
    }

    public List<String> findImagesByCafeteriaId(Long cafeteriaId) {
        List<CafeteriaImage> cis = cafeteriaImageRepository.findImagesByCafeteriaId(cafeteriaId);

        return cis.stream().map(CafeteriaImage::getCafeteriaImagePath).collect(Collectors.toList());
    }
}
