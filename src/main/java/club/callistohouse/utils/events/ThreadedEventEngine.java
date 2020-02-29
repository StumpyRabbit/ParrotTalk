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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadedEventEngine extends EventEngine {
	private ExecutorService executorService;
	private int threadCount;
	private String threadNameBase = "eventThread";

	public ThreadedEventEngine() {
		this(1);
	}

	public ThreadedEventEngine(int threadCount) {
		this("eventThread", threadCount);
	}
	public ThreadedEventEngine(String threadNameBase, int threadCount) {
		super();
		this.threadNameBase = threadNameBase;
		this.threadCount = threadCount;
		x_startExecutor();
	}

	protected void x_startExecutor() {
		executorService = new ThreadPoolExecutor(threadCount, threadCount, 0L, TimeUnit.NANOSECONDS, new LinkedBlockingQueue<Runnable>(), new EventThreadFactory());
	}

	public void flushCancel() {
		executorService.shutdown();
		x_startExecutor();
	}

	@Override
	public synchronized <GEvent> void fire(final GEvent event) {
		for (final Listener<?> listener : listeners) {
			if (listener.canHandle(event)) {
				try {
			    	executorService.submit(listener.getRunnable(event));
				} catch (Exception handleException) {
					// handleException.printStackTrace();
				}
			}
		}
	}

	public class EventThreadFactory implements ThreadFactory {
		public Thread newThread(final Runnable r) {
			return new Thread(r, threadNameBase);
		}
	}
	public class EventRejectedExecutionHandler implements RejectedExecutionHandler {
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
			System.out.println("rejected execution: " + r);
		}
	}
}