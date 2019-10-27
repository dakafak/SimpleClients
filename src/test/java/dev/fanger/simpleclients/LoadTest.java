package dev.fanger.simpleclients;

import dev.fanger.simpleclients.examples.ClientLoadTest;
import dev.fanger.simpleclients.examples.ServerExample;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class LoadTest {

	@Test
	public void clientServerLoadTest() {
		ServerExample serverExample = new ServerExample();

		ClientLoadTest clientLoadTest = new ClientLoadTest();
	}

}
