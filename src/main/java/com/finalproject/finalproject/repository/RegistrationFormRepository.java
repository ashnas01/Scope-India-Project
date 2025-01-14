package com.finalproject.finalproject.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import com.finalproject.finalproject.model.RegistrationForm;

public interface RegistrationFormRepository extends JpaRepository<RegistrationForm, Integer> {
	
		public RegistrationForm findByVerificationcode(String code);
		public RegistrationForm findByEmail(String email);
}
