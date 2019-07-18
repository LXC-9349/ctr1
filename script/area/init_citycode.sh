#!/bin/sh
#
#Author: liujiajun
#Date:
#Description: 初始化城市代码数据
#Usage:
#

source /etc/profile
export LC_ALL=C

#执行sql语句
function executeSql()
{
  sql="$1"
  
  if [ "$sql" = "" ]
  then
    cat | mysql -ucrm -pcrm123123 marry_crm -N --default-character-set=utf8 --local-infile -A
  else
    echo "$sql" | mysql -ucrm -pcrm123123 marry_crm -N --default-character-set=utf8 --local-infile -A
  fi
}

function handler()
{
  rm -rf citycode.sql
  echo "delete from SysDict where type in('province','city','county');" | executeSql
  SAVEDIFS=$IFS
  IFS="|"
  cat citycode.txt | while read codeId parentId codeName vtype remark; do
    if [ -z "$parentId" ]; then
      parentId="0"
    fi
    id=`cat /proc/sys/kernel/random/uuid | sed 's/-//g'`
    sql="insert into SysDict(id,value,parent,label,type,remark) values('$id','$codeId','$parentId','$codeName','$vtype','$remark');"
    echo "$sql"
  done >> citycode.sql
  cat citycode.sql | executeSql
  IFS=$SAVEIFS
}

#主函数
function main()
{
  handler
}

main
