package com.github.webzuri.well.proxy.handler;

import java.lang.reflect.Method;

public final class TopMethodInterpreter implements MethodInterpreter
{
	private Object            object;
	private MethodInterpreter interpreter;

	public TopMethodInterpreter(Object object, MethodInterpreter interpreter)
	{
		this.object      = object;
		this.interpreter = interpreter;
	}

	public Object getObject()
	{
		return object;
	}

	@Override
	public MethodCallHandler interpret(Method method)
	{
		return interpreter.interpret(method);
	}
}
