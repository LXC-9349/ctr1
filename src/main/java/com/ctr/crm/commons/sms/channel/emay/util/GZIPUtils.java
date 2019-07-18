package com.ctr.crm.commons.sms.channel.emay.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * GZIP 压缩工具
 * @author Frank
 *
 */
public class GZIPUtils {

	/**
	 * 数据压缩传输
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static void compressTransfe(byte[] bytes, OutputStream out) throws IOException {
		GZIPOutputStream gos = null;
		try {
			gos = new GZIPOutputStream(out);
			gos.write(bytes);
			gos.finish();
			gos.flush();
		} finally{
			if(gos != null){
				gos.close();
			}
		}
	}
	
	/**
	 * 数据压缩
	 * 
	 * @param is
	 * @param os
	 * @throws Exception
	 */
	public static byte[] compress(byte[] bytes) throws IOException {
		ByteArrayOutputStream out = null;
		GZIPOutputStream gos = null;
		try {
			out = new ByteArrayOutputStream();
			gos = new GZIPOutputStream(out);
			gos.write(bytes);
			gos.finish();
			gos.flush();
		} finally{
			if(gos != null){
				gos.close();
			}
			if(out != null){
				out.close();
			}
		}
		return out.toByteArray();
	}
	
	/**
	 * 数据解压
	 * 
	 * @param in
	 * @return
	 * @throws IOException
	 */
	public static byte[] decompress(byte[] bytes) throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(bytes);
		GZIPInputStream gin = new GZIPInputStream(in);
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		int count;
		byte data[] = new byte[1024];
		while ((count = gin.read(data, 0, 1024)) != -1) {
			out.write(data, 0, count);
		}
		out.flush();
		out.close();
		gin.close();
		return out.toByteArray();
	}

}
