package com.github.webzuri.well.proxy.handler;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public final class MethodInterpreters
{
	public MethodInterpreters()
	{
		throw new AssertionError();
	}

	// ==========================================================================

	public static MethodInterpreter top(Object original, MethodInterpreter interpreter)
	{
		return new TopMethodInterpreter(original, interpreter);
	}

	public static MethodInterpreter caching(MethodInterpreter interpreter)
	{
		Map<Method, MethodCallHandler> cache = new HashMap<>();
		return method -> cache.computeIfAbsent(method, interpreter::interpret);
	}

	public static MethodInterpreter binding(Object target)
	{
		return binding(target, method -> {
			throw new IllegalStateException(String.format("Target class %s does not support method %s", target.getClass(), method));
		});
	}

	public static MethodInterpreter binding(Object target, MethodInterpreter unboundInterpreter)
	{
		return method -> {

			if (method.getDeclaringClass().isAssignableFrom(target.getClass()))
			{
				return (proxy, args) -> method.invoke(target, args);
			}

			return unboundInterpreter.interpret(method);
		};
	}

	public static MethodInterpreter intercepting(MethodInterpreter interpreter, MethodCallInterceptor interceptor)
	{
		return method -> interceptor.intercepting(method, interpreter.interpret(method));
	}

	public static MethodInterpreter handlingDefaultMethods(MethodInterpreter nonDefaultInterpreter)
	{
		return method -> method.isDefault() ? DefaultMethodCallHandler.forMethod(method) : nonDefaultInterpreter.interpret(method);
	}

}
