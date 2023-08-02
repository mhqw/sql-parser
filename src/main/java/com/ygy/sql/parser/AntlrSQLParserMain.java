package com.ygy.sql.parser;

import com.ygy.sql.parser.antlr.ClickHouseLexer;
import com.ygy.sql.parser.antlr.ClickHouseParser;
import com.ygy.sql.parser.antlr.CustomClickHouseParserBaseListener;
import com.ygy.sql.parser.entity.Column;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-07-20
 */
public class AntlrSQLParserMain {
    public static void main(String[] args) {
        String sql = "SELECT\n" +
                "    t,\n" +
                "    ((`今日请求` - `昨日请求`) / `昨日请求`) * 100 as `同比昨日变化`\n" +
                "FROM\n" +
                "(\n" +
                "    SELECT\n" +
                "        (intDiv(toUInt32(timestamp), 60) * 60) * 1000 as t,\n" +
                "        sum(`count`) as `今日请求`\n" +
                "    FROM perf.api_perf_log\n" +
                "    WHERE\n" +
                "        `timestamp` > $from\n" +
                "        and `timestamp` < $to\n" +
                "        and dt >= toDate($from)\n" +
                "        and dt <= toDate($to)\n" +
                "        AND namespace = 'api'\n" +
                "        AND subtag = '/rest/app/kwaishop/product/c/detail'\n" +
                "    GROUP BY t\n" +
                "    HAVING count(1) > 2\n" +
                "    ORDER BY t\n" +
                ")\n" +
                "ANY LEFT JOIN\n" +
                "(\n" +
                "    SELECT\n" +
                "        (intDiv(toUInt32(timestamp) + 86400, 60) * 60) * 1000 as t,\n" +
                "        sum(`count`) as `昨日请求`\n" +
                "    FROM perf.api_perf_log\n" +
                "    WHERE\n" +
                "        `timestamp` > toDateTime($from - 86400)\n" +
                "        and `timestamp` < toDateTime($to - 86400)\n" +
                "        and dt >= toDate($from - 86400)\n" +
                "        and dt <= toDate($to - 86400)\n" +
                "        AND namespace = 'api'\n" +
                "        AND subtag = '/rest/app/kwaishop/product/c/detail'\n" +
                "    GROUP BY t\n" +
                "    HAVING count(1) > 2\n" +
                "    ORDER BY t\n" +
                ") USING  t";

        List<Column> columns = getOuterQueryColumns(sql);
        if (CollectionUtils.isNotEmpty(columns)) {
            for (Column column : columns) {
                if (StringUtils.isNotBlank(column.getAliasName())) {
                    System.out.println("columnName: " + column.getColumnName() + "\t\talias: " + column.getAliasName());
                } else {
                    System.out.println("columnName: " + column.getColumnName());
                }
            }
        }
    }

    public static List<Column> getOuterQueryColumns(String sql){
        ClickHouseLexer lexer = new ClickHouseLexer(new ANTLRInputStream(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ClickHouseParser parser = new ClickHouseParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();

        CustomClickHouseParserBaseListener clickhouseBaseBaseListener = new CustomClickHouseParserBaseListener();
        walker.walk(clickhouseBaseBaseListener, parser.queryStmt());

        List<Column> ret = new ArrayList<>();
        List<Column> columns = clickhouseBaseBaseListener.getColumns();
        List<String> columnLists = clickhouseBaseBaseListener.getColumnLists();

        if (CollectionUtils.isNotEmpty(columnLists)) {
            String firstColumnList = columnLists.get(0);
            if ("*".equals(firstColumnList)) {
                return List.of(new Column("*", "", ""));
            }

            StringBuilder stringBuilder = new StringBuilder();

            if (CollectionUtils.isNotEmpty(columns)) {
                stringBuilder.append(columns.get(0).toString());
                ret.add(columns.get(0));
            }

            if(stringBuilder.toString().equals(firstColumnList)) {
                return ret;
            }

            for (int i = 1;i < columns.size(); i++) {
                stringBuilder.append("," + columns.get(i).toString());
                ret.add(columns.get(i));
                if(stringBuilder.toString().equals(firstColumnList)) {
                    return ret;
                }
            }
        }

        return null;
    }
}
