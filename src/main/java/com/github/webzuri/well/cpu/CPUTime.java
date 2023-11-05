package com.github.webzuri.well.cpu;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

public final class CPUTime
{
	private CPUTime()
	{
		throw new AssertionError();
	}

	public static long getCpuTimeNano()
	{
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadCpuTime() : 0L;
	}

	public static long getUserTimeNano()
	{
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? bean.getCurrentThreadUserTime() : 0L;
	}

	public static long getSystemTimeNano()
	{
		ThreadMXBean bean = ManagementFactory.getThreadMXBean();
		return bean.isCurrentThreadCpuTimeSupported() ? (bean.getCurrentThreadCpuTime() - bean.getCurrentThreadUserTime()) : 0L;
	}
}