package com.peoplecatalogue.domain.objects;

import java.util.List;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Data
public class Person {

	private int id;
	private String firstName;
	private String lastName;
	private PersonAddress address;
	private List<PersonDish> signatureDishes;

	public Person(int id, String firstName, String lastName) {
		super();
		this.id = id;
		this.firstName = firstName;
		this.lastName = lastName;
	}

}
