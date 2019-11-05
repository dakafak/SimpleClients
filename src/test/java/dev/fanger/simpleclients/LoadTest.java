package dev.fanger.simpleclients;

import dev.fanger.simpleclients.examples.ClientLoadTest;
import dev.fanger.simpleclients.examples.ServerExample;

public class LoadTest {

	public static void main(String[] args) {
		new LoadTest();
	}

	public LoadTest() {
		ServerExample serverExample = new ServerExample();
		ClientLoadTest clientLoadTest = new ClientLoadTest();
	}

}
