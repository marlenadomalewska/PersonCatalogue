package com.peoplecatalogue.data.service;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.peoplecatalogue.data.entity.Person;
import com.peoplecatalogue.data.entity.PersonAddress;
import com.peoplecatalogue.data.entity.PersonDish;

@Repository
public class PersonRepository {

	private final JdbcTemplate jdbcTemplate;

	public PersonRepository(JdbcTemplate jdbcTemplate) {
		super();
		this.jdbcTemplate = jdbcTemplate;
	}

	public Collection<Person> personGetAll() {
		RowMapper<Person> lambdaMapper = (rs, rowNum) -> new Person(rs.getInt("id_p"), rs.getString("first_name"),
			rs.getString("last_name"));
		return jdbcTemplate.query("SELECT id_p, first_name, last_name FROM person;", lambdaMapper);
	}

	public Optional<Person> personGetById(int id) {
		return jdbcTemplate.query("SELECT "
			+ "p.id_p,"
			+ "p.first_name, "
			+ "p.last_name,"
			+ "a.id_a,"
			+ "a.city,"
			+ "a.street,"
			+ "a.house_number,"
			+ "a.postal_code,"
			+ "d.id_d,"
			+ "d.dish_name "
			+ "FROM person p "
			+ "LEFT JOIN address a ON a.id_p=p.id_p "
			+ "LEFT JOIN dish d ON d.id_p=p.id_p "
			+ "WHERE p.id_p=?;", rsExtractor, id);
	}

	private ResultSetExtractor<Optional<Person>> rsExtractor = new ResultSetExtractor<Optional<Person>>() {

		@Override
		public Optional<Person> extractData(ResultSet rs) throws SQLException, DataAccessException {
			Person person = null;
			if (rs.next()) {
				person = new Person();
				person.setId(rs.getInt("id_p"));
				person.setFirstName(rs.getString("first_name"));
				person.setLastName(rs.getString("last_name"));
				PersonAddress address = null;
				int idAddress = rs.getInt("id_a");
				if (idAddress != 0) {
					address = new PersonAddress();
					address.setId(idAddress);
					address.setCity(rs.getString("city"));
					address.setHouseNumber(rs.getString("house_number"));
					address.setPostalCode(rs.getString("postal_code"));
					address.setStreet(rs.getString("street"));
				}
				person.setAddress(address);
			}
			List<PersonDish> dishes = new ArrayList<>();
			while (rs.next()) {
				dishes.add(new PersonDish(rs.getInt("id_d"), rs.getString("dish_name")));
			}
			person.setSignatureDishes(dishes);
			return Optional.ofNullable(person);
		}
	};

	public void personDelete(int idP) {
		jdbcTemplate.update("DELETE FROM dish WHERE id_p=?; "
			+ "DELETE FROM address WHERE id_p=?;"
			+ "DELETE FROM person WHERE id_p=?;", idP, idP, idP);
	}

	public void addressDelete(int idA) {
		jdbcTemplate.update("DELETE FROM address WHERE id_a=?;", idA);
	}

	public void dishDelete(int idD) {
		jdbcTemplate.update("DELETE FROM dish WHERE id_d=?;", idD);
	}

	public void personAdd(Person person) {
		String sql = "INSERT INTO person (first_name, last_name) VALUES (?,?) RETURNING id_p;";
		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, person.getFirstName());
			ps.setString(2, person.getLastName());
			return ps;
		}, keyHolder);
		int id = keyHolder.getKey().intValue();
		if (person.getAddress() != null) {
			addressAdd(id, person.getAddress());
		}
		if (person.getSignatureDishes() != null && person.getSignatureDishes().size() > 0) {
			person.getSignatureDishes().stream().forEach(dish -> dishAdd(id, dish));
		}
	}

	public void addressAdd(int idP, PersonAddress address) {
		jdbcTemplate.update(
			"INSERT INTO address (city, street, house_number, postal_code, id_p) VALUES(?,?,?,?,?)",
			address.getCity(), address.getStreet(), address.getHouseNumber(), address.getPostalCode(), idP);
	}

	public void dishAdd(int idP, PersonDish dish) {
		jdbcTemplate.update("INSERT INTO dish (dish_name, id_p) VALUES(?,?)", dish.getName(), idP);
	}

	public void personUpdate(Person person) {
		jdbcTemplate.update("UPDATE person SET first_name=?, last_name=? WHERE id_p=?", person.getFirstName(),
			person.getLastName(), person.getId());
	}

	public void addressUpdate(PersonAddress address) {
		jdbcTemplate.update("UPDATE address SET city=?, street=?, house_number=?, postal_code=? WHERE id_a=?",
			address.getCity(), address.getStreet(), address.getHouseNumber(), address.getPostalCode(),
			address.getId());
	}

	public void dishUpdate(PersonDish dish) {
		jdbcTemplate.update("UPDATE dish SET dish_name=? WHERE id_d=?", dish.getName(), dish.getId());
	}
}
