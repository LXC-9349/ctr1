package com.ctr.crm.commons.utils.file;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.springframework.web.multipart.MultipartFile;

import com.superaicloud.fileserver.utils.UploadFileUtils;

/**
 * 说明：
 * @author eric
 * @date 2019年5月7日 下午6:24:24
 */
public class FileCommonUtils {
	
	public static final int original = 0;
	public static final int size100x125 = 1;
	public static final int size500x600 = 2;

	/**
	 * 获取文件扩展名
	 * @param filename
	 * @return
	 */
	public static String getExtensionName(String filename) {   
        if ((filename != null) && (filename.length() > 0)) {   
            int dot = filename.lastIndexOf('.');   
            if ((dot >-1) && (dot < (filename.length() - 1))) {   
                return filename.substring(dot + 1);   
            }   
        }   
        return filename;
    }
	
	public static boolean checkImage(InputStream is){
		try {
			BufferedImage image = ImageIO.read(is);
			return image != null;
		} catch (IOException e) {
			return false;
		}
	}
	
	/**
	 * 上传文件
	 * @param file
	 * @param id 客户ID或员工ID
	 * @param ml 大小规则 0:保留原始的  1:100x125 2:500x600
	 * @return
	 */
	public static UploadInfo upload(MultipartFile file, Long id, int ml){
		UploadInfo info = new UploadInfo();
		if (file == null)
            return null;
        String fileName = file.getOriginalFilename();
        String ext = FileCommonUtils.getExtensionName(fileName);
        String upLoadFileName = null;
        boolean image = false;
        try {
			image = checkImage(file.getInputStream());
		} catch (IOException e1) {
		}
        try {
        	if(ml == original){
        		upLoadFileName = UploadFileUtils.upload(file.getBytes(), ext, id, "0");
        	}else if(image && ml == size100x125){
        		byte[] src = ImageResizerUtils.resizerTo100x125(file.getInputStream(), ext);
        		if(src != null)
        		upLoadFileName = UploadFileUtils.upload(src, ext, id, "0");
        	}else if(image && ml == size500x600){
        		byte[] src = ImageResizerUtils.resizerTo500x600(file.getInputStream(), ext);
        		if(src != null)
        		upLoadFileName = UploadFileUtils.upload(src, ext, id, "0");
        	}
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (upLoadFileName == null) return null;
        info.setUploadFileName(upLoadFileName);
        info.setFileName(fileName);
        info.setExt(ext);
		return info;
	}
	
	public static class UploadInfo{
		private String fileName;
		private String uploadFileName;
		private String ext;
		public String getFileName() {
			return fileName;
		}
		public void setFileName(String fileName) {
			this.fileName = fileName;
		}
		public String getUploadFileName() {
			return uploadFileName;
		}
		public void setUploadFileName(String uploadFileName) {
			this.uploadFileName = uploadFileName;
		}
		public String getExt() {
			return ext;
		}
		public void setExt(String ext) {
			this.ext = ext;
		}
	}
	
}
