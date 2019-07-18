package com.gen;

import com.gen.invoker.SingleInvoker;
import com.gen.invoker.base.Invoker;

/**
 * 用于生成代码类入口
 *
 * @author DoubleLi
 * @time 2019年1月24日
 */
public class Exec {
    /**
     * JanTest需要对应修改
     *
     * @param args
     * @time 2019年1月24日
     * @author DoubleLi
     */
    public static void main(String[] args) {
        Invoker invoker = new SingleInvoker.Builder()
                .setTableName("")
                .setClassName("")
                .build();
        invoker.execute();
    }
}