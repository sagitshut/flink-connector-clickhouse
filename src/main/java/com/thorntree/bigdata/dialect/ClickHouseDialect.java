package com.thorntree.bigdata.dialect;

import com.thorntree.bigdata.converter.ClickHouseRowConverter;
import java.util.Optional;
import org.apache.flink.connector.jdbc.dialect.JdbcDialect;
import org.apache.flink.connector.jdbc.internal.converter.JdbcRowConverter;
import org.apache.flink.table.types.logical.RowType;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 11:51
 */
public class ClickHouseDialect  implements JdbcDialect {

    @Override
    public Optional<String> defaultDriverName() {
        return Optional.of("ru.yandex.clickhouse.ClickHouseDriver");
    }

    @Override
    public String quoteIdentifier(String identifier) {
        return "`" + identifier + "`";
    }

    @Override
    public String dialectName() {
        return "ClickHouse";
    }

    @Override
    public boolean canHandle(String s) {
        return s.startsWith("jdbc:clickhouse:");
    }

    @Override
    public JdbcRowConverter getRowConverter(RowType rowType) {
        return new ClickHouseRowConverter(rowType);
    }
}
