package com.ygy.sql.parser;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectItem;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickSchemaStatVisitor;
import com.alibaba.druid.util.JdbcConstants;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-07-14
 */
public class DruidSQLParserMain {
    public static void main(String[] args) {
        String sqls = "SELECT\n"
                + "    t,\n"
                + "    ((`今日请求` - `昨日请求`) / `昨日请求`) * 100 as `同比昨日变化`\n"
                + "FROM\n"
                + "(\n"
                + "    SELECT\n"
                + "        (intDiv(toUInt32(timestamp), 60) * 60) * 1000 as t,\n"
                + "        sum(`count`) as `今日请求`\n"
                + "    FROM perf.api_perf_log\n"
                + "    WHERE\n"
                + "        `timestamp` > $from\n"
                + "        and `timestamp` < $to\n"
                + "        and dt >= toDate($from)\n"
                + "        and dt <= toDate($to)\n"
                + "        AND namespace = 'api'\n"
                + "        AND subtag = '/rest/app/kwaishop/product/c/detail'\n"
                + "    GROUP BY t\n"
                + "    HAVING count(1) > 2\n"
                + "    ORDER BY t\n"
                + ")\n"
                + "LEFT JOIN\n"
                + "(\n"
                + "    SELECT\n"
                + "        (intDiv(toUInt32(timestamp) + 86400, 60) * 60) * 1000 as t,\n"
                + "        sum(`count`) as `昨日请求`\n"
                + "    FROM perf.api_perf_log\n"
                + "    WHERE\n"
                + "        `timestamp` > toDateTime($from - 86400)\n"
                + "        and `timestamp` < toDateTime($to - 86400)\n"
                + "        and dt >= toDate($from - 86400)\n"
                + "        and dt <= toDate($to - 86400)\n"
                + "        AND namespace = 'api'\n"
                + "        AND subtag = '/rest/app/kwaishop/product/c/detail'\n"
                + "    GROUP BY t\n"
                + "    HAVING count(1) > 2\n"
                + "    ORDER BY t\n"
                + ") USING t;";
        List<List<String>> outerQueryColumnsList = getOuterQueryColumns(sqls);
        for (int i = 0; i < outerQueryColumnsList.size(); i++) {
            List<String> columns = outerQueryColumnsList.get(i);
            System.out.println("sql" + (i + 1) + " outer query columns: " + columns);
        }
    }

    public static List<List<String>> getOuterQueryColumns(String sql) {
        List<SQLStatement> sqlStatementList = SQLUtils.parseStatements(sql, JdbcConstants.CLICKHOUSE);

        List<List<String>> outerQueryColumnsList = new ArrayList<>();

        for (SQLStatement sqlStatement : sqlStatementList) {
            ClickSchemaStatVisitor visitor = new ClickSchemaStatVisitor();
            sqlStatement.accept(visitor);

            List<String> outerQueryColumns = new ArrayList<>();

            if (sqlStatement instanceof SQLSelectStatement) {
                SQLSelectStatement selectStatement = (SQLSelectStatement) sqlStatement;
                List<SQLSelectItem> selectItems = selectStatement.getSelect().getQueryBlock().getSelectList();
                for (SQLSelectItem item : selectItems) {
                    outerQueryColumns.add(item.toString());
                }
            }
            outerQueryColumnsList.add(outerQueryColumns);
        }
        return outerQueryColumnsList;
    }
}