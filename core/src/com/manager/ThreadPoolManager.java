package com.manager;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


public class ThreadPoolManager
{
	public ScheduledThreadPoolExecutor _generalScheduledThreadPool;
	public ThreadPoolExecutor _generalThreadPool;	
	private static final long MAX_DELAY = Long.MAX_VALUE / 1000000 / 2;
	private boolean _shutdown;

	private ThreadPoolManager()	{
		_generalScheduledThreadPool = new ScheduledThreadPoolExecutor(2, new PriorityThreadFactory("General ThreadPool", Thread.NORM_PRIORITY+2));
		_generalThreadPool = new ThreadPoolExecutor(2, 2 + 1, 5L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new PriorityThreadFactory("General ThreadPool", Thread.NORM_PRIORITY));
	}

	public static long validateDelay(long delay) {
		if (delay < 0)
			delay = 0;
		else if (delay > MAX_DELAY)
			delay = MAX_DELAY;
		return delay;
	}

	public ScheduledFuture<?> scheduleGeneral(Runnable r, long delay) {
		try	{
			delay = ThreadPoolManager.validateDelay(delay);
			return _generalScheduledThreadPool.schedule(r, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null; /* shutdown, ignore */
		}
	}

	public ScheduledFuture<?> scheduleGeneralAtFixedRate(Runnable r, long initial, long delay) {
		try	{
			delay = ThreadPoolManager.validateDelay(delay);
			initial = ThreadPoolManager.validateDelay(initial);
			return _generalScheduledThreadPool.scheduleAtFixedRate(r, initial, delay, TimeUnit.MILLISECONDS);
		} catch (RejectedExecutionException e) {
			return null; /* shutdown, ignore */
		}
	}

	public void removeRunnable(Runnable r) {
		if (r == null)
			return;
		_generalThreadPool.remove(r);
		_generalScheduledThreadPool.remove(r);
		purge();
	}
	
	public void executeTask(Runnable r)	{
		_generalThreadPool.execute(r);
	}
	
	public static void remove(Runnable r){
		getInstance().removeRunnable(r);
	}
	
	public static void execute(Runnable r){
		getInstance().executeTask(r);
	}
	
	public static ScheduledFuture<?> schedule(Runnable r,long delay){
		return getInstance().scheduleGeneral(r,delay);
	}
	
	public static ScheduledFuture<?> scheduleAtFixedRate(Runnable r,long initial,long delay){
		return getInstance().scheduleGeneralAtFixedRate(r,initial,delay);
	}
	
	private class PriorityThreadFactory implements ThreadFactory {
		private int _prio;
		private String _name;
		private AtomicInteger _threadNumber = new AtomicInteger(1);
		private ThreadGroup _group;

		public PriorityThreadFactory(String name, int prio) {
			_prio = prio;
			_name = name;
			_group = new ThreadGroup(_name);
		}

		@Override
		public Thread newThread(Runnable r)	{
			Thread t = new Thread(_group, r);
			t.setName(_name + "-" + _threadNumber.getAndIncrement());
			t.setPriority(_prio);
			return t;
		}
	}

	public void shutdown() {
		_shutdown = true;
		try	{
			_generalScheduledThreadPool.awaitTermination(1, TimeUnit.SECONDS);
			_generalThreadPool.awaitTermination(1, TimeUnit.SECONDS);
			_generalScheduledThreadPool.shutdown();
			_generalThreadPool.shutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public boolean isShutdown() {
		return _shutdown;
	}

	public void purge()	{
		_generalScheduledThreadPool.purge();
		_generalThreadPool.purge();
	}

	public static ThreadPoolManager getInstance(){
		return SingletonHolder._instance;
	}

	@SuppressWarnings("synthetic-access")
	private static class SingletonHolder {
		protected static final ThreadPoolManager _instance = new ThreadPoolManager();
	}
}