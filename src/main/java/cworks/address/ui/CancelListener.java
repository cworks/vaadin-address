package cworks.address.ui;

import com.vaadin.event.ConnectorEventListener;
import cworks.address.model.Contact;

public interface CancelListener extends ConnectorEventListener {

    /**
     * This method is called when adding or updating a Contact is cancelled.
     *
     * @param contact the Contact as it was right before cancel was invoked.
     */
    void cancelled(Contact contact);
}
