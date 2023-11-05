package com.github.webzuri.well.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.stream.Stream;

import com.github.webzuri.well.proxy.handler.MethodCallInterceptor;
import com.github.webzuri.well.proxy.handler.MethodInterpreters;

public final class Proxies
{
	public Proxies()
	{
		throw new AssertionError();
	}

	@SuppressWarnings("unchecked")
	public static <T> T simpleProxy(Class<? extends T> iface, InvocationHandler handler, Class<?>... otherIfaces)
	{
		Class<?>[] allInterfaces = Stream.concat( //
			Stream.of(iface), //
			Stream.of(otherIfaces) //
		) //
			.distinct() //
			.toArray(Class<?>[]::new);

		return (T) Proxy.newProxyInstance(iface.getClassLoader(), allInterfaces, handler);
	}

	public static <T> T interceptingProxy(T target, Class<T> iface, MethodCallInterceptor interceptor)
	{
		return simpleProxy(iface, //
			MethodInterpreters.top(target, //
				MethodInterpreters.caching( //
					MethodInterpreters.intercepting( //
						MethodInterpreters.handlingDefaultMethods( //
							MethodInterpreters.binding(target) //
						), interceptor))));
	}

}
