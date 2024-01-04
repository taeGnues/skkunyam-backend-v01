package com.skkudteam3.skkusirenorder.src.repository;

import com.skkudteam3.skkusirenorder.common.exceptions.MenuEmptyException;
import com.skkudteam3.skkusirenorder.src.entity.Menu;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class MenuRepository {
    private final EntityManager em;

    public Optional<Menu> findById(Long id){
        return Optional.ofNullable(em.find(Menu.class, id));
    }
    public Long save(Menu menu){
        em.persist(menu);
        return menu.getId();
    }

    public void delete(Menu menu){
        em.remove(menu);
    }

    public Optional<List<Menu>> findMenus(Long cafeteriaId){
        return Optional.ofNullable(em.createQuery("select m from Menu m" +
                        " join fetch m.cafeteria c" +
                        " where c.id = :cafeteriaId", Menu.class)
                .setParameter("cafeteriaId", cafeteriaId)
                .getResultList());
    }

    public Optional<List<Menu>> findAllMenusWithOpenCafeteria() {
        return Optional.ofNullable(em.createQuery("select m from Menu m" +
                        " join fetch m.cafeteria c" +
                        " where c.isCafeteriaOpen = true", Menu.class)
                .getResultList());
    }

    public List<Menu> findTopFiveMenus(Long cafeteriaId){
        return Optional.ofNullable(em.createQuery("select m from Menu m join fetch m.cafeteria c" +
                        " where c.id = :cafeteriaId order by m.sales desc", Menu.class)
                .setParameter("cafeteriaId", cafeteriaId)
                .setMaxResults(5)
                .getResultList()).orElseThrow(MenuEmptyException::new);

    }
}
