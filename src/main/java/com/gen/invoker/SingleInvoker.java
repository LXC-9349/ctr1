package com.gen.invoker;

import com.gen.invoker.base.BaseBuilder;
import com.gen.invoker.base.BaseInvoker;
import com.gen.task.ControllerTask;
import com.gen.task.DaoTask;
import com.gen.task.EntityTask;
import com.gen.task.ServiceTask;
import com.gen.utils.GeneratorUtil;
import com.gen.utils.StringUtil;

import java.sql.SQLException;

public class SingleInvoker extends BaseInvoker {

    @Override
    protected void getTableInfos() throws SQLException {
        tableInfos = connectionUtil.getMetaData(tableName);
    }

    @Override
    protected void initTasks() {
        taskQueue.add(new DaoTask(className));
        taskQueue.add(new ServiceTask(className,tableInfos));
        taskQueue.add(new ControllerTask(className));
        taskQueue.add(new EntityTask(className, tableInfos));
    }

    public static class Builder extends BaseBuilder {

		private SingleInvoker invoker = new SingleInvoker();

        public Builder setTableName(String tableName) {
            invoker.setTableName(tableName);
            return this;
        }

        public Builder setClassName(String className) {
            invoker.setClassName(className);
            return this;
        }

        @Override
        public BaseInvoker build(){
            if (!isParamtersValid()) {
                return null;
            }
            return invoker;
        }

        @Override
        public void checkBeforeBuild() throws Exception {
            if (StringUtil.isBlank(invoker.getTableName())) {
                throw new Exception("Expect table's name, but get a blank String.");
            }
            if (StringUtil.isBlank(invoker.getClassName())) {
                invoker.setClassName(GeneratorUtil.generateClassName(invoker.getTableName()));
            }
        }
    }

}
