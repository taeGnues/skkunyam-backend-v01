package com.skkudteam3.skkusirenorder.src.repository;

import com.skkudteam3.skkusirenorder.common.exceptions.CafeteriaNotFoundException;
import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import com.skkudteam3.skkusirenorder.src.entity.CafeteriaImage;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class CafeteriaImageRepository {
    private final EntityManager em;

    public void save(CafeteriaImage cafeteriaImage) {
        em.persist(cafeteriaImage);
    }


    public List<CafeteriaImage> findImagesByCafeteriaId(Long cafeteriaId) {
        return Optional.ofNullable(em.createQuery("select ci from CafeteriaImage ci " +
                        " where ci.cafeteria.id = :cafeteriaId", CafeteriaImage.class)
                .setParameter("cafeteriaId", cafeteriaId)
                .getResultList()).orElseThrow(CafeteriaNotFoundException::new);
    }
}
