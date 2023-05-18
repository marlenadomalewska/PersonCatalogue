package com.peoplecatalogue.data.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class DishRepository {

	private final JdbcTemplate jdbcTemplate;

	public DishRepository(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

}
