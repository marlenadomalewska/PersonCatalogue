package com.peoplecatalogue.views.peoplecatalogue;

import java.util.ArrayList;
import java.util.Optional;

import com.peoplecatalogue.data.entity.Person;
import com.peoplecatalogue.data.entity.PersonAddress;
import com.peoplecatalogue.data.entity.PersonDish;
import com.peoplecatalogue.data.service.PersonRepository;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
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
@Route(value = "person/:id?")
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
	private Button bExit;
	private Button bAddDish;
	private Person person;
	private TextField tfDishName;

	public PersonView(PersonRepository repository) {
		this.repository = repository;
		createGUI();
		addListeners();
	}

	private void createGUI() {
		tfFirstName = new TextField("First name");
		tfLastName = new TextField("Last name");
		tfFirstName.setRequired(true);
		tfLastName.setRequired(true);
		tfCity = new TextField("City");
		tfStreet = new TextField("Street");
		tfHouseNumber = new TextField("House number");
		tfPostalCode = new TextField("Postal code");
		tfDishName = new TextField();
		tfDishName.setWidthFull();

		bSave = new Button("Save");
		bExit = new Button("Exit");
		bAddDish = new Button("Add dish");

		gridDishes = initGridDishes();

		add(tfFirstName, tfLastName, new HorizontalLayout(new VerticalLayout(new H2("Address"), tfCity, tfStreet, tfHouseNumber, tfPostalCode),
			new VerticalLayout(new H2("Signature dishes"), new HorizontalLayout(tfDishName, bAddDish), gridDishes)), new HorizontalLayout(bSave, bExit));

		setSizeFull();
		setJustifyContentMode(JustifyContentMode.CENTER);
		setDefaultHorizontalComponentAlignment(Alignment.CENTER);
	}

	private void addListeners() {
		bSave.addClickListener(event -> doOnSave());
		bExit.addClickListener(event -> {
			getUI().ifPresent(ui -> ui.navigate(PeopleGridView.class));
		});
		bAddDish.addClickListener(event -> doOnAddDish());
		tfDishName.addKeyDownListener(event -> {
			if (event.getKey().equals(Key.ENTER)) {
				doOnAddDish();
			}
		});
	}

	private void doOnAddDish() {
		if (canDishBeAdded()) {
			PersonDish dish = new PersonDish();
			dish.setName(tfDishName.getValue());
			tfDishName.clear();
			((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems().add(dish);
			gridDishes.getDataProvider().refreshAll();
		} else {
			tfDishName.setInvalid(true);
		}
	}

	private boolean canDishBeAdded() {
		if (tfDishName.isEmpty()) {
			return false;
		}
		for (PersonDish dish : ((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems()) {
			if (tfDishName.getValue().equalsIgnoreCase(dish.getName())) {
				return false;
			}
		}
		return true;
	}

	private void doOnSave() {
		if (!tfFirstName.isInvalid() && !tfLastName.isInvalid()) {
			repository.personAdd(buildPerson());
			getUI().ifPresent(ui -> ui.navigate(PeopleGridView.class));
		}
	}

	@Override
	public void beforeEnter(BeforeEnterEvent event) {
		Optional<String> idParameter = event.getRouteParameters().get("id");
		if (idParameter.isPresent()) {
			loadPerson(repository.personGetById(Integer.parseInt(idParameter.get())));
		}
	}

	private Grid<PersonDish> initGridDishes() {
		gridDishes = new Grid<>(PersonDish.class, false);
		gridDishes.addColumn(PersonDish::getName).setHeader("Dish").setWidth("400px");
		gridDishes.addComponentColumn(source -> {
			return initBtnDelete(source);
		});

		gridDishes.setWidth("700px");
		gridDishes.setHeight("400px");
		return gridDishes;
	}

	private Button initBtnDelete(PersonDish source) {
		Button bDelete = new Button(VaadinIcon.CLOSE.create(), event -> {
			if (((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems().contains(source)) {
				((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems().remove(source);
			}
			gridDishes.getDataProvider().refreshAll();
		});
		bDelete.addThemeVariants(ButtonVariant.LUMO_ICON, ButtonVariant.LUMO_ERROR);
		return bDelete;
	}

	private void loadPerson(Optional<Person> optPerson) {
		this.person = optPerson.get();
		if (optPerson.isEmpty()) {
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

	private Person buildPerson() {
		if (person == null) {
			person = new Person();
		}
		person.setFirstName(tfFirstName.getValue());
		person.setLastName(tfLastName.getValue());
		PersonAddress address = null;
		if (tfCity.getOptionalValue().isPresent() || tfHouseNumber.getOptionalValue().isPresent() || tfPostalCode.getOptionalValue().isPresent()
			|| tfStreet.getOptionalValue().isPresent())
		{
			address = new PersonAddress();
			address.setCity(tfCity.getValue());
			address.setHouseNumber(tfHouseNumber.getValue());
			address.setPostalCode(tfPostalCode.getValue());
			address.setStreet(tfStreet.getValue());
		}
		person.setAddress(address);
		person.setSignatureDishes(new ArrayList<>());
		person.getSignatureDishes().addAll(((ListDataProvider<PersonDish>) gridDishes.getDataProvider()).getItems());
		return person;
	}
}
