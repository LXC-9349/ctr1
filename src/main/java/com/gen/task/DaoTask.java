package com.gen.task;

import com.gen.task.base.BaseTask;
import com.gen.utils.ConfigUtil;
import com.gen.utils.FileUtil;
import com.gen.utils.FreemarketConfigUtils;
import com.gen.utils.StringUtil;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class DaoTask extends BaseTask {

    /**
	 * 
	 */
	private static final long serialVersionUID = -3125439552781924751L;

	public DaoTask(String className) {
        super(className);
    }

    @Override
    public void run() throws IOException, TemplateException {
        // 生成Dao填充数据
        System.out.println("Generating " + className + "Dao.java");
        Map<String, String> daoData = new HashMap<>();
        daoData.put("BasePackageName", ConfigUtil.configuration.getPackageName());
        daoData.put("DaoPackageName", "moduls."+className.toLowerCase()+"."+ConfigUtil.configuration.getPath().getDao());
        daoData.put("EntityPackageName", "moduls."+className.toLowerCase()+"."+ConfigUtil.configuration.getPath().getEntity());
        daoData.put("Author", ConfigUtil.configuration.getAuthor());
        daoData.put("Date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        daoData.put("ClassName", className);
        daoData.put("EntityName", StringUtil.firstToLowerCase(className));
        String filePath = FileUtil.getSourcePath() + StringUtil.package2Path(ConfigUtil.configuration.getPackageName()) + StringUtil.package2Path(daoData.get("DaoPackageName"));
        String fileName = className + "Dao.java";
        // 生成dao文件
        FileUtil.generateToJava(FreemarketConfigUtils.TYPE_DAO, daoData, filePath + fileName,filePath);
    }
}
