package com.walt;

import com.google.common.collect.Ordering;
import com.google.common.collect.Comparators;
import java.util.Comparator;

import com.walt.dao.*;
import com.walt.model.*;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;


import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@SpringBootTest()
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class WaltTest {

    @TestConfiguration
    static class WaltServiceImplTestContextConfiguration {

        @Bean
        public WaltService waltService() {
            return new WaltServiceImpl();
        }
    }

    @Autowired
    WaltService waltService;

    @Resource
    CityRepository cityRepository;

    @Resource
    CustomerRepository customerRepository;

    @Resource
    DriverRepository driverRepository;

    @Resource
    DeliveryRepository deliveryRepository;

    @Resource
    RestaurantRepository restaurantRepository;

    @BeforeEach()
    public void prepareData() throws Exception {


        City jerusalem = new City("Jerusalem");
        City tlv = new City("Tel-Aviv");
        City bash = new City("Beer-Sheva");
        City haifa = new City("Haifa");

        cityRepository.save(jerusalem);
        cityRepository.save(tlv);
        cityRepository.save(bash);
        cityRepository.save(haifa);

        createDrivers(jerusalem, tlv, bash, haifa);

        createCustomers(jerusalem, tlv, haifa);

        createRestaurant(jerusalem, tlv);
    }

    private void createRestaurant(City jerusalem, City tlv) {
        Restaurant meat = new Restaurant("meat", jerusalem, "All meat restaurant");
        Restaurant vegan = new Restaurant("vegan", tlv, "Only vegan");
        Restaurant cafe = new Restaurant("cafe", tlv, "Coffee shop");
        Restaurant chinese = new Restaurant("chinese", tlv, "chinese restaurant");
        Restaurant mexican = new Restaurant("restaurant", tlv, "mexican restaurant ");

        restaurantRepository.saveAll(Lists.newArrayList(meat, vegan, cafe, chinese, mexican));
    }

    private void createCustomers(City jerusalem, City tlv, City haifa) {
        Customer beethoven = new Customer("Beethoven", tlv, "Ludwig van Beethoven");
        Customer mozart = new Customer("Mozart", jerusalem, "Wolfgang Amadeus Mozart");
        Customer chopin = new Customer("Chopin", haifa, "Frédéric François Chopin");
        Customer rachmaninoff = new Customer("Rachmaninoff", tlv, "Sergei Rachmaninoff");
        Customer bach = new Customer("Bach",tlv, "Sebastian Bach. Johann");

        customerRepository.saveAll(Lists.newArrayList(beethoven, mozart, chopin, rachmaninoff, bach));
    }

    private void createDrivers(City jerusalem, City tlv, City bash, City haifa) {
        Driver mary = new Driver("Mary", tlv);
        Driver patricia = new Driver("Patricia", tlv);
        Driver jennifer = new Driver("Jennifer", haifa);
        Driver james = new Driver("James", bash);
        Driver john = new Driver("John", bash);
        Driver robert = new Driver("Robert", jerusalem);
        Driver david = new Driver("David", jerusalem);
        Driver daniel = new Driver("Daniel", tlv);
        Driver noa = new Driver("Noa", haifa);
        Driver ofri = new Driver("Ofri", haifa);
        Driver nata = new Driver("Neta", jerusalem);
        driverRepository.saveAll(Lists.newArrayList(mary, patricia, jennifer, james, john, robert, david, daniel, noa, ofri, nata));
    }

    public void createDelivery(String CustomerName,String cityName ,String deliveryTime,String restaurantName,Double distance, String driverName) throws ParseException {
        City city = cityRepository.findByName(cityName);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH");
        Date deliveryDate =  dateFormat.parse(deliveryTime);
        Restaurant restaurant = restaurantRepository.findByName(restaurantName);
        Customer customer = customerRepository.findByName(CustomerName);
        Driver driver = driverRepository.findByName(driverName);

        Delivery delivery = new Delivery(driver,restaurant,customer,deliveryDate);
        delivery.setDistance(distance);

        deliveryRepository.save(delivery);
    }



    @Test
    public void testBasics(){

        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("Beer-Sheva")).size()), 2);

    }

    @Test
    public void testNoDriverAvailableInTheCityByTime() throws Exception {
        Delivery a = waltService.PlaceOrder("Beethoven","Tel-Aviv","asd","11/08/2021 16","cafe");//tlv
        Delivery b = waltService.PlaceOrder("Beethoven","Tel-Aviv","asd","11/08/2021 16","cafe");//tlv
        Delivery c = waltService.PlaceOrder("Beethoven","Tel-Aviv","asd","11/08/2021 16","cafe");//tlv

        try {
            Delivery d = waltService.PlaceOrder("Beethoven","Tel-Aviv","asd","11/08/2021 16","cafe");//tlv
            assertTrue("add Delivery when should not because no available driver at the time",false);
        }
        catch (Exception e){
            assertEquals(e.getMessage(),"All drivers is occupied at the time");
        }
    }




    @Test
    public void testCustomerAndRestaurantInDifferentCity(){
        try {
            Delivery a = waltService.PlaceOrder("Mozart","Tel-Aviv","asd","11/08/2021 16","cafe");//tlv
            assertTrue("Add delivery when should not because the restaurant and customer in difference city",false);
        }
        catch (Exception e){
            assertEquals(e.getMessage(),"The Customer and the Restaurant is not in the same City");
        }

    }


    @Test
    public void testDriverRankReport() throws Exception {

        createDelivery("Beethoven","Tel-Aviv","11/08/2021 16","vegan",1.0,"Mary");
        createDelivery("Beethoven","Tel-Aviv","11/08/2021 15","vegan",20.0,"Mary");
        createDelivery("Beethoven","Tel-Aviv","11/08/2021 16","vegan",1.0,"Patricia");
        createDelivery("Mozart","Jerusalem","11/08/2021 16","meat",2.0,"Robert");
        List<Long> driverRankReport = waltService.getDriverRankReport().stream().map(DriverDistance::getTotalDistance).collect(Collectors.toList());
        assertTrue("Not in correct order or unable to complete",Ordering.natural().reverse().isOrdered(driverRankReport));
    }

    @Test
    public void testDriverRankReportByCity() throws ParseException {
        createDelivery("Beethoven","Tel-Aviv","11/08/2021 16","vegan",1.0,"Mary");
        createDelivery("Beethoven","Tel-Aviv","11/08/2021 15","vegan",20.0,"Mary");
        createDelivery("Beethoven","Tel-Aviv","11/08/2021 16","vegan",1.0,"Patricia");
        List<Long> driverRankReportByCity = waltService.getDriverRankReportByCity(cityRepository.findByName("Tel-Aviv")).stream().map(DriverDistance::getTotalDistance).collect(Collectors.toList());
        assertTrue("Not in correct order or unable to complete",Ordering.natural().reverse().isOrdered(driverRankReportByCity));
    }

}
