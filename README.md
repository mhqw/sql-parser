# sql-parser

解析clickhouse sql，提取查询语句最外层的查询列

## Druid SQL Parser
请使用DruidSQLParserMain

将需要解析的sql语句放入main函数中的sqls字符串里，运行程序便会在控制台打印sql查询语句最外层的查询列

支持批量查询，每条sql语句用 ; 隔开

## Antlr SQL Parser

请使用AntlrSQLParserMain

将需要解析的sql语句放入main函数中的sql字符串里，运行程序便会在控制台打印sql查询语句最外层的查询列

不支持批量查询