package com.microservice.userservice.services;

import com.microservice.userservice.entities.Hotel;
import com.microservice.userservice.entities.Rating;
import com.microservice.userservice.entities.User;
import com.microservice.userservice.exception.ResourceNotFoundException;
import com.microservice.userservice.external.HotelService;
import com.microservice.userservice.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private HotelService hotelService;
    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Override
    public User saveUser(User user) {
        String randomUserId = UUID.randomUUID().toString();
        user.setUserId(randomUserId);
        return userRepository.save(user);
    }

    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(String userId) {
        User user =  userRepository.findById(userId)
                .orElseThrow(()->
                        new ResourceNotFoundException("User with given id is not found on server !!" + userId));
        //fettch rating of the above user from Rating service
        //http://localhost:8083/ratings/users/b8f8f2fb-de8f-43dd-af28-37c9b906802f
        Rating[] ratingOfUser = restTemplate.getForObject("http://RATING/ratings/users/"+user.getUserId(), Rating[].class);
        logger.info("{}", ratingOfUser);
        List<Rating> ratings = Arrays.stream(ratingOfUser).toList();
         List<Rating> ratingList =  ratings.stream().map(rating -> {
             System.out.println(rating.getHotelId());
             //ResponseEntity<Hotel> forEntity = restTemplate.getForEntity("http://HOTELSERVICE/hotels/"+ rating.getHotelId(), Hotel.class);
           Hotel hotel = hotelService.getHotel(rating.getHotelId());
          // logger.info("response status code: {}", forEntity.getStatusCode());
           rating.setHotel(hotel);
           return rating;
        }).collect(Collectors.toList());
        user.setRatings(ratingList);
        return user;
    }
}
