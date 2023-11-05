package com.github.webzuri.well.cpu;

import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

public class CPUTimeBenchmark
{
	public Duration sysTime  = Duration.ZERO;
	public Duration usrTime  = Duration.ZERO;
	public Duration cpuTime  = Duration.ZERO;
	public Duration realTime = Duration.ZERO;

	private long start_sysTime = -1;
	private long start_usrTime = -1;
	private long start_cpuTime = -1;
	private long start_real    = -1;

	public static enum TIME
	{
		USR, SYS, REAL, CPU;
	}

	public CPUTimeBenchmark()
	{
	}

	public CPUTimeBenchmark(CPUTimeBenchmark copy)
	{
		copy(copy);
	}

	public static void stopStartChrono(CPUTimeBenchmark stop, CPUTimeBenchmark start)
	{
		stopStartChrono(Collections.singleton(stop), Collections.singleton(start));
	}

	public static void startStopChrono(CPUTimeBenchmark start, CPUTimeBenchmark stop)
	{
		startStopChrono(Collections.singleton(start), Collections.singleton(stop));
	}

	public static void stopStartChrono(Collection<? extends CPUTimeBenchmark> stop, Collection<? extends CPUTimeBenchmark> start)
	{
		startStopChrono(start, stop);
	}

	public static void startStopChrono(Collection<? extends CPUTimeBenchmark> start, Collection<? extends CPUTimeBenchmark> stop)
	{
		var start_real    = System.nanoTime();
		var start_cpuTime = CPUTime.getCpuTimeNano();
		var start_sysTime = CPUTime.getSystemTimeNano();
		var start_usrTime = CPUTime.getUserTimeNano();

		for (var t : start)
		{
			t.start_real    = start_real;
			t.start_cpuTime = start_cpuTime;
			t.start_sysTime = start_sysTime;
			t.start_usrTime = start_usrTime;
		}
		for (var t : stop)
		{
			t.cpuTime  = t.cpuTime.plusNanos(start_cpuTime - t.start_cpuTime);
			t.sysTime  = t.sysTime.plusNanos(start_sysTime - t.start_sysTime);
			t.usrTime  = t.usrTime.plusNanos(start_usrTime - t.start_usrTime);
			t.realTime = t.realTime.plusNanos(start_real - t.start_real);
		}
	}

	public void startChrono()
	{
		start_real    = System.nanoTime();
		start_cpuTime = CPUTime.getCpuTimeNano();
		start_sysTime = CPUTime.getSystemTimeNano();
		start_usrTime = CPUTime.getUserTimeNano();
	}

	public void startChrono(CPUTimeBenchmark src)
	{
		start_cpuTime = src.start_cpuTime;
		start_real    = src.start_real;
		start_usrTime = src.start_usrTime;
		start_sysTime = src.start_sysTime;
	}

	public static void startChrono(CPUTimeBenchmark... times)
	{
		startChrono(List.of(times));
	}

	public static void startChrono(Collection<? extends CPUTimeBenchmark> times)
	{
		var start_real    = System.nanoTime();
		var start_cpuTime = CPUTime.getCpuTimeNano();
		var start_sysTime = CPUTime.getSystemTimeNano();
		var start_usrTime = CPUTime.getUserTimeNano();

		for (var t : times)
		{
			t.start_real    = start_real;
			t.start_cpuTime = start_cpuTime;
			t.start_sysTime = start_sysTime;
			t.start_usrTime = start_usrTime;
		}
	}

	public void stopChrono()
	{
		final long cpu  = CPUTime.getCpuTimeNano();
		final long sys  = CPUTime.getSystemTimeNano();
		final long usr  = CPUTime.getUserTimeNano();
		final long real = System.nanoTime();
		cpuTime  = cpuTime.plusNanos(cpu - start_cpuTime);
		sysTime  = sysTime.plusNanos(sys - start_sysTime);
		usrTime  = usrTime.plusNanos(usr - start_usrTime);
		realTime = realTime.plusNanos(real - start_real);
	}

	public static void stopChrono(CPUTimeBenchmark... times)
	{
		stopChrono(List.of(times));
	}

	public static void stopChrono(Collection<? extends CPUTimeBenchmark> times)
	{
		var cpu  = CPUTime.getCpuTimeNano();
		var sys  = CPUTime.getSystemTimeNano();
		var usr  = CPUTime.getUserTimeNano();
		var real = System.nanoTime();

		for (var t : times)
		{
			t.cpuTime  = t.cpuTime.plusNanos(cpu - t.start_cpuTime);
			t.sysTime  = t.sysTime.plusNanos(sys - t.start_sysTime);
			t.usrTime  = t.usrTime.plusNanos(usr - t.start_usrTime);
			t.realTime = t.realTime.plusNanos(real - t.start_real);
		}
	}

	public void plus(Duration duration, EnumSet<TIME> times)
	{
		if (times.contains(TIME.USR))
			usrTime = usrTime.plus(duration);
		if (times.contains(TIME.SYS))
			sysTime = sysTime.plus(duration);
		if (times.contains(TIME.REAL))
			realTime = realTime.plus(duration);
		if (times.contains(TIME.CPU))
			cpuTime = cpuTime.plus(duration);
	}

	public static CPUTimeBenchmark plus(CPUTimeBenchmark a, Duration duration, EnumSet<TIME> times)
	{
		var ret = a.clone();
		ret.plus(duration, times);
		return ret;
	}

	public void plus(CPUTimeBenchmark time)
	{
		cpuTime  = cpuTime.plus(time.cpuTime);
		usrTime  = usrTime.plus(time.usrTime);
		sysTime  = sysTime.plus(time.sysTime);
		realTime = realTime.plus(time.realTime);
	}

	public static CPUTimeBenchmark plus(CPUTimeBenchmark a, CPUTimeBenchmark b)
	{
		var ret = a.clone();
		ret.plus(b);
		return ret;
	}

	public void minus(CPUTimeBenchmark time)
	{
		cpuTime  = cpuTime.minus(time.cpuTime);
		usrTime  = usrTime.minus(time.usrTime);
		sysTime  = sysTime.minus(time.sysTime);
		realTime = realTime.minus(time.realTime);
	}

	public static CPUTimeBenchmark minus(CPUTimeBenchmark a, CPUTimeBenchmark b)
	{
		var ret = a.clone();
		ret.minus(b);
		return ret;
	}

	public void copy(CPUTimeBenchmark src)
	{
		sysTime  = src.sysTime;
		usrTime  = src.usrTime;
		cpuTime  = src.cpuTime;
		realTime = src.realTime;
		startChrono(src);
	}

	@Override
	public String toString()
	{
		return new StringBuilder() //
			.append("r").append(realTime.toMillis()) //
			.append("u").append(usrTime.toMillis()) //
			.append("s").append(sysTime.toMillis()) //
			.append("c").append(cpuTime.toMillis()) //
			.toString();
	}

	@Override
	public CPUTimeBenchmark clone()
	{
		return new CPUTimeBenchmark(this);
	}
}
