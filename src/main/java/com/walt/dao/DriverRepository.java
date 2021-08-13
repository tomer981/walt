package com.walt.dao;

import com.walt.model.City;
import com.walt.model.Driver;
import com.walt.model.DriverDistance;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface DriverRepository extends CrudRepository<Driver,Long> {
    Set<Driver> findAllDriversByCity(City city);

    @Query("select d AS driver,0 AS distance from Driver d where d.id is not null")
    List<DriverDistance> getAllDrivers();

    @Query("select d AS driver,0 As distance from Driver d where d.city = :city")
    List<DriverDistance> getDriverDistanceByCity(@Param("city") City city);






    Driver findByName(String name);
}
//
// Spring, maven
//         working knowledge of SQL
//         cloud platforms (i.e. AWS)
//         Proven hands-on Software Development experience in Java
