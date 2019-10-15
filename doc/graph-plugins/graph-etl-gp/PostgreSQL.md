## 操作

- 客户端连接

```
/data/master/gpseg-1/pg_hba.conf
host     all         gpadmin         10.10.10.0/24       trust

```

- 远程导出

```
# 本地安装PostgreSQL，或者拷贝 psql命令文件到本地
/Library/PostgreSQL/10/bin/psql -h 192.168.1.213 -p 5432 -U gpadmin -d demo_graph -c "\copy (select * from public.demo_vertex) to /temp/gp/my_data.txt"
```

- 本地导出
```
psql -p 5432 -U gpadmin -d demo_graph -c "\copy (select * from public.demo_vertex) to /temp/gp/my_data.txt"
```