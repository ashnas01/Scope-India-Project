package com.finalproject.finalproject.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.finalproject.finalproject.model.Country;
@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
	

}
