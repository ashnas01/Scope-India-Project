package com.finalproject.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finalproject.finalproject.model.Country;
import com.finalproject.finalproject.model.State;
@Repository
public interface StateRepository extends JpaRepository<State, Integer> {
	 List<State> findByCountry(Country id);

}
