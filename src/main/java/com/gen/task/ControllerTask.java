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

@SuppressWarnings("serial")
public class ControllerTask extends BaseTask {

    public ControllerTask(String className) {
        super(className);
    }

    @Override
    public void run() throws IOException, TemplateException {
        // 生成Controller填充数据
        System.out.println("Generating " + className + "Controller.java");
        Map<String, String> controllerData = new HashMap<>();
        controllerData.put("BasePackageName", ConfigUtil.configuration.getPackageName());
        controllerData.put("ControllerPackageName", ConfigUtil.configuration.getPath().getController()+"."+className.toLowerCase());
        controllerData.put("ServicePackageName", "moduls."+className.toLowerCase()+"."+ConfigUtil.configuration.getPath().getService());
        controllerData.put("EntityPackageName", "moduls."+className.toLowerCase()+"."+ConfigUtil.configuration.getPath().getEntity());
        controllerData.put("Author", ConfigUtil.configuration.getAuthor());
        controllerData.put("Date", new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        controllerData.put("ClassName", className);
        controllerData.put("EntityName", StringUtil.firstToLowerCase(className));
        controllerData.put("LowerEntityName", className.toLowerCase());
        String filePath = FileUtil.getSourcePath() + StringUtil.package2Path(ConfigUtil.configuration.getPackageName()) + StringUtil.package2Path(ConfigUtil.configuration.getPath().getController()+"."+className.toLowerCase());
        String fileName = className + "Controller.java";
        // 生成Controller文件
        FileUtil.generateToJava(FreemarketConfigUtils.TYPE_CONTROLLER, controllerData, filePath + fileName,filePath);
    }
}
