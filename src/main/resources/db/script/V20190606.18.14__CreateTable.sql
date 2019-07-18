drop table if exists WorkerWhiteList;
create table WorkerWhiteList(
id int not null auto_increment,
workerId int not null,
companyId varchar(50) NOT NULL DEFAULT '0',
createTime datetime not null default current_timestamp,
primary key(id),
unique key UX_WorkerWhiteList (workerId,companyId)
)engine=innodb default charset=utf8 comment='员工白名单,白名单内的员工可见明文手机号';