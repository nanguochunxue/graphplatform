package com.haizhi.graph.server.api.gdb.search.result;

import com.haizhi.graph.server.api.gdb.search.bean.CollectionType;

/**
 * Created by chengmo on 2018/1/24.
 */
public class Projection {

    /**
     * 表的类型，顶点表/边表
     */
    private CollectionType collectionType;
    private String table;
    private String field;

    public Projection(String table, String field) {
        this(CollectionType.DOCUMENT, table, field);
    }

    public Projection(CollectionType collectionType, String table, String field) {
        this.collectionType = collectionType;
        this.table = table;
        this.field = field;
    }

    public Projection() {}

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public CollectionType getCollectionType()
    {
        return collectionType;
    }

    public void setCollectionType(CollectionType collectionType)
    {
        this.collectionType = collectionType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Projection that = (Projection) o;

        if (table != null ? !table.equals(that.table) : that.table != null) return false;
        return field != null ? field.equals(that.field) : that.field == null;
    }

    @Override
    public int hashCode() {
        int result = table != null ? table.hashCode() : 0;
        result = 31 * result + (field != null ? field.hashCode() : 0);
        return result;
    }
}
