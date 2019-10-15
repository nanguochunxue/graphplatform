#!/usr/bin/perl -s
use utf8;
binmode(STDOUT,":encoding(gbk)");
$command="D:/install/pgsql/bin/psql -h 192.168.1.213 -p 5432 -U gpadmin ";
if(!(defined $db_access_id && $db_access_id != 1)){
    print "缺少必要参数 [db_access_id:数据库访问控制ID]\n";
    print "使用host，port，用户名，密码，数据库参数进行连接\n";
    if(!(defined $db && $db != 1)){
        print "缺少必要参数 [db:数据库]\n";
        exit 1;
    }else{
        $command .= "-d $db ";
    }
    if(!(defined $port && $port != 1)){
        print "缺少必要参数 [port:端口]\n";
        exit 1;
    }else{
        $command .= "-p $port ";
    }
}else{
    $command .= "db_access_id=$db_access_id ";
}

$command .= "-c \"\\COPY ( ";
if(!(defined $select_list && $select_list != 1)){
    print "缺少必要参数 [select_list:[选填]按字段卸载(不区分大小写)，需要卸载的字段列表]\n";
    exit 1;
}else{
    $command .= "select $select_list ";
}
if(!(defined $table_name && $table_name != 1)){
    print "缺少必要参数 [table_name:卸数目标表]\n";
    exit 1;
}else{
    $command .= "from $table_name ";
}
if(!(defined $table_filt && $table_filt != 1)){
}else{
    $command .= "where $table_filt";
}
$command .= ") ";
if(!(defined $outfile && $outfile != 1)){
    print "缺少必要参数 [outfile:导出文件(全路径)]\n";
    exit 1;
}else{
    $command .= "TO '$outfile'";
}
if(!(defined $fixed && $fixed != 1)){
    print "缺少必要参数 [fixed:定长标志(Y-定长 N-非定长)]\n";
    exit 1;
}
if(!(defined $delim && $delim != 1)){
    print "缺少必要参数 [delim:分隔符]\n";
    exit 1;
}
if(!(defined $enddelim && $enddelim != 1)){
    print "缺少必要参数 [enddelim:是否含末尾分隔符(Y-是 N-否)]\n";
    exit 1;
}
if(!(defined $charset && $charset != 1)){
    print "缺少必要参数 [charset:卸数字符集(如：utf8/gbk)]\n";
    exit 1;
}
if(!(defined $ctl_file_type && $ctl_file_type != 1)){
    print "缺少必要参数 [ctl_file_type:控制文件类型(0/1/2/3)]\n";
    print "0-不需要\n";
    print "1-DDL文件和控制文件\n";
    print "2-DDL文件\n";
    print "3-控制文件\n";
    exit 1;
}
$command .= "WITH csv HEADER DELIMITER '$delim'\"";

print "command : $command \n";
system($command);