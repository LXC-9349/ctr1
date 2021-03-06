package com.ctr.crm.commons.result;

/**
 * 响应状态
 *
 * @author Administrator
 */
public enum ResponseStatus {

    /**
     * 请求成功
     */
    success(100000) {
        @Override
        public String getStatusDesc() {
            return "请求成功";
        }
    },
    /**
     * 请求失败
     */
    failed(100001) {
        @Override
        public String getStatusDesc() {
            return "请求失败";
        }
    },
    /**
     * 请求异常
     */
    error(100002) {
        @Override
        public String getStatusDesc() {
            return "请求异常";
        }
    },
    /**
     * 参数为空
     */
    null_param(100003) {
        @Override
        public String getStatusDesc() {
            return "参数为空";
        }
    },
    /**
     * 未登录
     */
    no_login(100004) {
        @Override
        public String getStatusDesc() {
            return "未登录";
        }
    },
    /**
     * 未设置可显字段
     */
    no_explicit_field(100005) {
        @Override
        public String getStatusDesc() {
            return "未设置可显字段";
        }
    },
    /**
     * 无权限
     */
    no_access(100006) {
        @Override
        public String getStatusDesc() {
            return "无权限";
        }
    },
    /**
     * 客户信息不存在
     */
    non_existent(100007) {
        @Override
        public String getStatusDesc() {
            return "客户信息不存在";
        }
    },
    /**
     * 非法请求
     */
    illegal_request(100008) {
        @Override
        public String getStatusDesc() {
            return "非法请求";
        }
    },
    /**
     * 参数不完整
     */
    imperfect(100009) {
        @Override
        public String getStatusDesc() {
            return "参数不完整";
        }
    },
    /**
     * 手机号已存在
     */
    exists_phone(100010) {
        @Override
        public String getStatusDesc() {
            return "手机号已存在";
        }
    },
    /**
     * 错误的文件格式
     */
    file_format_incorrect(100011) {
        @Override
        public String getStatusDesc() {
            return "文件格式有误";
        }
    },
    /**
     * 文件最大大小
     */
    file_max_size(100012) {
        @Override
        public String getStatusDesc() {
            return "超出最大文件大小";
        }
    },
    /**
     * 数据重复
     */
    duplicate(100013) {
        @Override
        public String getStatusDesc() {
            return "数据重复";
        }
    },
    /**
     * 重置密码成功
     */
    reset_passwords_success(100014) {
        @Override
        public String getStatusDesc() {
            return "重置密码成功";
        }
    },
    /**
     * 员工工号不存在
     */
    non_workerId(100015) {
        @Override
        public String getStatusDesc() {
            return "员工工号不存在";
        }
    },
    /**
     * 数据被引用,无法删除
     */
    data_referenced(100016) {
        @Override
        public String getStatusDesc() {
            return "数据被引用,无法删除";
        }
    },
    /**
     * 修改密码失败
     */
    modify_passwords_defeat(100017) {
        @Override
        public String getStatusDesc() {
            return "修改密码失败";
        }
    },
    /**
     * 密码错误
     */
    wrong_passwords(100018) {
        @Override
        public String getStatusDesc() {
            return "密码错误";
        }
    },
    /**
     * 没有短信相关的配置信息
     */
    smsconfigure_null(100020) {
        @Override
        public String getStatusDesc() {
            return "短信配置为空";
        }
    },
    /**
     * 公司名称或账号重复
     */
    duplicate_company_name(100021) {
        @Override
        public String getStatusDesc() {
            return "公司名称或账号重复";
        }
    },

    /**
     * 分机号重复
     */
    extension_repeat(100022) {
        @Override
        public String getStatusDesc() {
            return "分机号重复";
        }
    },
    /**
     * 渠道为空
     */
    null_source(100023) {
        @Override
        public String getStatusDesc() {
            return "渠道为空";
        }
    },
    /**
     * 帐号不存在
     */
    non_company(100024) {
        @Override
        public String getStatusDesc() {
            return "帐号不存在";
        }
    },
    /**
     * 鉴权失败
     */
    auth_failed(100025) {
        @Override
        public String getStatusDesc() {
            return "鉴权认证失败";
        }
    },
    /**
     * 非法参数
     */
    illegal_parameter(100026) {
        @Override
        public String getStatusDesc() {
            return "非法参数";
        }
    },
    /**
     * 分配失败
     */
    allot_failed(100027) {
        @Override
        public String getStatusDesc() {
            return "分配失败";
        }
    },
    /**
     * 转移失败
     */
    transfer_failed(100028) {
        @Override
        public String getStatusDesc() {
            return "已有归属人不能转才俊佳丽";
        }
    },
    /**
     * 产品名称为空
     */
    orderproductname_null(100029) {
        @Override
        public String getStatusDesc() {
            return "产品名称不能为空";
        }
    },
    /**
     * 转移失败
     */
    transfer_classType(100030) {
        @Override
        public String getStatusDesc() {
            return "转移失败!请设置才俊佳丽意向度";
        }
    },
    /**
     * 数据重复
     */
    configDuplicate(100031) {
        @Override
        public String getStatusDesc() {
            return "公众号名称重复";
        }
    },
    /**
     * 数据重复
     */
    configDuplicateAPPID(100032) {
        @Override
        public String getStatusDesc() {
            return "公众号APPID重复";
        }
    },
    /**
     * 进入审批
     */
    approval(100030) {
        @Override
        public String getStatusDesc() {
            return "当前操作需要审批通过后才能执行，请在审批管理中查看审批进度！";
        }
    };
    private Integer statusCode;

    private ResponseStatus(Integer statusCode) {
        this.setStatusCode(statusCode);
    }

    public abstract String getStatusDesc();

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}
