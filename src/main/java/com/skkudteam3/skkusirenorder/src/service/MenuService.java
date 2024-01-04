package com.skkudteam3.skkusirenorder.src.service;

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
import com.skkudteam3.skkusirenorder.src.repository.CafeteriaRepository;
import com.skkudteam3.skkusirenorder.src.repository.MenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MenuService {

    private final MenuRepository menuRepository;
    private final CafeteriaRepository cafeteriaRepository;

    /*
    메뉴 추가 (menu 변환 -> menu 저장 -> 해당 메뉴 cafeteria에 추가)
     */
    @Transactional
    public Long saveMenu(MenuPostReqDTO menuPostReqDTO){
        Cafeteria cafeteria = cafeteriaRepository.findById(menuPostReqDTO.getCafeteriaId()).orElseThrow(CafeteriaNotFoundException::new);
        Menu menu = menuPostReqDTO.toEntity();
        menu.setCafeteria(cafeteria);
        Long menuId = menuRepository.save(menu);

        cafeteria.addMenu(menu);

        return menuId;
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
