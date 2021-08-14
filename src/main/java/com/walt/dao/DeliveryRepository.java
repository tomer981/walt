package com.walt.dao;

import com.walt.model.*;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import javax.transaction.Transactional;
import java.util.*;

@Repository
public interface DeliveryRepository extends CrudRepository<Delivery, Long> {
    @Query("SELECT d.driver.id " +
            "FROM Delivery d " +
            "WHERE   d.driver.city = :city AND " +
                    "d.deliveryTime = :deliveryTime")
    Set<Long> findDriversByCityAndTime(
            @Param("city") City city,
            @Param("deliveryTime") Date deliveryTime);


    //I preferred to use driver id instead of driver because this way we won't use join 2 joins
    @Query("select driver.id AS id, COUNT(deliveryTime) AS deliveriesCount from Delivery where driver.city = :city GROUP BY driver.id  order by deliveriesCount ASC ")
    List<DriverIdDeliveryTime> getDriverDeliveries(@Param("city") City city);



    @Query("select d.driver AS driver, SUM(d.distance) As distance from Delivery d WHERE d.driver.city = :city GROUP BY d.driver ORDER BY distance DESC")
    List<DriverDistance> getRankDriverDistanceByCity(@Param("city") City city);

    @Modifying
    @Transactional
    @Query(value = "INSERT INTO Delivery(id,driver,restaurant,customer,deliveryTime,distance) values (:id,:driver, :restaurant, :customer, :deliveryTime, :distance)", nativeQuery = true)
    int insertDelivery(
            @Param("id") Long id,
            @Param("driver") Driver driver,
            @Param("restaurant") Restaurant restaurant,
            @Param("customer") Customer customer,
            @Param("deliveryTime") Date deliveryTime,
            @Param("distance") Double distance);



    //I use d.driver because you made an interface and i think you wanted this way (i would use the first way that found in getDriverDeliveries)
    @Query("select d.driver AS driver, SUM(d.distance) AS distance from Delivery d GROUP BY d.driver ORDER BY distance DESC")
    List<DriverDistance> getRankDriverDistance();





}


