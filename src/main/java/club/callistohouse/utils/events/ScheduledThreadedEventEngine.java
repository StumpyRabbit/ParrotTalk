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

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlTransient;

@XmlTransient
public abstract class ScheduledThreadedEventEngine extends EventEngine implements Runnable {
	private ScheduledThreadPoolExecutor executor;
	private int threadCount;
	private String threadNameBase = "eventThread";
	private int delay;

	public ScheduledThreadedEventEngine() {
		this(1);
	}

	public ScheduledThreadedEventEngine(int threadCount) {
		this("eventThread", threadCount, 100);
	}
	public ScheduledThreadedEventEngine(String threadNameBase, int threadCount, int delay) {
		super();
		this.threadNameBase = threadNameBase;
		this.threadCount = threadCount;
		this.delay = delay;
		x_startExecutor();
	}

	public void fireRun() { fire(this); }

	protected void x_startExecutor() {
		executor = new ScheduledThreadPoolExecutor(threadCount, new EventThreadFactory(), new EventRejectedExecutionHandler());
		addListener(new Listener<Runnable>(Runnable.class) {
			protected void handle(Runnable event) {
				event.run();
			}});
	}

	public void flushCancel() {
		executor.shutdown();
		x_startExecutor();
	}

	@Override
	public synchronized <GEvent> void fire(final GEvent event) {
		for (final Listener<?> listener : listeners) {
			if (listener.canHandle(event)) {
				try {
			    	executor.schedule(listener.getRunnable(event), delay, TimeUnit.MILLISECONDS);
				} catch (Exception handleException) {
					handleException.printStackTrace();
				}
			}
		}
	}

	public synchronized <GEvent> void fireImmediate(final Runnable runnable) {
		try {
	    	executor.execute(runnable);
		} catch (Exception handleException) {
			handleException.printStackTrace();
		}
	}

	@XmlTransient
	public class EventThreadFactory implements ThreadFactory {
		public Thread newThread(final Runnable r) {
			return new Thread(r, threadNameBase);
		}
	}
	@XmlTransient
	public class EventRejectedExecutionHandler implements RejectedExecutionHandler {
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			System.out.println("rejected execution: " + r);
		}
	}
}