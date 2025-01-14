package com.finalproject.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finalproject.finalproject.model.City;
import com.finalproject.finalproject.model.State;


@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
	List<City> findByState(State stateid);

}
