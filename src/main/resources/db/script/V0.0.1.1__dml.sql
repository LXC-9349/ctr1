INSERT IGNORE INTO `SmsChannel`
VALUES ('1', '亿美软通', 'http://bjmtn.b2m.cn', NULL, '', 'emayChannel', '2019-06-06 11:12:19', 0, '【超脑云】', 2);
INSERT IGNORE INTO `SmsChannel`
VALUES ('2', '秒嘀', 'https://openapi.miaodiyun.com/distributor/sendSMS',
        'http://{ip}:{port}/api/sms_back/status/miaodiChannel', 'http://{ip}:{port}/api/sms_back/up/miaodiChannel',
        'miaodiChannel', '2019-06-06 11:15:01', 0, '【超脑云】', 1);