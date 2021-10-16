package org.ashu.validation.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;

public class ResourceUtils {

	public static String readFileToString(String fileName, String folderPath) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource(folderPath + "/" + fileName);
		return asString(resource);
	}

	public static String asString(Resource resource) {
		try {
			Reader reader = new InputStreamReader(resource.getInputStream());
			return FileCopyUtils.copyToString(reader);
		} catch (IOException e) {
			throw new IllegalArgumentException("Cannot read resource" + resource + "because of " + e.getMessage());
		}
	}
}
