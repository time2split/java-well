package com.github.webzuri.well.help;

import java.util.Objects;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

public final class HelpFile
{
	private HelpFile()
	{
		throw new AssertionError();
	}

	public static String ensureExtension(String fileName, String extension, int allowToDeleteExtensionDepth)
	{
		assert (allowToDeleteExtensionDepth >= 0);
		var extensionDepth = StringUtils.countMatches(extension, '.') + 1;
		var ext            = fileName.split("\\.");

		// Check the file extension
		if (extensionDepth < ext.length)
		{
			var fileExtension = StringUtils.join(ArrayUtils.subarray(ext, ext.length - extensionDepth, ext.length), '.');

			// Good file extension
			if (Objects.equals(extension, fileExtension))
				return fileName;
		}

		// Bad file extension

		if (ext.length > allowToDeleteExtensionDepth)
			fileName = StringUtils.join(ArrayUtils.subarray(ext, 0, ext.length - allowToDeleteExtensionDepth), '.');

		return new StringBuilder().append(fileName).append('.').append(extension).toString();
	}
}
