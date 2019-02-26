package ui;

import server.data.PayloadValidator;

public class TestPayloadValidator extends PayloadValidator<String> {

	@Override
	public boolean isValid(String data) {
		return true;
	}

}
