package com.walt;

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

import static org.junit.Assert.assertEquals;

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

        createDeliveries();


        City city = cityRepository.findByName("Tel-Aviv");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH");
        Date deliveryTime = dateFormat.parse("11/08/2021 16");
        Customer customer = customerRepository.findByName("Beethoven");
        Restaurant restaurant = restaurantRepository.findByName("vegan");

        Delivery a = waltService.createOrderAndAssignDriver(customer,restaurant,deliveryTime);
        List<DriverDistance> b = waltService.getDriverRankReport();
        List<DriverDistance> c = waltService.getDriverRankReportByCity(tlv);
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
        Customer bach = new Customer("Bach", tlv, "Sebastian Bach. Johann");

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

    private void createDeliveries() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH");
        City Jerusalem = cityRepository.findByName("Jerusalem");
        City tlv = cityRepository.findByName("Tel-Aviv");

        Restaurant meat = restaurantRepository.findByName("meat");//jur
        Restaurant vegan = restaurantRepository.findByName("vegan");//tlv
        Restaurant cafe = restaurantRepository.findByName("cafe");//tlv

        Driver robert = driverRepository.findByName("Robert");//jur
        Driver mary = driverRepository.findByName("Mary");//tlv
        Driver patricia = driverRepository.findByName("Patricia");//tlv
        Driver daniel = driverRepository.findByName("Daniel");//tlv

        Customer mozart = customerRepository.findByName("Mozart");
        Customer beethoven = customerRepository.findByName("Beethoven");
        Customer rachmaninoff = customerRepository.findByName("Rachmaninoff");

        Date deliveryTime = dateFormat.parse("11/08/2021 16");


        Delivery a = new Delivery(robert,meat,mozart,deliveryTime);
        Delivery b = new Delivery(mary,vegan,beethoven,deliveryTime);
        Delivery c = new Delivery(patricia,vegan,beethoven,deliveryTime);
        deliveryTime = dateFormat.parse("11/08/2021 15");
        Delivery d = new Delivery(mary,cafe,rachmaninoff,deliveryTime);
        Delivery e = new Delivery(daniel,cafe,rachmaninoff,deliveryTime);


        deliveryRepository.save(a);
        deliveryRepository.save(b);
        deliveryRepository.save(c);
        deliveryRepository.save(d);
        deliveryRepository.save(e);
    }

    @Test
    public void testBasics(){

        assertEquals(((List<City>) cityRepository.findAll()).size(),4);
        assertEquals((driverRepository.findAllDriversByCity(cityRepository.findByName("Beer-Sheva")).size()), 2);
    }
}
