package com.ygy.sql.parser.entity;

/**
 * @author 叶桂源 <yeguiyuan@kuaishou.com>
 * Created on 2023-08-02
 */
public class Column {
    private String columnName;
    private String asName;
    private String aliasName;

    public Column(String columnName, String asName, String aliasName) {
        this.columnName = columnName;
        this.asName = asName;
        this.aliasName = aliasName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getAsName() {
        return asName;
    }

    public void setAsName(String asName) {
        this.asName = asName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    @Override
    public String toString() {
        return columnName + asName + aliasName;
    }
}
