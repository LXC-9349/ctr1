-- ----------------------------
-- Records of GlobalSetting
-- ----------------------------
INSERT INTO `GlobalSetting`(`id`, `name`, `type`, `value`, `desc`, `createTime`, `deleted`)
VALUES ('3e45abce458841d683f7e7f3cc0ba199', '手机号码加密显示', 'phoneencrypt', '1', '1加密0不加密', '2019-04-19 11:42:22', 0);
INSERT INTO `GlobalSetting`(`id`, `name`, `type`, `value`, `desc`, `createTime`, `deleted`)
VALUES ('9d47c21ae37b4015b6f8da7bd996e0c8', '每人每天允许捞取', 'gain_total', '100', '允许；捞取数量', '2019-04-23 18:15:57', 0);
INSERT INTO `GlobalSetting`(`id`, `name`, `type`, `value`, `desc`, `createTime`, `deleted`)
VALUES ('f4d5d764f49c44b5a18d5d849ee696fe', '录入设置', 'duplicate_off', '1', '1允许重复2不允许重复', '2019-04-23 18:13:06', 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('4eb3f44a703547f590f2cc47ecc8ca0a', 'phone', '接拨电话', 'contact_information', 1, NULL,
        '804a1fae6ff249a590f08f928cff9191', '2019-04-23 10:46:47', 0, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('6ce6dada8e89480596d5213cf9d721e9', 'sms', '发送短信', 'contact_information', 3, NULL,
        '804a1fae6ff249a590f08f928cff9191', '2019-04-23 13:49:27', 0, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('804a1fae6ff249a590f08f928cff9191', '0', '有效联系方式', 'contact_information', 0, NULL, '0', '2019-04-23 10:43:24',
        0, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('bbd2649354be41b6b882c107d58a3c65', 'notes', '跟进记录', 'contact_information', 4, NULL,
        '804a1fae6ff249a590f08f928cff9191', '2019-04-23 13:49:38', 0, 0);

INSERT INTO `SysCirculationConfig`(`id`, `reason`, `deleteMember`, `transferMember`, `quitMember`, `createTime`,
                                   `deleted`)
VALUES ('71add9b5a2244a56b5291269794cbf24', '其它', 1, 1, 1, '2019-04-23 14:58:46', 0);
INSERT INTO `SysCirculationConfig`(`id`, `reason`, `deleteMember`, `transferMember`, `quitMember`, `createTime`,
                                   `deleted`)
VALUES ('7d2bfaaf916447c498761ab2f1c86e1d', '无意向客户', 1, 1, 1, '2019-04-22 11:08:39', 0);
INSERT INTO `SysCirculationConfig`(`id`, `reason`, `deleteMember`, `transferMember`, `quitMember`, `createTime`,
                                   `deleted`)
VALUES ('f8b5c42f8e974d0b873b2b64929fb8fd', '无法联系', 1, 1, 1, '2019-04-21 16:45:30', 0);

# 新增字典项
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('04352c4a8bb14ae5b028e90590d171b6', '6', '短信用户配置', 'log_type', 6, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:49:21', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('263c64df27054adbb07548c28e5342ed', '7', '客户流转原因配置', 'log_type', 7, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:49:44', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('263ed27e81024c54b69ba115e4af8e4d', '5', '客户标签组设置', 'log_type', 5, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:48:39', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('47206dd2f29a4243be1a5d9ab9bc24a4', '1', '流程操作', 'log_type', 1, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:47:14', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd56a7', '2', '系统全局配置', 'log_type', 2, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:47:54', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('7defed8fe1f5480989bfb59d5bbfe000', '0', '日志类型', 'log_type', 0, NULL, '0', '2019-04-29 11:44:02', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('9a803598199241baa2f4cc6eafcbe9d0', '3', '意向度设置', 'log_type', 3, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:48:11', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('b37cc67591044399955d764e5f06ebb6', '8', '客户回收策略配置', 'log_type', 8, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:50:08', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('e4c8ba7dc1544bfeaaecc9cb2556c9e1', '4', '客户标签设置', 'log_type', 4, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:48:32', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('16d5587425ba40ac89c276974434cc5e', '4', '查询', 'log_action', 4, NULL, '216489e977e848db963637a80d7465b4',
        '2019-04-29 11:46:33', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('1d89482d6e704773bcfb886cc4ad3f69', '3', '修改', 'log_action', 3, NULL, '216489e977e848db963637a80d7465b4',
        '2019-04-29 11:46:23', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('216489e977e848db963637a80d7465b4', '0', '日志动作', 'log_action', 0, NULL, '0', '2019-04-29 11:45:08', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('9b4750cab34f48e39934433dc95445bf', '2', '删除', 'log_action', 2, NULL, '216489e977e848db963637a80d7465b4',
        '2019-04-29 11:46:15', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('da52139334d94da6b9dd505b71cb248f', '1', '新增', 'log_action', 1, NULL, '216489e977e848db963637a80d7465b4',
        '2019-04-29 11:46:03', 1, 0);


########################### 分配相关表##############################

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd5699', '9', '自动分配', 'log_type', 9, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-04-29 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd9999', '10', '红娘分配', 'log_type', 10, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-08 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd9900', '11', '红娘调配', 'log_type', 11, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-08 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('da52139334d94da6b9dd505b71cb2422', '5', '分配', 'log_action', 5, NULL, '216489e977e848db963637a80d7465b4',
        '2019-04-29 11:46:03', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('da52139334d94da6b9dd505b71ff2403', '6', '调配', 'log_action', 6, NULL, '216489e977e848db963637a80d7465b4',
        '2019-05-08 11:46:03', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd1022', '12', '销售分配', 'log_type', 12, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-09 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457dd1995', '13', '销售调配', 'log_type', 13, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-09 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070bese344457dd0615', '14', '销售捞取', 'log_type', 14, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-09 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('da52139334d94da6b9dd505b71ff5682', '7', '捞取', 'log_action', 7, NULL, '216489e977e848db963637a80d7465b4',
        '2019-05-08 11:46:03', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('qeb36eb0f3394070be11c44457dd0615', '15', '销售放弃', 'log_type', 15, NULL, '7defed8fe1f5480989bfb59d5bbfe000',
        '2019-05-09 11:47:54', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('vg52139334d94da6b9dd505b71ff5682', '8', '放弃', 'log_action', 8, NULL, '216489e977e848db963637a80d7465b4',
        '2019-05-08 11:46:03', 1, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457tz0001', '0', '通知类型', 'notice_type', 0, NULL, '0',
        '2019-05-10 16:47:54', 0, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457tz0002', '1', '系统通知', 'notice_type', 1, NULL, '68b36eb0f3394070be11c44457tz0001',
        '2019-05-10 16:47:54', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('68b36eb0f3394070be11c44457tz0003', '2', '员工通知', 'notice_type', 2, NULL, '68b36eb0f3394070be11c44457tz0001',
        '2019-05-10 16:47:54',2, 0);

INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('386adf495e1f44db995aa9678eb654dc', 'matchmaker_allot', '红娘分配', 'allot_type', 3, NULL,
        'e76adf495e1f44db995aa9678eb6548d', '2019-06-04 10:24:00', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('236adf495e1f44db995aa9678eb65425', 'sales_auto_allot', '自动分配', 'allot_type', 1, NULL,
        'e76adf495e1f44db995aa9678eb6548d', '2019-06-04 10:24:00', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('126adf495e1f44db995aa9678eb6548a', 'sales_allot', '销售分配', 'allot_type', 2, NULL,
        'e76adf495e1f44db995aa9678eb6548d', '2019-06-04 10:24:00', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('4n6adf495e1f44db995aa9678eb654ga', 'sales_gain', '销售捞取', 'allot_type', 4, NULL,
        'e76adf495e1f44db995aa9678eb6548d', '2019-06-04 10:24:00', 1, 0);
INSERT INTO `SysDict`(`id`, `value`, `label`, `type`, `sort`, `remark`, `parent`, `createTime`, `isedit`, `deleted`)
VALUES ('076adf495e1f44db995aa9678eb6548d', '0', '分配类型', 'allot_type', 0, NULL, '0', '2019-06-04 10:21:52', 1, 0);


