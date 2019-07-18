package com.gen.invoker.base;

public abstract class BaseBuilder {

    public abstract BaseInvoker build();

    public boolean isParamtersValid() {
        try {
            checkBeforeBuild();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public abstract void checkBeforeBuild() throws Exception;

}
