package com.github.webzuri.well.proxy.handler;

import java.lang.reflect.Method;
import java.util.Map;

public final class MethodCallInterceptors
{
	public MethodCallInterceptors()
	{
		throw new AssertionError();
	}

	@FunctionalInterface
	public static interface Interception
	{
		public Object invoke(Object proxy, Method intercepted, Object[] args) throws Throwable;
	}

	public static Interception noInterception(MethodCallHandler handler)
	{
		return (p, m, a) -> handler.invoke(p, a);
	}

	public static MethodCallInterceptor interceptMethods(Map<Method, Interception> intercept)
	{
		return (proxy, method, args, handler) -> {
			var callHandler = intercept.getOrDefault(method, noInterception(handler));
			return callHandler.invoke(proxy, method, args);
		};
	}
}