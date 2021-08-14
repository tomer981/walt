package com.walt.dao;

import com.walt.model.*;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.util.*;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {
    @Query("SELECT d.driver.id FROM Delivery d WHERE d.driver.city = :city AND d.deliveryTime = :deliveryTime")
    Set<Long> findDriversByCityAndTime(
            @Param("city") City city,
            @Param("deliveryTime") Date deliveryTime);

    @Query("SELECT driver.id AS id, COUNT(deliveryTime) AS deliveriesCount FROM Delivery WHERE driver.city = :city GROUP BY driver.id ORDER BY deliveriesCount ASC")
    List<DriverIdToNumberOfDeliveries> getDriverDeliveries(@Param("city") City city);

    @Query("SELECT d.driver AS driver, SUM(d.distance) AS distance FROM Delivery d WHERE d.driver.city = :city GROUP BY d.driver ORDER BY distance DESC")
    List<DriverDistance> getRankDriverDistanceByCity(@Param("city") City city);

    @Query("SELECT d.driver AS driver, SUM(d.distance) AS distance FROM Delivery d GROUP BY d.driver ORDER BY distance DESC")
    List<DriverDistance> getRankDriverDistance();

    @Query("select d from Delivery d where d.restaurant.id = :restaurantId and d.customer.id = :customerId and d.deliveryTime = :deliveryTime")
    Optional<Delivery> findDeliveryByCustomerAndRestaurantAndDeliveryTime(@Param("restaurantId") Long restaurantId,@Param("customerId") Long customerId,@Param("deliveryTime") Date deliveryTime);




}
