package com.kuaishou.ygy.antlr;

import java.util.ArrayList;
import java.util.List;

import org.antlr.v4.runtime.tree.ParseTreeWalker;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-07-20
 */
public class CustomClickHouseParserBaseListener extends ClickHouseParserBaseListener {
    private final List<String> columns = new ArrayList<>();

    public List<String> getColumns() {
        return columns;
    }

    @Override
    public void enterQuery(ClickHouseParser.QueryContext ctx) {
        ParseTreeWalker queryWalker = new ParseTreeWalker();
        queryWalker.walk(new ClickHouseParserBaseListener() {
            public void enterColumnExprList(ClickHouseParser.ColumnExprListContext ctx) {
                if (ctx != null) {
                    String column = ctx.getText();
                    columns.add(column);
                }
            }
        }, ctx);
    }
}

