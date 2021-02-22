package com.thorntree.bigdata.table;

import org.apache.flink.connector.jdbc.dialect.JdbcDialect;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.connector.jdbc.table.JdbcRowDataInputFormat;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.connector.source.InputFormatProvider;
import org.apache.flink.table.connector.source.ScanTableSource;
import org.apache.flink.table.types.logical.RowType;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 13:45
 */
public class ClickHouseDynamicTableSource implements ScanTableSource {

    private final JdbcOptions options;
    private final TableSchema tableSchema;

    public ClickHouseDynamicTableSource(JdbcOptions options, TableSchema tableSchema) {
        this.options = options;
        this.tableSchema = tableSchema;
    }

    @Override
    public ChangelogMode getChangelogMode() {
        return ChangelogMode.insertOnly();
    }

    @Override
    public ScanRuntimeProvider getScanRuntimeProvider(ScanContext scanContext) {
        final JdbcDialect dialect = options.getDialect();

        String query = dialect.getSelectFromStatement(
            options.getTableName(), tableSchema.getFieldNames(), new String[0]);

        final RowType rowType = (RowType) tableSchema.toRowDataType().getLogicalType();

        final JdbcRowDataInputFormat.Builder builder = JdbcRowDataInputFormat.builder()
            .setDrivername(options.getDriverName())
            .setDBUrl(options.getDbURL())
            .setUsername(options.getUsername().orElse(null))
            .setPassword(options.getPassword().orElse(null))
            .setQuery(query)
            .setRowConverter(dialect.getRowConverter(rowType))
            .setRowDataTypeInfo(scanContext
                .createTypeInformation(tableSchema.toRowDataType()));

        return InputFormatProvider.of(builder.build());
    }

    @Override
    public DynamicTableSource copy() {
        return new ClickHouseDynamicTableSource(options, tableSchema);
    }

    @Override
    public String asSummaryString() {
        return "ClickHouse Table Source";
    }
}
