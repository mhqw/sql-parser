package com.ygy.sql.parser.antlr;

import com.ygy.sql.parser.entity.Column;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-07-20
 */
public class CustomClickHouseParserBaseListener extends ClickHouseParserBaseListener {
    private final List<Column> columns = new ArrayList<>();
    private final List<String> columnLists = new ArrayList<>();

    public List<Column> getColumns() {
        return columns;
    }

    public List<String> getColumnLists() {
        return columnLists;
    }

    @Override
    public void enterColumnExprList(ClickHouseParser.ColumnExprListContext ctx) {
        if (ctx != null) {
            columnLists.add(ctx.getText());
        }
    }

    @Override
    public void enterColumnsExprColumn(ClickHouseParser.ColumnsExprColumnContext ctx) {
        if (ctx != null && ctx.getChildCount() == 1) {
            ParseTree child = ctx.getChild(0);
            if (child.getChildCount() == 1) {
                columns.add(new Column(child.getChild(0).getText(), "", ""));
            } else if (child.getChildCount() == 2) {
                columns.add(new Column(child.getChild(0).getText(), "", child.getChild(1).getText()));
            } else if (child.getChildCount() == 3) {
                columns.add(new Column(child.getChild(0).getText(), child.getChild(1).getText(), child.getChild(2).getText()));
            } else if (child.getChildCount() > 3) {
                columns.add(new Column(child.getText(), "", ""));
            }
        }
    }

}

