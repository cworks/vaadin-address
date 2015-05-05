package cworks.address.ui;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.event.ShortcutAction;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import cworks.address.model.Contact;
import cworks.address.service.ContactService;

import java.util.ArrayList;
import java.util.List;

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
     * List of CancelListener(s) that we call when/if the cancel button is clicked
     */
    private List<CancelListener> cancelListeners = new ArrayList<>();

    /**
     * List of SaveListener(s) that we call when/if a Contact is saved
     */
    private List<SaveListener> saveListeners = new ArrayList<>();

    /**
     * Model instance
     */
    private Contact contact;

    /**
     * Easily bind form to beans and manage validation and buffering
     */
    private BeanFieldGroup<Contact> formFieldBindings;

    /**
     * Constructor
     */
    public ContactForm() {
        configureComponents();
        buildLayout();
    }

    /**
     * Add an Action.Listener that we will call when/if the cancel button is clicked.
     * @param listener
     */
    public void addCancelledListener(CancelListener listener) {
        cancelListeners.add(listener);
    }

    /**
     * Add a SaveListener that we will callback when/if a Contact is saved.
     * @param listener
     */
    public void addSavedListener(SaveListener listener) {
        saveListeners.add(listener);
    }

    void edit(Contact contact) {
        this.contact = contact;
        if(contact != null) {
            // Bind the properties of the contact POJO to fields in this form
            formFieldBindings = BeanFieldGroup.bindFieldsBuffered(contact, this);
            firstName.focus();
        }
        setVisible(contact != null);
    }

    void save(Button.ClickEvent event) {
        try {
            // commit the fields from the UI to DAO
            formFieldBindings.commit();

            // Save the contact using the ContactService
            ContactService.save(contact);

            String msg = String.format("Saved '%s %s'.",
                contact.getFirstName(),
                contact.getLastName());

            Notification.show(msg, Notification.Type.TRAY_NOTIFICATION);

            if(saveListeners != null && saveListeners.size() > 0) {
                // Notify each CancelListener
                for (SaveListener listener : saveListeners) {
                    listener.saved(contact);
                }
            }

        } catch (FieldGroup.CommitException e) {
            // validation errors could be shown here
        }
    }

    void cancel(Button.ClickEvent event) {
        // Place to call business logic.
        Notification.show("Cancelled", Notification.Type.TRAY_NOTIFICATION);

        if(cancelListeners != null && cancelListeners.size() > 0) {
            // Notify each CancelListener
            for (CancelListener listener : cancelListeners) {
                listener.cancelled(formFieldBindings.getItemDataSource().getBean());
            }
        }
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

}
