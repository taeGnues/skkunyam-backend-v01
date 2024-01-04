package com.skkudteam3.skkusirenorder.src.service;

import com.skkudteam3.skkusirenorder.common.exceptions.CafeteriaEmptyException;
import com.skkudteam3.skkusirenorder.common.exceptions.CafeteriaNotFoundException;
import com.skkudteam3.skkusirenorder.src.dto.CafeteriaGetResDTO;
import com.skkudteam3.skkusirenorder.src.dto.CafeteriaPatchReqDTO;
import com.skkudteam3.skkusirenorder.src.dto.CafeteriaPostReqDTO;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.repository.CafeteriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CafeteriaService {

    private final CafeteriaRepository cafeteriaRepository;

    public Cafeteria findCafeteria(Long id){
        return cafeteriaRepository.findById(id).orElseThrow(CafeteriaNotFoundException::new);
    }


    public Cafeteria findDetailCafeteria(Long id) {
        return cafeteriaRepository.findDetailById(id).orElseThrow(CafeteriaNotFoundException::new);
    }

    public List<CafeteriaGetResDTO> findCafeterias(int pageNo, int pageSize){
        return cafeteriaRepository.findCafeterias(pageNo, pageSize).orElseThrow(CafeteriaEmptyException::new).stream().map(Cafeteria::toCafeteriaGetResDTO).toList();
    }

    @Transactional
    public Long saveCafeteria(CafeteriaPostReqDTO cafeteriaPostReqDTO){

        return cafeteriaRepository.save(cafeteriaPostReqDTO.toEntity());
    }

    @Transactional
    public Boolean openCafeteria(Long id){

        Cafeteria cafeteria = findCafeteria(id);
        cafeteria.open();

        return cafeteria.getIsCafeteriaOpen();
    }

    @Transactional
    public Boolean closeCafeteria(Long id){

        Cafeteria cafeteria = findCafeteria(id);
        cafeteria.close();

        return cafeteria.getIsCafeteriaOpen();
    }

    @Transactional
    public void updateCafeteria(CafeteriaPatchReqDTO cafeteriaPatchReqDTO){
        Cafeteria cafeteria = findCafeteria(cafeteriaPatchReqDTO.getCafeteriaId());
        cafeteria.updateInfo(cafeteriaPatchReqDTO);
    }

}
