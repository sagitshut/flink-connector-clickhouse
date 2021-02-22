package com.thorntree.bigdata.converter;

import org.apache.flink.connector.jdbc.internal.converter.AbstractJdbcRowConverter;
import org.apache.flink.table.types.logical.RowType;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 11:34
 */
public class ClickHouseRowConverter extends AbstractJdbcRowConverter {

    private static final long serialVersionUID = 1L;

    public ClickHouseRowConverter(RowType rowType) {
        super(rowType);
    }

    @Override
    public String converterName() {
        return "ClickHouse";
    }
}
