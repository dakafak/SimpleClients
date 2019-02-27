package testclasses;

import server.data.payload.PayloadValidator;

public class TestPayloadValidator extends PayloadValidator<String> {

    @Override
    public boolean isValid(String data) {
        return true;
    }

}
