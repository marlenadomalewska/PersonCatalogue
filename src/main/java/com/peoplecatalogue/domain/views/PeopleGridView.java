package com.peoplecatalogue.domain.views;

import com.peoplecatalogue.db.PersonRepository;
import com.peoplecatalogue.domain.objects.Person;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route
public class PeopleGridView extends VerticalLayout {

	// TODO change repository so it communicates through rests

	private final PersonRepository repository;
	private Grid<Person> grid;
	private Button bAdd;

	public PeopleGridView(PersonRepository repository) {
		super();
		this.repository = repository;
		createGUI();
		addListeners();

	}

	private void createGUI() {
		bAdd = new Button("Add person");
		initGrid();
		add(bAdd);
		add(grid);
		loadAll();
	}

	private void addListeners() {
		bAdd.addClickListener(event -> {
			;
			loadAll();
		});
	}

	private void initGrid() {
		grid = new Grid<>(Person.class, false);
		grid.addColumn(Person::getFirstName).setHeader("First name");
		grid.addColumn(Person::getLastName).setHeader("Last name");
		grid.addComponentColumn(source -> new HorizontalLayout(
			new Button("Edit", event -> {
				;
			}),
			new Button("Delete", event -> {
				repository.personDelete(source.getId());
				loadAll();
			})));
	}

	private void loadAll() {
		grid.setItems(repository.personGetAll());
	}

}
