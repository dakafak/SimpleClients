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

		System.out.println("Shutting down server");
		serverExample.shutdownServer();
		System.out.println("Done running load test");
	}

}
