drop table if exists InviteToShop;
CREATE TABLE InviteToShop (
  id int(11) not null auto_increment,
  memberId bigint(20) not null comment '客户ID',
  inviteDate date not null comment '预约日期',
  inviteTime tinyint(2) not null comment '预约时间段 1(8:00-10:00) 2(10:00-12:00) 3(13:00-15:00) 4(15:00-17:00) 5(17:00-19:00) 6(19:00-21:00)',
  inviteWorkerId int(11) not null comment '预约人',
  shopStatus tinyint(2) default '0' COMMENT '到店情况 0未到店 1已到店',
  shopTime datetime default null comment '到店时间',
  inviteType smallint(2) not null default '1' comment '到店类型 1预约到店 2主动到店',
  createTime datetime not null default current_timestamp comment '创建时间',
  primary key(id),
  KEY IX_InviteToShop_MemberId(memberId),
  KEY IX_InviteToShop_InviteDate (inviteDate),
  KEY IX_InviteToShop_shopTime (shopTime),
  KEY IX__InviteToShop_CreateTime (createTime)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='预约到店信息';