package dev.fanger.simpleclients;

import dev.fanger.simpleclients.examples.client.ClientLoadTest;
import dev.fanger.simpleclients.examples.SimpleServerExample;

public class LoadTest {

	public static void main(String[] args) {
		new LoadTest();
	}

	public LoadTest() {
		SimpleServerExample serverExample = new SimpleServerExample();
		ClientLoadTest clientLoadTest = new ClientLoadTest();

		serverExample.shutdownServer();
	}

}
