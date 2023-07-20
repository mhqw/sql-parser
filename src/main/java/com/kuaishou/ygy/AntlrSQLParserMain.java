package com.kuaishou.ygy;

import java.util.List;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.apache.commons.collections4.CollectionUtils;

import com.kuaishou.ygy.antlr.ClickHouseLexer;
import com.kuaishou.ygy.antlr.ClickHouseParser;
import com.kuaishou.ygy.antlr.CustomClickHouseParserBaseListener;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-07-20
 */
public class AntlrSQLParserMain {
    public static void main(String[] args) {
        String sql = "SELECT \n"
                + "    (SELECT AVG(column1) FROM some_table WHERE condition1) AS avg1,\n"
                + "    (SELECT AVG(column2) FROM some_table WHERE condition2) AS avg2;";

        List<String> columns = getOuterQueryColumns(sql);
        if (CollectionUtils.isNotEmpty(columns)) {
            System.out.println("outer query columns: [" + columns.get(0) + "]");
        }
    }

    public static List<String> getOuterQueryColumns(String sql){
        ClickHouseLexer lexer = new ClickHouseLexer(new ANTLRInputStream(sql));
        CommonTokenStream tokenStream = new CommonTokenStream(lexer);
        ClickHouseParser parser = new ClickHouseParser(tokenStream);
        ParseTreeWalker walker = new ParseTreeWalker();

        CustomClickHouseParserBaseListener clickhouseBaseBaseListener = new CustomClickHouseParserBaseListener();
        walker.walk(clickhouseBaseBaseListener, parser.queryStmt());

        return clickhouseBaseBaseListener.getColumns();
    }
}
