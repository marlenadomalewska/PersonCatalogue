package com.peoplecatalogue.views.peoplecatalogue;

import java.util.Optional;

import com.peoplecatalogue.data.entity.Person;
import com.peoplecatalogue.data.entity.PersonDish;
import com.peoplecatalogue.data.service.PersonRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;

@PageTitle("Person")
@Route("person/:id?")
@AnonymousAllowed
public class PersonView extends VerticalLayout implements BeforeEnterObserver {

	private final PersonRepository repository;
	private TextField tfFirstName;
	private TextField tfLastName;
	private Grid<PersonDish> gridDishes;
	private TextField tfCity;
	private TextField tfStreet;
	private TextField tfHouseNumber;
	private TextField tfPostalCode;
	private Button bSave;
	private Person person;

	public PersonView(PersonRepository repository) {
		this.repository = repository;
		tfFirstName = new TextField("First name");
		tfLastName = new TextField("Last name");
		tfFirstName.setRequired(true);
		tfLastName.setRequired(true);
		tfCity = new TextField("City");
		tfStreet = new TextField("Street");
		tfHouseNumber = new TextField("House number");
		tfPostalCode = new TextField("Postal code");

		initGridDishes();
		add(tfFirstName, tfLastName, new HorizontalLayout(new VerticalLayout(new H2("Address"), tfCity, tfStreet, tfHouseNumber, tfPostalCode), gridDishes));
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<String> idParameter = event.getRouteParameters().get("id");
		if (idParameter.isPresent()) {
			loadPerson(repository.personGetById(Integer.parseInt(idParameter.get())));
		}
	}

	private void loadPerson(Person person) {
		this.person = person;
		if (person == null) {
			return;
		}
		tfFirstName.setValue(person.getFirstName());
		tfLastName.setValue(person.getLastName());
		if (person.getAddress() != null) {
			tfCity.setValue(person.getAddress().getCity());
			tfHouseNumber.setValue(person.getAddress().getHouseNumber());
			tfPostalCode.setValue(person.getAddress().getPostalCode());
			tfStreet.setValue(person.getAddress().getStreet());
		}
		gridDishes.setItems(person.getSignatureDishes());
	}

	private void initGridDishes() {
		gridDishes = new Grid<>(PersonDish.class, false);
		gridDishes.addColumn(PersonDish::getName).setHeader("Dish").setAutoWidth(true);
		gridDishes.addComponentColumn(source -> {
			Icon i = new Icon(VaadinIcon.CLIPBOARD_CROSS);
			i.addClickListener(event -> {
				((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems().remove(source);
				gridDishes.getDataProvider().refreshAll();
			});
			return i;
		});
		gridDishes.setWidth("600px");
		gridDishes.setHeight("400px");
	}
}
