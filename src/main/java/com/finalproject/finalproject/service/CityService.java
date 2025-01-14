package com.finalproject.finalproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finalproject.finalproject.model.City;
import com.finalproject.finalproject.model.State;
import com.finalproject.finalproject.repository.CityRepository;



@Service
public class CityService {
    @Autowired
    private CityRepository cityRepository;
    public List<City>getCity(){
        return cityRepository.findAll();
    }
    public List<City>getCityBy(State stateid){
        return cityRepository.findByState(stateid);
    }


}
