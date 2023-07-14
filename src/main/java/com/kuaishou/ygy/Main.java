package com.kuaishou.ygy;

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
public class Main {
    public static void main(String[] args) {
        String sqls = "SELECT\n"
                + "    (intDiv(toUInt32(timestamp), 60) * 60) * 1000 as t,\n"
                + "    concat(' topic:', extra1, ' consumer:', extra2) as key,\n"
                + "    max(`avg`)\n"
                + "FROM perf.kafka_perf_log\n"
                + "WHERE\n"
                + "    `timestamp` > 1689242340\n"
                + "    and `timestamp` < 1689245940\n"
                + "    and dt >= toDate(1689242340)\n"
                + "    and dt <= toDate(1689245940)\n"
                + "    and namespace = 'kafka.consumerThreadUsage.worker'\n"
                + "    AND kServiceName = ''\n"
                + "    AND kServiceStage = ''\n"
                + "    AND kServiceRegion = ''\n"
                + "GROUP BY\n"
                + "    t,\n"
                + "    key\n"
                + "ORDER BY\n"
                + "    t;"
                + "SELECT\n"
                + "    (intDiv(toUInt32(timestamp), 60) * 60) * 1000 as t,\n"
                + "    concat(' topic:', extra1, ' consumer:', extra2) as key,\n"
                + "    max(`avg`)\n"
                + "FROM perf.kafka_perf_log\n"
                + "WHERE\n"
                + "    `timestamp` > 1689242340\n"
                + "    and `timestamp` < 1689245940\n"
                + "    and dt >= toDate(1689242340)\n"
                + "    and dt <= toDate(1689245940)\n"
                + "    and namespace = 'kafka.consumerThreadUsage.worker'\n"
                + "    AND kServiceName = ''\n"
                + "    AND kServiceStage = ''\n"
                + "    AND kServiceRegion = ''\n"
                + "GROUP BY\n"
                + "    t,\n"
                + "    key\n"
                + "ORDER BY\n"
                + "    t; ";
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