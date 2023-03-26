package com.peoplecatalogue.domain;

import java.util.Collection;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.peoplecatalogue.db.PersonRepository;
import com.peoplecatalogue.domain.objects.Person;

@RestController
@RequestMapping("")
public class PersonController {

	private final PersonRepository repository;

	public PersonController(PersonRepository repository) {
		super();
		this.repository = repository;
	}

	@GetMapping
	public Collection<Person> getPersons() {
		return repository.getAllPerson();
	}

	@GetMapping("/{id}")
	public Person getPerson(@PathVariable("id") int id) {
		return repository.getPersonById(id);
	}

	@DeleteMapping("/{id}")
	public void deletePerson(@PathVariable("id") int id) {
		repository.deletePerson(id);
	}

	@PostMapping
	public void addPerson(@RequestBody Person person) {
		repository.addPerson(person);
	}

	@PutMapping("/{id}")
	public void editPerson(@PathVariable("id") int id, @RequestBody Person person) {
		person.setId(id);
		repository.updatePerson(person);
	}

	/*
	 * @PatchMapping("/{id}") public void changeFirstName(@PathVariable("id") int
	 * id,
	 * 
	 * @RequestParam(value = "firstName", defaultValue = "") String firstName) {
	 * repository.getPersonById(id).setFirstName(firstName); }
	 */

}
