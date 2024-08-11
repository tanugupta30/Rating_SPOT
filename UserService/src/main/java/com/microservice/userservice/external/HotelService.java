package com.microservice.userservice.external;

import com.microservice.userservice.entities.Hotel;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name= "HOTELSERVICE")
public interface HotelService {
    @GetMapping("/hotels/{hotelId}")
    Hotel getHotel(@PathVariable("hotelId") String hotelId);

}
