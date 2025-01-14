package com.finalproject.finalproject.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.finalproject.finalproject.model.Country;
import com.finalproject.finalproject.model.State;
import com.finalproject.finalproject.repository.StateRepository;



@Service
public class StateService {
    @Autowired
    private StateRepository stateRepository;
    public List<State> getstate(){
        return stateRepository.findAll();
    }
    public List<State>getStateBy(Country countryid){
        return stateRepository.findByCountry(countryid);
    }
}
