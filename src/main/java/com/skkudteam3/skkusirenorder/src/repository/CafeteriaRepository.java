package com.skkudteam3.skkusirenorder.src.repository;

import com.skkudteam3.skkusirenorder.src.entity.Cafeteria;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class CafeteriaRepository {
    private final EntityManager em;

    public Optional<Cafeteria> findById(Long id){
        return Optional.ofNullable(em.find(Cafeteria.class, id));
    }


    public Long save(Cafeteria cafeteria) {
        em.persist(cafeteria);
        return cafeteria.getId();
    }

    public Optional<Cafeteria> findDetailById(Long cafeteriaId) {
        return Optional.ofNullable(em.createQuery("select c from Cafeteria c" +
                        " join fetch c.cafeteriaImages" +
                        " where c.id = :cafeteriaId", Cafeteria.class)
                .setParameter("cafeteriaId", cafeteriaId)
                .getSingleResult());
    }

    public Optional<List<Cafeteria>> findCafeterias(int pageNo, int pageSize){
        return Optional.ofNullable(em.createQuery("select c from Cafeteria c", Cafeteria.class)
                .setFirstResult(pageNo)
                .setMaxResults(pageSize)
                .getResultList());
    }


}
