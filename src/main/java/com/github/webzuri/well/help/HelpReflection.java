package com.github.webzuri.well.help;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public final class HelpReflection
{
	private HelpReflection()
	{
		throw new AssertionError();
	}

	public static Stream<Class<?>> getPackageClasses(String packageName)
	{
		return getPackageClasses(packageName, 0);
	}

	public static Stream<String> getPackageItems(String packageName, int maxDepth)
	{
		return getPackageItems(packageName, maxDepth, ClassLoader.getSystemClassLoader());
	}

	public static Stream<String> getPackageItems(String packageName, int maxDepth, ClassLoader loader)
	{
		try
		{
			var packageName_    = packageName.replace('.', '/');
			var url             = loader.getResource(packageName_);
			var packagePath     = Paths.get(url.toURI());
			var packageStartPos = packagePath.toString().indexOf(packageName_) + packageName_.length() + 1;
			return getPackageItems_(packagePath, packageName, packageStartPos, maxDepth);
		}
		catch (URISyntaxException e)
		{
			throw new AssertionError(e);
		}
	}

	private static Stream<String> getPackageItems_(Path packagePath, String packageName, int packageStartPos, int maxDepth)
	{
		try
		{
			if (!Files.isDirectory(packagePath))
				return Stream.empty();

			@SuppressWarnings("resource")
			var reader = Files.list(packagePath);

			return reader.flatMap(path -> //
			{
				var className = path.toString().substring(packageStartPos).replace('/', '.');

				if (maxDepth == 0 || path.endsWith(".class"))
					return Stream.of(className);

				int ndepth     = maxDepth > 0 ? maxDepth - 1 : -1;
				var subPackage = packageName + "." + path.getName(path.getNameCount() - 1).toString();
				var subStream  = getPackageItems_(path, subPackage, packageStartPos, ndepth); // .map(cls -> Path.of(packageName + "/" + cls.getName(cls.getNameCount() - 1))) //;
				return Stream.concat(Stream.of(className), subStream);
			});
		}
		catch (IOException e)
		{
			throw new AssertionError(e);
		}
	}

	public static Stream<Class<?>> getPackageClasses(String packageName, int maxDepth)
	{
		var loader = ClassLoader.getSystemClassLoader();
		return getPackageItems(packageName, maxDepth).filter(n -> n.endsWith(".class")).map(c -> {
			c = StringUtils.removeEnd(c, ".class");
			try
			{
				return loader.loadClass(packageName + "." + c);
			}
			catch (ClassNotFoundException e)
			{
				throw new RuntimeException(e);
			}
		});
	}

	public static List<Class<?>> extendsOrImplements(Class<?> c)
	{
		var ret       = new ArrayList<Class<?>>();
		var toprocess = new LinkedList<Class<?>>();

		var sc = c.getSuperclass();
		if (sc != null)
			toprocess.add(sc);
		toprocess.addAll(List.of(c.getInterfaces()));

		while (!toprocess.isEmpty())
		{
			var current = toprocess.pop();
			ret.add(current);

			sc = current.getSuperclass();

			if (sc != null)
				toprocess.add(sc);
			toprocess.addAll(List.of(current.getInterfaces()));
		}
		return ret;
	}

	public static Predicate<Class<?>> checkClasses(Collection<Class<?>> classes, Function<Class<?>, Collection<Class<?>>> getClasses)
	{
		return (c) -> {
			var cclasses = new HashSet<>(getClasses.apply(c));

			for (var cc : classes)
			{
				if (!cclasses.remove(cc))
					return false;
			}
			return true;
		};
	}

	public static Predicate<Class<?>> implementsDirectly(Collection<Class<?>> interfaces)
	{
		return checkClasses(interfaces, c -> List.of(c.getInterfaces()));
	}

	public static Predicate<Class<?>> extendsOrImplements(Collection<Class<?>> interfaces)
	{
		return checkClasses(interfaces, c -> extendsOrImplements(c));
	}
}
