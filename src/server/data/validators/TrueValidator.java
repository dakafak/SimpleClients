package server.data.validators;

import server.data.payload.PayloadValidator;

public class TrueValidator extends PayloadValidator {
    @Override
    public boolean isValid(Object data) {
        return true;
    }
}
