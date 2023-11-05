package com.github.webzuri.well.proxy.handler;

@FunctionalInterface
public interface MethodCallHandler
{
	Object invoke(Object proxy, Object[] args) throws Throwable;
}
