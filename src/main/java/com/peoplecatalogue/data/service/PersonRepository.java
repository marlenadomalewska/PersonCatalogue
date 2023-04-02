package com.peoplecatalogue.data.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowMapper;
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
		RowMapper<Person> lambdaMapper = (rs, rowNum) -> new Person(rs.getInt("id"), rs.getString("first_name"),
			rs.getString("last_name"));
		return jdbcTemplate.query("SELECT id, first_name, last_name FROM person;", lambdaMapper);
	}

	public Person personGetById(int id) {
		return jdbcTemplate.query("SELECT "
			+ "p.id AS p_id,"
			+ "p.first_name, "
			+ "p.last_name,"
			+ "a.id AS a_id,"
			+ "a.city,"
			+ "a.street,"
			+ "a.house_number,"
			+ "a.postal_code,"
			+ "d.id AS d_id,"
			+ "d.name "
			+ "FROM person p "
			+ "LEFT JOIN address a ON a.id_person=p.id "
			+ "LEFT JOIN dish d ON d.id_person=p.id "
			+ "WHERE p.id=?;", rsExtractor, id);
	}

	private ResultSetExtractor<Person> rsExtractor = new ResultSetExtractor<Person>() {

		@Override
		public Person extractData(ResultSet rs) throws SQLException, DataAccessException {
			Person person = null;
			if (rs.next()) {
				person = new Person();
				person.setId(rs.getInt("p_id"));
				person.setFirstName(rs.getString("first_name"));
				person.setLastName(rs.getString("last_name"));
				PersonAddress address = null;
				int idAddress = rs.getInt("a_id");
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
				dishes.add(new PersonDish(rs.getInt("d_id"), rs.getString("name")));
			}
			person.setSignatureDishes(dishes);
			return person;
		}
	};

	public void personDelete(int id) {
		jdbcTemplate.update("DELETE FROM dish WHERE id_person=?; " + "DELETE FROM address WHERE id_person=?;"
			+ "DELETE FROM person WHERE id=?", id, id, id);
	}

	public void addressDelete(int id) {
		jdbcTemplate.update("DELETE FROM address WHERE id=?;", id);
	}

	public void dishDelete(int id) {
		jdbcTemplate.update("DELETE FROM dish WHERE id=?;", id);
	}

	public void personAdd(Person person) {
		jdbcTemplate.update("INSERT INTO person (first_name, last_name) VALUES (?,?)", person.getFirstName(),
			person.getLastName());
	}

	public void addressAdd(int idPerson, PersonAddress address) {
		jdbcTemplate.update(
			"INSERT INTO address (city, street, house_number, postal_code, person_id) VALUES(?,?,?,?,?)",
			address.getCity(), address.getStreet(), address.getHouseNumber(), address.getPostalCode(), idPerson);
	}

	public void dishAdd(int idPerson, PersonDish dish) {
		jdbcTemplate.update("INSERT INTO address (name, person_id) VALUES(?,?)", dish.getName(), idPerson);
	}

	public void personUpdate(Person person) {
		jdbcTemplate.update("UPDATE person SET first_name=?, last_name=? WHERE int id=?", person.getFirstName(),
			person.getLastName(), person.getId());
	}

	public void addressUpdate(PersonAddress address) {
		jdbcTemplate.update("UPDATE address SET city=?, street=?, house_number=?, postal_code=? WHERE int id=?",
			address.getCity(), address.getStreet(), address.getHouseNumber(), address.getPostalCode(),
			address.getId());
	}

	public void dishUpdate(PersonDish dish) {
		jdbcTemplate.update("UPDATE dish SET name=? WHERE id=?", dish.getName(), dish.getId());
	}
}
