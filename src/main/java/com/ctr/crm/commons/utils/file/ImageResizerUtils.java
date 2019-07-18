package com.ctr.crm.commons.utils.file;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

public class ImageResizerUtils {

	public static byte[] resizerTo100x125(InputStream srcFile, String ext) {
		return resizer(srcFile, ext, 100, 125);
	}

	public static byte[] resizerTo100x125(String srcFilePath, String ext) {
		return resizer(srcFilePath, ext, 100, 125);
	}

	public static byte[] resizerTo500x600(InputStream srcFile, String ext) {
		return resizer(srcFile, ext, 500, 600);
	}

	public static byte[] resizer(String srcFilePath, String ext, int newWidth, int newHeight) {
		try {
			return resizer(new FileInputStream(srcFilePath), ext, newWidth, newHeight);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static byte[] resizer(InputStream srcFile, String ext, int newWidth, int newHeight) {
		try {
			BufferedImage source = ImageIO.read(srcFile);
			ImageResizer resizer = new ImageResizer(source);
			resizer.setTargetSize(newWidth, newHeight);
			resizer.setUpscale(true);
			resizer.setCrop(true);
			resizer.setCropPosition(-1);
			BufferedImage result = resizer.resize();
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			ImageIO.write(result, ext, stream);
			return stream.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
