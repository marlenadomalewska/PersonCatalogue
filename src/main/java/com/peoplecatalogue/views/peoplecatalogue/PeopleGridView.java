package com.peoplecatalogue.views.peoplecatalogue;

import com.peoplecatalogue.data.entity.Person;
import com.peoplecatalogue.data.service.PersonRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteParameters;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("People Catalogue")
@Route(value = "")
@AnonymousAllowed
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
		add(bAdd, grid);
		loadAll();
	}

	private void addListeners() {
		bAdd.addClickListener(event -> {
			bAdd.getUI().ifPresent(ui -> ui.navigate(PersonView.class));
		});
	}

	private void initGrid() {
		grid = new Grid<>(Person.class, false);
		grid.addColumn(Person::getFirstName).setHeader("First name").setWidth("700px");
		grid.addColumn(Person::getLastName).setHeader("Last name").setWidth("700px");
		grid.addComponentColumn(source -> new HorizontalLayout(
			initButtonEdit(source), initButtonDelete(source))).setAutoWidth(true);
	}

	private Button initButtonEdit(Person source) {
		return new Button("Edit", event -> {
			getUI().ifPresent(ui -> ui.navigate(PersonView.class, new RouteParameters("id", String.valueOf(source.getId()))));
		});
	}

	private Button initButtonDelete(Person source) {
		return new Button("Delete", event -> {
			repository.personDelete(source.getId());
			Notification.show("Person deleted");
			loadAll();
		});
	}

	private void loadAll() {
		grid.setItems(repository.personGetAll());
	}

}
