package cworks.address.ui;

import cworks.address.model.Contact;

public interface SaveListener {

    /**
     * This method is called after a Contact is saved
     *
     * @param contact the Contact that was saved.
     */
    void saved(Contact contact);

}
