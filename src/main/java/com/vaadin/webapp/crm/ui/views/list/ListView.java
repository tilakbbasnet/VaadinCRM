package com.vaadin.webapp.crm.ui.views.list;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.webapp.crm.backend.entity.Company;
import com.vaadin.webapp.crm.backend.entity.Contact;
import com.vaadin.webapp.crm.backend.service.CompanyService;
import com.vaadin.webapp.crm.backend.service.ContactService;
import com.vaadin.webapp.crm.ui.MainLayout;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Contacts | Vaadin CRM")
@CssImport("./styles/shared-styles.css")
public class ListView extends VerticalLayout {
    private ContactService contactService;
    Grid<Contact> grid = new Grid<>(Contact.class);
    TextField filterField = new TextField();
    private final ContactForm contactForm;

    public ListView(ContactService contactService,
                    CompanyService companyService) {
        this.contactService = contactService;
        addClassName("list-view");
        setSizeFull();

        //configure contact grid/table to show  selected column in defined order
        configureGrid();
        getToolBar();

        //instantiating new Contact Form
        contactForm = new ContactForm(companyService.findAll());
        contactForm.addListener(ContactForm.SaveEvent.class, this::saveContact);
        contactForm.addListener(ContactForm.DeleteEvent.class, this::deleteContact);
        contactForm.addListener(ContactForm.CloseEvent.class, e -> closeUpdateForm());

        Div allContent = new Div(grid, contactForm);
        allContent.addClassName("content");
        allContent.setSizeFull();

        add(getToolBar(), allContent);

        //this method fetches the data from backend and display data in grid
        updateList();

        closeUpdateForm();

        /*Button btn = new Button("Click Me");
        DatePicker datePicker = new DatePicker("Date Here");

        HorizontalLayout layout = new HorizontalLayout(datePicker, btn);
        layout.setDefaultVerticalComponentAlignment(Alignment.END);

        add(layout);

        btn.addClickListener(cl -> add(new Paragraph("Clicked "+datePicker.getValue())));*/
    }

    private void deleteContact(ContactForm.DeleteEvent evt) {
        contactService.delete(evt.getContact());
        updateList();
        closeUpdateForm();
    }

    private void saveContact(ContactForm.SaveEvent evt) {
        contactService.save(evt.getContact());
        updateList();
        closeUpdateForm();
    }

    private void closeUpdateForm() {
        contactForm.setContact(null);
        contactForm.setVisible(false);
        removeClassName("editing");
    }

    private HorizontalLayout getToolBar() {
        filterField.setPlaceholder("Filter By Name..");
        //show cross(x) icon
        filterField.setClearButtonVisible(true);
        filterField.setValueChangeMode(ValueChangeMode.LAZY);
        filterField.addValueChangeListener(event -> updateList());

        Button newContactButton = new Button("New Contact", buttonClickEvent -> addContact(new Contact()));

        HorizontalLayout toolBar = new HorizontalLayout(filterField, newContactButton);
        toolBar.addClassName("toolbar");
        return toolBar;
    }

    private void addContact(Contact contact) {
        grid.asSingleSelect().clear();
        editContact(contact);
    }

    private void updateList() {
        grid.setItems(contactService.findAll(filterField.getValue()));
    }

    private void configureGrid() {
        grid.addClassName("contact-grid");
        grid.setSizeFull();
        //removing column company
        grid.removeColumnByKey("company");
        grid.setColumns("firstName","lastName","email","status");

        //Getting company name for each contact and dispalying on grid
        grid.addColumn(contact -> {
            Company company= contact.getCompany();
            return contact == null ? "-" : company.getName();
        }).setHeader("Company");

        //setting auto size of the columns
        grid.getColumns().forEach(column -> column.setAutoWidth(true));

        //Single Row selector
        grid.asSingleSelect().addValueChangeListener(evt -> editContact(evt.getValue()));
    }

    private void editContact(Contact contact) {
      if(contact == null) {
          closeUpdateForm();
      }else {
          contactForm.setContact(contact);
          contactForm.setVisible(true);
          contactForm.setClassName("editing");
      }
    }
}
