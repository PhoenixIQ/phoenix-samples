package com.iquantex.account.utils;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 速率限制器
 *
 * @author gchaoxue
 * @date 2020/3/11 14:27
 **/
@Slf4j
public class RateLimiter {

	/** 1e9纳秒（1秒） */
	private static final long ONE_SEC_NANO = (long) 1e9;

	/** 1e3毫秒（1秒） */
	private static final long ONE_SEC_MILLIS = (long) 1e3;

	/** 名称 */
	private String name;

	/** 工作线程 */
	private Runnable workload;

	/** 开始 */
	private volatile boolean started;

	/** 速率 */
	private long rate;

	/** 总数 */
	private long total;

	/** 工作时间 */
	private long timeout;

	/** 监控速率限制器是否在工作 */
	private RunMonitor runMonitor;

	/**
	 * 用来判断速率显示器是否工作
	 */
	@Getter
	@Setter
	@AllArgsConstructor
	public static class RunMonitor {

		boolean run;

	}

	/**
	 * @param rate 速率
	 * @param total 总数
	 * @param timeout 请求时间
	 * @param workload 工作线程
	 * @param name 名称
	 * @param monitor 监控
	 */
	public RateLimiter(long rate, long total, long timeout, Runnable workload, String name, RunMonitor monitor) {
		this.rate = rate;
		this.total = total;
		this.timeout = timeout * ONE_SEC_MILLIS;
		this.workload = workload;
		this.name = name;
		this.runMonitor = monitor;
	}

	/**
	 * @param rate 速率
	 * @param total 总数
	 * @param timeout 请求时间
	 * @param workload 工作线程
	 */
	public RateLimiter(long rate, long total, long timeout, Runnable workload) {
		this(rate, total, timeout, workload, "unnamed work load", null);
	}

	/**
	 * 停止
	 */
	public void stop() {
		started = false;
		setMonitorStop();
	}

	/**
	 * 启动速率限制器
	 * @return 是否启动成功
	 */
	public boolean start() {
		if (rate <= 0) {
			log.error("tps value cannot be less than 1");
			return false;
		}
		// start working thread
		// workload
		Thread workThread = new Thread(() -> {
			long rateControlTimeGap = ONE_SEC_NANO;
			long rateControlCounter = 0;
			long rateControlCountPerGap = rate;
			long rateControlDelayTimer;
			long rateControlDelay = rateControlTimeGap / rateControlCountPerGap * 4 / 5;
			long rateControlTimer = System.nanoTime();
			long timeoutTimer = System.currentTimeMillis();
			long totalCounter = 0;

			started = true;
			// 设置监控器为运行状态
			setMonitorRun();

			while (started && System.currentTimeMillis() - timeoutTimer < timeout && totalCounter < total) {
				if (System.nanoTime() - rateControlTimer < rateControlTimeGap) {
					// 如果为完成一个时间间隔内的发送数量，则继续发送
					if (rateControlCounter < rateControlCountPerGap) {
						// workload
						workload.run();

						rateControlCounter++;
						totalCounter++;
						rateControlDelayTimer = System.nanoTime();
						// 设置延迟
						while (System.nanoTime() - rateControlDelayTimer < rateControlDelay)
							;
					}
					else {
						// 完成一个时间间隔内的发送量，则进入下个一个时间间隔（1s）
						rateControlTimer = rateControlTimer + rateControlTimeGap;
						rateControlCounter = 0;
						// 这个时间间隔完成了发送，则消耗这个时间间隔（1s）的时间
						while (System.nanoTime() < rateControlTimer)
							;
					}
				}
				else {
					// 进入下个时间间隔（1s）
					rateControlTimer = rateControlTimer + rateControlTimeGap;
					rateControlCounter = 0;
				}
			}
			if (started) {
				log.info("rate limiter thread stop: name<{}> timeout: {}ms", name, timeout);
			}
			else {
				log.info("rate limiter thread stop: name<{}> interrupted", name);
			}
			this.started = false;
			setMonitorStop();
		});

		workThread.start();
		log.info("rate limiter thread started: name<{}>", name);
		return true;
	}

	/**
	 * 测试
	 */
	public static void main(String[] args) {
		RateLimiter limiter = new RateLimiter(1, 10, 10, () -> log.info("hello"), "test", null);
		limiter.start();
	}

	/**
	 * 设置监视器状态为运行
	 */
	private void setMonitorRun() {
		if (runMonitor != null) {
			runMonitor.setRun(true);
		}
	}

	/**
	 * 设置监视器状态为停止
	 */
	private void setMonitorStop() {
		if (runMonitor != null) {
			runMonitor.setRun(false);
		}
	}

}
