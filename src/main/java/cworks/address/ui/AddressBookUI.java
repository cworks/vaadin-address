package cworks.address.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import cworks.address.model.Contact;
import cworks.address.service.ContactService;

import javax.servlet.annotation.WebServlet;

@Title("AddressBookUI")
@Theme("valo")
public class AddressBookUI extends UI {

    /**
     * Servlet bound to this UI
     */
    @WebServlet(urlPatterns = "/*")
    @VaadinServletConfiguration(ui = AddressBookUI.class, productionMode = false)
    public static class AddressUIServlet extends VaadinServlet { }

    /**
     * TextField used to filter the contactList Grid
     */
    private TextField filter = new TextField();

    /**
     * Grid used to list Contact(s)
     */
    private Grid contactList = new Grid();

    /**
     * Button used to add a new Contact
     */
    private Button newContact = new Button("New Contact");

    /**
     * Custom contact form
     */
    private ContactForm contactForm;

    /**
     * Constructor
     */
    public AddressBookUI() {
        contactForm = new ContactForm();
        contactForm.addCancelledListener(contact -> {
            // if cancel event if fired from the form then
            // we clear the contactList selection
            contactList.select(null);
        });
        contactForm.addSavedListener(contact -> {
            refreshContacts();
        });
    }

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
        actions.setWidth("50%");
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
                Contact.class, ContactService.findAll(filter)));
        contactForm.setVisible(false);
    }

}
