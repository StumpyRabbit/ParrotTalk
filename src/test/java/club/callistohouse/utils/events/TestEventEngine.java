/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2003, 2016 Robert Withers
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * ******************************************************************************
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the Squeak/Pharo communities and the cryptography 
 * team, which are this software's foundation.
 * ******************************************************************************
 * porcini/whisper would not be possible without the ideas, implementation, 
 * brilliance and passion of the erights.org community, which is also this software's 
 * foundation.  In particular, I would like to thank the following individuals:
 *         Mark Miller
 *         Marc Stiegler
 *         Bill Franz
 *         Tyler Close 
 *         Kevin Reid
 *******************************************************************************/
package club.callistohouse.utils.events;

import static org.junit.Assert.assertEquals;

import java.rmi.RemoteException;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.junit.Test;

import club.callistohouse.utils.events.EventEngine;
import club.callistohouse.utils.events.Listener;
import club.callistohouse.utils.events.ThreadedEventEngine;

public class TestEventEngine {

	int eventCounter = 0;
	public void incrementCounter() { eventCounter++; }

	@Test
	public void testUnthreaded() throws InterruptedException {
		EventEngine producer = new EventEngine();
		// straight-up event class matching
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}
		});
		// match method matching
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}

			public boolean match(TestEvent1 event) {
				return event.testString.equals("I_AM");
			}
		});

		eventCounter = 0;
		for (int i = 0; i < 5; i++) {
			producer.fire(new TestEvent1());
		}
		assertEquals(10, eventCounter);
	}

	@Test
	public void testThreaded() throws InterruptedException {
		ThreadedEventEngine producer = new ThreadedEventEngine(1);
		// straight-up event class matching
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}
		});
		// match method matching
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}

			public boolean match(TestEvent1 event) {
				return event.testString.equals("I_AM");
			}
		});

		eventCounter = 0;
		for (int i = 0; i < 5; i++) {
			producer.fire(new TestEvent1());
		}
		Thread.sleep(100);
		assertEquals(10, eventCounter);
	}

	@Test
	public void testUnthreadedForwardingListener() throws InterruptedException, ExecutionException {
		EventEngine producer = new EventEngine();
		EventEngine producerForward = new EventEngine();
		producerForward.forwardEventsTo(producer);
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}
		});

		eventCounter = 0;
		for (int i = 0; i < 5; i++) {
			producerForward.fire(new TestEvent1());
		}
		Thread.sleep(100);
		assertEquals(5, eventCounter);
	}

	@Test
	public void testThreadedForwardingListener() throws InterruptedException, ExecutionException {
		ThreadedEventEngine producer = new ThreadedEventEngine();
		EventEngine producerForward = new EventEngine();
		producerForward.forwardEventsTo(producer);
		producer.addListener(new Listener<TestEvent1>(TestEvent1.class) {
			public void handle(TestEvent1 event) {
				incrementCounter();
			}
		});

		eventCounter = 0;
		for (int i = 0; i < 5; i++) {
			producerForward.fire(new TestEvent1());
		}
		Thread.sleep(100);
		assertEquals(5, eventCounter);
	}

	@Test
	public void testDosShellUnthreaded() throws RemoteException {
    	EventEngine producer = new EventEngine();
        // dos shell matching
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "test1") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "test2") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});
        // generic dos shell matching
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "*") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});

	    for (int i = 0; i < 10; i++) {
        	producer.fire(createRandomEvent());
        }
	    assertEquals(20, eventCounter);
	}

	@Test
	public void testDosShellThreaded() throws RemoteException, InterruptedException {
    	ThreadedEventEngine producer = new ThreadedEventEngine(1);
        // dos shell matching
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "test1") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "test2") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});
        // generic dos shell matching
        producer.addListener(new TestDosShellMatchListener(TestDosShellMatchEvent.class, "*") { 
        	public void handle(TestDosShellMatchEvent event) { incrementCounter(); }});

	    for (int i = 0; i < 10; i++) {
        	producer.fire(createRandomEvent());
        }
	    Thread.sleep(100);
	    assertEquals(20, eventCounter);
	}

	private static class TestEvent1 { String testString = "I_AM"; };

	private static Random random = new Random();

	private static Object createRandomEvent() {
		return random.nextBoolean() ? new TestDosShellMatchEvent("test1") : new TestDosShellMatchEvent("test2");
    }

    private static class TestDosShellMatchEvent {
    	String testString;
    	public TestDosShellMatchEvent(String testString) { this.testString = testString; }
    	public String toString() { return getClass().getSimpleName() +"(" + testString +")"; }
    };

    private static abstract class TestDosShellMatchListener extends Listener<TestDosShellMatchEvent> {
    	private TestDosShellMatchEvent matchEvent;

		public TestDosShellMatchListener(Class<TestDosShellMatchEvent> eventClazz, String testString) {
			super(eventClazz);
			matchEvent = new TestDosShellMatchEvent(testString);
		}
		public boolean match(TestDosShellMatchEvent event) {
			return dosShellMatch(matchEvent.testString, ((TestDosShellMatchEvent)event).testString);
		}
	};
}
