package com.peoplecatalogue.domain.views;

import com.peoplecatalogue.db.PersonRepository;
import com.peoplecatalogue.domain.objects.Person;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class MainView extends VerticalLayout {

	private final PersonRepository repository;
	private Grid<Person> grid;

	public MainView(PersonRepository repository) {
		super();
		this.repository = repository;
		grid = new Grid<>(Person.class);
		add(grid);
		loadAll();
	}

	private void loadAll() {
		grid.setItems(repository.personGetAll());
	}

}
