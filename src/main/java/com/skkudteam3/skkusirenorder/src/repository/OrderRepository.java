package com.skkudteam3.skkusirenorder.src.repository;

import com.skkudteam3.skkusirenorder.common.exceptions.OrderEmptyByStatusException;
import com.skkudteam3.skkusirenorder.common.exceptions.OrderEmptyException;
import com.skkudteam3.skkusirenorder.src.entity.Order;
import com.skkudteam3.skkusirenorder.src.entity.OrderStatus;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
@RequiredArgsConstructor
public class OrderRepository {
    private final EntityManager em;

    public Optional<Order> findById(Long id){
        return Optional.ofNullable(em.find(Order.class, id));
    }

    public void save(Order order){
        em.persist(order); // cascade_all
    }

    public List<Order> findAllOrderByOrderStatus(Long cafeteriaId, OrderStatus orderStatus){
        return Optional.ofNullable(em.createQuery("select o " +
                        "from Order o " +
                        "join fetch o.cafeteria c " +
                        "where o.orderStatus=:orderStatus and c.id=:cafeteriaId", Order.class)
                .setParameter("orderStatus", orderStatus) // 바인딩
                .setParameter("cafeteriaId", cafeteriaId) // 바인딩
                .getResultList()).orElseThrow(OrderEmptyByStatusException::new);
    }

    public Long findTotalOrderCounts(Long cafeteriaId){

        return Optional.ofNullable((Long) em.createQuery("select count(o) from Order o " +
                "join fetch o.cafeteria c " +
                "where c.id=:cafeteriaId").getSingleResult()).orElseThrow(OrderEmptyException::new);
    }

    public Long findTakeOutCounts(Long cafeteriaId){
        return Optional.ofNullable((Long) em.createQuery("select count(o) from Order o " +
                "join fetch o.cafeteria c " +
                "where c.id=:cafeteriaId and o.isTakeOut=true").getSingleResult()).orElseThrow(OrderEmptyException::new);
    }

    public Long findDineInCounts(Long cafeteriaId){
        return Optional.ofNullable((Long) em.createQuery("select count(o) from Order o " +
                "join fetch o.cafeteria c " +
                "where c.id=:cafeteriaId and o.isTakeOut=false").getSingleResult()).orElseThrow(OrderEmptyException::new);
    }
}
