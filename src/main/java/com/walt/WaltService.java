package com.walt;

import com.walt.model.*;

import java.util.Date;
import java.util.List;

public interface WaltService{

    Delivery createOrderAndAssignDriver(Customer customer, Restaurant restaurant, Date deliveryTime) throws Exception;

    List<DriverDistance> getDriverRankReport();

    List<DriverDistance> getDriverRankReportByCity(City city);

    Delivery PlaceOrder(String CustomerName,String cityName,String Address ,String deliveryTime,String restaurantName) throws Exception;

}

