package com.finalproject.finalproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finalproject.finalproject.model.Country;
import com.finalproject.finalproject.repository.CountryRepository;


@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;
    public List<Country> countryList(){
        return countryRepository.findAll();
    }
}