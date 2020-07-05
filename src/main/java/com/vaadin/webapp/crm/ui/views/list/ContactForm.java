package com.vaadin.webapp.crm.ui.views.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.shared.Registration;
import com.vaadin.webapp.crm.backend.entity.Company;
import com.vaadin.webapp.crm.backend.entity.Contact;

import java.util.List;

public class ContactForm extends FormLayout {

    TextField firstName=new TextField("First name");
    TextField lastName=new TextField("Last name");
    EmailField email=new EmailField("Email");
    ComboBox<Contact.Status> status=new ComboBox<>("Status");
    ComboBox<Company> company=new ComboBox<>("Company");

    Button save=new Button("Save");
    Button delete=new Button("Delete");
    Button close=new Button("Cancel");

    //Declaring binder to bind form data to java object
    Binder<Contact> binder = new BeanValidationBinder<>(Contact.class);

    public ContactForm(List<Company> companyList) {
         addClassName("contact-form");

         //binding form data to java object
        binder.bindInstanceFields(this);

        //setting statuses
        status.setItems(Contact.Status.values());

        //setting companies
        company.setItems(companyList);

        company.setItemLabelGenerator(Company::getName);

         add(firstName, lastName, email, status, company, createButtonsLayout())
;    }

    private Component createButtonsLayout() {
        save.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
        close.addThemeVariants(ButtonVariant.LUMO_TERTIARY);

        save.addClickShortcut(Key.ENTER);
        close.addClickShortcut(Key.ESCAPE);

        save.addClickListener(click -> validateAndSave());

        delete.addClickListener(click -> fireEvent(new DeleteEvent(this, binder.getBean())));

        close.addClickListener(click  -> fireEvent(new CloseEvent(this)));

        //Enabling or disabling save button
        binder.addStatusChangeListener(evt -> save.setEnabled(binder.isValid()));

        return new HorizontalLayout(save, delete, close);
    }

    private void validateAndSave() {
        if(binder.isValid()) {
            fireEvent(new SaveEvent(this, binder.getBean()));
        }
    }

    public void setContact(Contact contact){
        binder.setBean(contact);
    }

    // Events
    public static abstract class ContactFormEvent extends ComponentEvent<ContactForm> {
        private Contact contact;
        protected ContactFormEvent(ContactForm source, Contact contact){
            super(source,false);
            this.contact=contact;
        }

        public Contact getContact(){
            return contact;
        }
    }

        public static class SaveEvent extends ContactFormEvent{
        SaveEvent(ContactForm source,Contact contact){
            super(source,contact);
        }
    }

        public static class DeleteEvent extends ContactFormEvent{
        DeleteEvent(ContactForm source,Contact contact){
            super(source,contact);
        }
    }
        public static class CloseEvent extends ContactFormEvent{
        CloseEvent(ContactForm source){
            super(source,null);
        }
    }

    public<T extends ComponentEvent<?>> Registration addListener(Class<T> eventType,
                                                                 ComponentEventListener<T> listener) {
        return getEventBus().addListener(eventType, listener);
    }
}
