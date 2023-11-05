package com.github.webzuri.well.filter;

import java.util.function.Predicate;

public interface IPrefixStringFilter extends Predicate<String>
{
	public boolean isEmpty();

	public void addPrefix(String s);

	public void addPrefix(String s, String... more);

	public void addPrefix(Iterable<String> slist);

	@Override
	public boolean test(String s);

	public int size();

	public int prefixSize();

}
