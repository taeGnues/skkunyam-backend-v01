package com.skkudteam3.skkusirenorder.src.service;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.skkudteam3.skkusirenorder.common.ImageStore;
import com.skkudteam3.skkusirenorder.common.exceptions.CafeteriaNotFoundException;
import com.skkudteam3.skkusirenorder.common.exceptions.MenuEmptyException;
import com.skkudteam3.skkusirenorder.common.exceptions.MenuNotFoundException;
import com.skkudteam3.skkusirenorder.common.exceptions.TodayMenuNotFoundException;
import com.skkudteam3.skkusirenorder.src.dto.MenuDetailGetResDTO;
import com.skkudteam3.skkusirenorder.src.dto.MenuGetResDTO;
import com.skkudteam3.skkusirenorder.src.dto.MenuPatchReqDTO;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.entity.Menu;
import com.skkudteam3.skkusirenorder.src.dto.MenuPostReqDTO;
import com.skkudteam3.skkusirenorder.src.entity.MenuImage;
import com.skkudteam3.skkusirenorder.src.repository.CafeteriaRepository;
import com.skkudteam3.skkusirenorder.src.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final CafeteriaRepository cafeteriaRepository;
    private final ImageStore imageStore;
    @Value("${file.dir.menu}")
    private String MenuImageDir;
    private final AmazonS3Client amazonS3Client;

    @Value("${cloud.aws.s3.bucket}") // TODO: 이미지 수정해주기
    private String bucket;
    @Value("${cloud.aws.region.static}")
    private String region;

    /*
    메뉴 추가 (menu 변환 -> menu 저장 -> 해당 메뉴 cafeteria에 추가)
     */

    @Transactional
    public Long saveMenu(MenuPostReqDTO menuPostReqDTO, MultipartFile menuImage) throws IOException {

        String fileName = menuImage.getOriginalFilename();
        String fileUrl = "https://" + bucket + ".s3." + region + ".amazonaws.com/" + fileName;
        ObjectMetadata metadata= new ObjectMetadata();
        metadata.setContentType(menuImage.getContentType());
        metadata.setContentLength(menuImage.getSize());
        amazonS3Client.putObject(bucket, fileName, menuImage.getInputStream(), metadata);
        log.info("fileUrl={}",fileUrl);

        Cafeteria cafeteria = cafeteriaRepository.findById(menuPostReqDTO.getCafeteriaId()).orElseThrow(CafeteriaNotFoundException::new);
        Menu menu = menuPostReqDTO.toEntity();
        menu.setCafeteria(cafeteria);
        menu.setMenuImage(storeMenuImage(menuImage));
        Long menuId = menuRepository.save(menu);

        cafeteria.addMenu(menu);

        return menuId;
    }
    // 이미지 등록용
    public String getFullPath(String filename){
        return MenuImageDir + filename;
    }

    public MenuImage storeMenuImage(MultipartFile multipartFile) throws IOException {

        MenuImage menuImage = storeImage(multipartFile);
        return menuImage;
    }

    public MenuImage storeImage(MultipartFile multipartFile) throws IOException {
        if(multipartFile.isEmpty()){
            return null;
        }

        String originalFilename = multipartFile.getOriginalFilename(); // 원래 이름
        String storeFilename = imageStore.createStoreFileName(originalFilename); // 저장된 이름
        multipartFile.transferTo(new File(getFullPath(storeFilename))); // 디렉토리에 파일 넘어가서 만들어짐.


        return new MenuImage(originalFilename, storeFilename);
    }
    /*
    메뉴 이미지 조회
     */
    public String getMenuImageFullPath(Long menuId){
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        return getFullPath(menu.getMenuImage().getMenuImagePath());
    }


    /*
    메뉴 수정
     */
    @Transactional
    public void updateMenu(MenuPatchReqDTO menuPatchReqDTO){
        Cafeteria cafeteria = cafeteriaRepository.findById(menuPatchReqDTO.getCafeteriaId()).orElseThrow(CafeteriaNotFoundException::new);
        Menu menu = menuRepository.findById(menuPatchReqDTO.getMenuId()).orElseThrow(MenuNotFoundException::new);

        menu = menuPatchReqDTO.toEntity();
    }

    /*
    메뉴 삭제
     */
    @Transactional
    public void deleteMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        menuRepository.delete(menu);
    }

    /*
    해당 식당 모든 메뉴 조회
     */
    public List<MenuGetResDTO> findMenus(Long cafeteriaId) {
        return menuRepository.findMenus(cafeteriaId).orElseThrow(MenuEmptyException::new).stream().map(Menu::toMenuGetResDTO).toList();
    }

    /*
    해당 메뉴 조회
     */
    public MenuDetailGetResDTO findMenuDetail(Long menuId) {
        return menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new).toMenuDetailGetResDTO();
    }

    /*
    오늘의 메뉴 조회
    - 열려있는 가게들의 모든 메뉴 조회
    - 그 메뉴들 중 무작위로 메뉴 1개 추천.
     */
    public MenuGetResDTO findTodayMenu() {
        List<Menu> menus = menuRepository.findAllMenusWithOpenCafeteria().orElseThrow(TodayMenuNotFoundException::new);

        Random random = new Random();
        return menus.get(random.nextInt(menus.size())).toMenuGetResDTO();
    }

    /*
    해당 메뉴 soldout 상태로 변경
     */
    @Transactional
    public void soldOutMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        menu.soldOut();
    }

    /*
    해당 메뉴 soldout 상태로 변경
     */
    @Transactional
    public void onSaleMenu(Long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(MenuNotFoundException::new);
        menu.onSale();
    }
}
