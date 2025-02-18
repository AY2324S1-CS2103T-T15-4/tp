package connectify.model.person;

import connectify.model.Name;

/**
 * Represents a Person's name in the address book.
 * Guarantees: immutable; is valid as declared in {@link #isValidName(String)}
 */
public class PersonName extends Name implements Comparable<Name> {

    /**
     * Constructs a {@code Name}.
     *
     * @param name A valid name.
     */
    public PersonName(String name) {
        super(name);
    }

    @Override
    public int compareTo(Name o) {
        return this.toString().toLowerCase().compareTo(o.toString().toLowerCase());
    }

}
