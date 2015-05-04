package cworks.address.ui;

import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import cworks.address.model.Contact;
import cworks.address.service.ContactService;

import javax.servlet.annotation.WebServlet;

public class AddressBookUI extends UI {

    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressUIServlet extends VaadinServlet {

    }

    private TextField filter = new TextField();

    private Grid contactList = new Grid();

    private Button newContact = new Button("New Contact");

    /**
     * Custom contact form
     */
    private ContactForm contactForm = new ContactForm();

    private ContactService contactService = ContactService.create();

    @Override
    protected void init(VaadinRequest request) {
        configureComponents();
        buildLayout();
    }

    void refreshContacts() {
        refreshContacts(filter.getValue());
    }

    private void configureComponents() {
        newContact.addClickListener(event -> {
            contactForm.edit(new Contact());
        });

        filter.setInputPrompt("Filter Contacts...");

        filter.addTextChangeListener(event -> {
            refreshContacts(event.getText());
        });

        contactList.setContainerDataSource(new BeanItemContainer<>(Contact.class));
        contactList.setColumnOrder("firstName", "lastName", "email");
        contactList.removeColumn("id");
        contactList.removeColumn("birthDate");
        contactList.removeColumn("phone");
        contactList.setSelectionMode(Grid.SelectionMode.SINGLE);
        contactList.addSelectionListener(event -> {
            contactForm.edit((Contact) contactList.getSelectedRow());
        });
        refreshContacts();
    }

    private void buildLayout() {
        HorizontalLayout actions = new HorizontalLayout(filter, newContact);
        actions.setWidth("100%");
        filter.setWidth("100%");
        actions.setExpandRatio(filter, 1);

        VerticalLayout left = new VerticalLayout(actions, contactList);
        left.setSizeFull();
        contactList.setSizeFull();
        left.setExpandRatio(contactList, 1);

        HorizontalLayout mainLayout = new HorizontalLayout(left, contactForm);
        mainLayout.setSizeFull();
        mainLayout.setExpandRatio(left, 1);

        setContent(mainLayout);
    }

    private void refreshContacts(String filter) {
        contactList.setContainerDataSource(new BeanItemContainer<>(
                Contact.class, contactService.findAll(filter)));
        contactForm.setVisible(false);
    }

}
