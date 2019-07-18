package com.ctr.crm.commons;

public class Constants {

    /**************** 人事相关 ↓↓↓↓↓↓↓↓↓↓↓ *****************/
    // 是否是系统预设值
    public final static int PRESET_TRUE = 0;
    public final static int PRESET_FALSE = 1;

    // 是否删除 1：删除；0：未删除
    public final static int DELETED_TRUE = 1;
    public final static int DELETED_FALSE = 0;

    // 是否禁用 1:禁用，0：启用
    public final static int DISABLED_TRUE = 1;
    public final static int DISABLED_FALSE = 0;

    public final static int BASE_FUNCTION = 0;
    public final static int ADVANCE_FUNCTION = 1;

    // 部门ID在structure中占的长度
    public final static int DEPT_STRUCTURE_LENGTH = 10;

    public final static String DEFAULT_COMPANY_NAME = "超脑云信息技术";
    public final static String DEFAULT_DEPT_NAME = "超脑云信息技术";

    // 默认用户名
    public final static String DEFAULT_WORKER_NAME = "系统管理员";
    // 默认用户工号
    public final static Integer DEFAULT_WORKER_ID = 8888;
    // 默认密码
    public final static String DEFAULT_WORKER_PASSWORD = "admin123";

    // 总监
    public final static int DIRECTOR = 15;
    // 经理
    public final static int MANAGER = 10;
    // 主管
    public final static int LEADER = 5;
    // 普通员工
    public final static int WORKER = 0;

    /**************** 人事相关 ↑↑↑↑↑↑↑↑↑↑↑ *****************/

    public final static String CACHE_NAMESPACE_WORKER_OF_ROLE = "WorkerOfRole_R2";
    public final static String CACHE_NAMESPACE_MENU_OF_ROLE = "MenuOfRole_R2";
    public final static String CACHE_NAMESPACE_ACTION_OF_ROLE = "ActionOfRole_R2";
    public final static String CACHE_NAMESPACE_ACTIONOFROLE_LIST = "ActionOfRoleList_R2_";

    // =============客户类型相关常量===========///////////
    /**
     * 客户类型位数
     */
    public final static int MEMBER_TYPE_LENGTH = 16;
    public final static char NO = '0';
    public final static char YES = '1';
    /**
     * 不在公海标识位
     */
    public final static int member_type_seas = 1;
    /**
     * 是否分配过标识位
     */
    public final static int member_type_alloted = 2;
    /**
     * 是否黑名单标识位
     */
    public final static int member_type_rubbish = 3;
    /**
     * 是否成单标识位
     */
    public final static int member_type_vip = 4;
    /**
     * 是否在库标识位
     */
    public final static int member_type_insalecase = 5;
    /**
     * 是否在回收站
     */
    public final static int member_type_isrecycling = 6;
    /**
     * 才俊佳丽
     */
    public final static int member_type_ishandsome = 7;
    // ===================================///////////

    public static final long max_file_size = 10 * 1024 * 1024;

    // =============消息功能相关常量===========////////////

    /**
     * 名称空间
     *
     * @author Administrator
     */
    public enum NameSpace {
        /**
         * 提醒、IM、Wechat
         */
        notice("/crm");

        private String namespace;

        private NameSpace(String namespace) {
            this.setNamespace(namespace);
        }

        public String getNamespace() {
            return namespace;
        }

        public void setNamespace(String namespace) {
            this.namespace = namespace;
        }
    }

    /**
     * 事件名称
     *
     * @author Administrator
     */
    public enum EventName {
        status, message, notice, im;
    }

    public static final String CACHE_SIO_WORKER = "worker";
    public static final String CACHE_SIO_TIME = "time";
    // ===================================///////////

    // ==============缓存相关常量=============//////////
    public static final int exp_24hours = 60 * 60 * 24;
    public static final int exp_1hours = 60 * 60 * 1;
    public static final int exp_12hours = 60 * 60 * 12;
    public static final int exp_10hours = 60 * 60 * 10;
    public static final int exp_10minutes = 60 * 10;
    public static final int exp_30minutes = 60 * 30;
    public static final String Worker_Key = "O_W_";
    public static final String WorkerLog_Key = "O_W_L_";
    public static final String Company_Key = "O_C_";
    public static final String Dept_Key = "O_D_";
    // ===================================///////////

    /**
     * 监听
     */
    public static final int listen = 1;
    /**
     * 耳语
     */
    public static final int whisper = 2;
    /**
     * 取消耳语
     */
    public static final int whisper_cancel = 3;
    /**
     * 三方通话
     */
    public static final int three_party_call = 4;

    private Constants() {
        // 不可以被初始化的构造函数
    }

    /***
     *
     */
    /*=======审批类型 begin=================*/
    /**
     * 合同
     */
    public static final String APPROVAL_CONTRACT = "contract";
    public static final String APPROVAL_MEET = "meet";
    /*===========end=================*/
}
