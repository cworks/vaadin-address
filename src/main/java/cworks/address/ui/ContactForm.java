package cworks.address.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.*;
import cworks.address.model.Contact;

@Title("ContactForm")
@Theme("valo")
public class ContactForm extends FormLayout {

    private Button save = new Button("Save", this::save);
    private Button cancel = new Button("Cancel", this::cancel);
    private TextField firstName = new TextField("First name");
    private TextField lastName = new TextField("Last name");
    private TextField phone = new TextField("Phone");
    private TextField email = new TextField("Email");
    private DateField birthDate = new DateField("Birth date");

    /**
     * Model instance
     */
    private Contact contact;

    /**
     * Easily bind form to beans and manage validation and buffering
     */
    private BeanFieldGroup<Contact> formFieldBindings;

    public ContactForm() {
        configureComponents();
        buildLayout();
    }

    @Override
    public AddressBookUI getUI() {
        return (AddressBookUI)super.getUI();
    }

    private void configureComponents() {
        save.setClickShortcut(ShortcutAction.KeyCode.ENTER);
        setVisible(false);
    }

    private void buildLayout() {
        setSizeUndefined();
        setMargin(true);

        HorizontalLayout actions = new HorizontalLayout(save, cancel);
        actions.setSpacing(true);

        addComponents(actions, firstName, lastName, phone, email, birthDate);
    }

    public void edit(Contact contact) {
        this.contact = contact;
        if(contact != null) {
            // Bind the properties of the contact POJO to fields in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(contact, this);
            firstName.focus();
        }
        setVisible(contact != null);
    }

    public void save(Button.ClickEvent event) {
        try {
            // commit the fields from the UI to DAO
            formFieldBindings.commit();

            getUI().service.save(contact);

            String msg = String.format("Saved '%s %s'.",
                contact.getFirstName(),
                contact.getLastName());

            Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);

            getUI().refreshContacts();
        } catch (FieldGroup.CommitException e) {
            // validation errors could be shown here
        }
    }

    public void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Notification.Type.TRAY_NOTIFICATION);
        getUI().contactList.select(null);
    }
}
