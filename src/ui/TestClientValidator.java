package ui;

import connection.ClientValidator;
import connection.Connection;

public class TestClientValidator extends ClientValidator {

    @Override
    public boolean isValid(Connection connectionToValidate) {
        return false;
    }

}
