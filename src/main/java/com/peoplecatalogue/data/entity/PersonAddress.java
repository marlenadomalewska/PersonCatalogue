package com.peoplecatalogue.data.entity;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Data
@NoArgsConstructor
public class PersonAddress {
	private int id;
	private String city;
	private String street;
	private String houseNumber;
	private String postalCode;

}
