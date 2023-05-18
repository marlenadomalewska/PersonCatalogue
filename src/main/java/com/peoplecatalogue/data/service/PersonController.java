package com.peoplecatalogue.data.service;

import java.util.Collection;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.peoplecatalogue.data.entity.Person;

@RestController
public class PersonController {

	private final PersonRepository repository;

	public PersonController(PersonRepository repository) {
		super();
		this.repository = repository;
	}

	@GetMapping("")
	public Collection<Person> getPersons() {
		return repository.personGetAll();
	}

	@GetMapping("/{id}")
	public Optional<Person> getPerson(@PathVariable("id") int id) {
		return repository.personGetById(id);
	}

	@DeleteMapping("/{id}")
	public void deletePerson(@PathVariable("id") int id) {
		repository.personDelete(id);
	}

	@PostMapping("")
	public void addPerson(@RequestBody Person person) {
		repository.personAdd(person);
	}

	@PutMapping("/{id}")
	public void editPerson(@PathVariable("id") int id, @RequestBody Person person) {
		person.setId(id);
		repository.personUpdate(person);
	}

	/* @PatchMapping("/{id}") public void changeFirstName(@PathVariable("id") int
	 * id,
	 * 
	 * @RequestParam(value = "firstName", defaultValue = "") String firstName) {
	 * repository.getPersonById(id).setFirstName(firstName); } */

}
