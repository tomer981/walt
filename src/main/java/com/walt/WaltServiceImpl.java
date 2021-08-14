package com.walt;

import com.walt.dao.*;
import com.walt.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaltServiceImpl implements WaltService {
    @Autowired
    DriverRepository driverRepository;

    @Autowired
    DeliveryRepository deliveryRepository;

    @Autowired
    CityRepository cityRepository;

    @Autowired
    RestaurantRepository restaurantRepository;

    @Autowired
    CustomerRepository customerRepository;


    private boolean isTheSameCity(Customer customer, Restaurant restaurant){
        return customer.getCity().equals(restaurant.getCity());
    }

    private boolean isDriverExist(DriverDistance driver,List<DriverDistance> driversRank){
        return driversRank.stream().anyMatch(driverDistanceObject -> driverDistanceObject.getDriver().getId().equals(driver.getDriver().getId()));
    }

    private void addZeroRank(List<DriverDistance> driversRank,List<DriverDistance> allDrivers){
        for (DriverDistance driver : allDrivers){
            if (!isDriverExist(driver,driversRank)){
                driversRank.add(driver);
            }
        }
    }


    @Override
    public Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) throws Exception {
        if (!isTheSameCity(customer,restaurant)){
            throw new Exception("The Customer and the Restaurant is not in the same City");
        }

        Set<Driver> driversByCity = driverRepository.findAllDriversByCity(customer.getCity());
        Set<Long> driversByCityAndTime = deliveryRepository.findDriversByCityAndTime(customer.getCity(),deliveryTime);//driverId
        List<DriverIdDeliveryTime> driversIdAndNumberOfDeliveryByCity = deliveryRepository.getDriverDeliveries(customer.getCity());//driverId,numberOFDeliveries
        Long driverId = null;

        if (driversByCity.size() == driversIdAndNumberOfDeliveryByCity.size()){ //all the drivers have delivery candidates =    driversIdAndNumberOfDeliveryByCity \  driversByCityAndTime
            driversIdAndNumberOfDeliveryByCity.removeIf(driverIdDelivery-> driversByCityAndTime.contains(driverIdDelivery.getId()));
            if (driversIdAndNumberOfDeliveryByCity.size() == 0){
                throw new Exception("All drivers is occupied at the time");
            }
            driverId = driversIdAndNumberOfDeliveryByCity.get(0).getId();

        }else {//have driver with no delivery - candidates = driversByCity \ driverDelivery
            Set<Long> driverDelivery = driversIdAndNumberOfDeliveryByCity.stream().map(driver -> driver.getId()).collect(Collectors.toSet());
            driversByCity.removeIf(driver -> driverDelivery.contains(driver.getId()));
            driverId = driversByCity.stream().findFirst().get().getId();
        }

        Driver driver = driverRepository.findById(driverId).get();
        Delivery delivery = null;


        try{
            delivery = new Delivery(driver,restaurant,customer,deliveryTime);
            delivery = deliveryRepository.save(delivery);
        }

        catch(Exception e) {
            throw new RuntimeException("The Delivery already exist in DB");
        }


        return delivery;
    }

    @Override
    public List<DriverDistance> getDriverRankReport() {
        List<DriverDistance> driversRank = deliveryRepository.getRankDriverDistance();//LinkHashSet
        List<DriverDistance> allDrivers = driverRepository.getAllDrivers();
        addZeroRank(driversRank,allDrivers);

        return driversRank;

    }

    @Override
    public List<DriverDistance> getDriverRankReportByCity(City city) {
        List<DriverDistance> driversRankByCity = deliveryRepository.getRankDriverDistanceByCity(city);
        List<DriverDistance> allDrivers = driverRepository.getDriverDistanceByCity(city);
        addZeroRank(driversRankByCity,allDrivers);

        return driversRankByCity;
    }

    @Override
    public Delivery PlaceOrder(String CustomerName,String cityName,String Address ,String deliveryTime,String restaurantName) throws Exception {
        City city = cityRepository.findByName(cityName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH");
        Date deliveryDate =  dateFormat.parse(deliveryTime);
        Restaurant restaurant = restaurantRepository.findByName(restaurantName);

        Customer customer = customerRepository.findByName(CustomerName);
        if (customer == null){
            customer = customerRepository.save(new Customer(CustomerName,city,Address));
        }

        return createOrderAndAssignDriver(customer,restaurant,deliveryDate);
    }
}
