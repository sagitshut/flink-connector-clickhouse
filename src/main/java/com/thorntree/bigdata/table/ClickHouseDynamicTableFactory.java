package com.thorntree.bigdata.table;

import com.thorntree.bigdata.dialect.ClickHouseDialect;
import java.util.HashSet;
import java.util.Set;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.table.api.TableSchema;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.source.DynamicTableSource;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.factories.DynamicTableSinkFactory;
import org.apache.flink.table.factories.DynamicTableSourceFactory;
import org.apache.flink.table.factories.FactoryUtil;
import org.apache.flink.table.factories.SerializationFormatFactory;
import org.apache.flink.table.types.DataType;
import org.apache.flink.table.utils.TableSchemaUtils;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 14:04
 */
public class ClickHouseDynamicTableFactory implements DynamicTableSourceFactory, DynamicTableSinkFactory {

    public static final String IDENTIFIER = "clickhouse";
    private static final String DRIVER_NAME = "ru.yandex.clickhouse.ClickHouseDriver";


    @Override
    public DynamicTableSource createDynamicTableSource(Context context) {
        // either implement your custom validation logic here ...
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        final ReadableConfig config = helper.getOptions();

        // validate all options
        helper.validate();

        JdbcOptions jdbcOptions = getJdbcOptions(config);

        // get table schema
        TableSchema physicalSchema = TableSchemaUtils.getPhysicalSchema(context.getCatalogTable().getSchema());

        // table source
        return new ClickHouseDynamicTableSource(jdbcOptions, physicalSchema);
    }

    @Override
    public DynamicTableSink createDynamicTableSink(Context context) {

        // either implement your custom validation logic here ...
        final FactoryUtil.TableFactoryHelper helper = FactoryUtil.createTableFactoryHelper(this, context);

        // discover a suitable decoding format
        final EncodingFormat<SerializationSchema<RowData>> encodingFormat = helper.discoverEncodingFormat(
            SerializationFormatFactory.class,
            FactoryUtil.FORMAT);

        final ReadableConfig config = helper.getOptions();

        // validate all options
        helper.validate();

        // get the validated options
        JdbcOptions jdbcOptions = getJdbcOptions(config);

        // derive the produced data type (excluding computed columns) from the catalog table
        final DataType dataType = context.getCatalogTable().getSchema().toPhysicalRowDataType();

        // table sink
        return new ClickHouseDynamicTableSink(jdbcOptions, encodingFormat, dataType);
    }

    @Override
    public String factoryIdentifier() {
        return IDENTIFIER;
    }

    @Override
    public Set<ConfigOption<?>> requiredOptions() {
        Set<ConfigOption<?>> requiredOptions = new HashSet<>();
        requiredOptions.add(ClickHouseDynamicTableOption.URL);
        requiredOptions.add(ClickHouseDynamicTableOption.TABLE_NAME);
        requiredOptions.add(ClickHouseDynamicTableOption.USERNAME);
        requiredOptions.add(ClickHouseDynamicTableOption.PASSWORD);
        requiredOptions.add(ClickHouseDynamicTableOption.FORMAT);
        return requiredOptions;
    }

    @Override
    public Set<ConfigOption<?>> optionalOptions() {
        return new HashSet<>();
    }


    private JdbcOptions getJdbcOptions(ReadableConfig readableConfig) {
        final String url = readableConfig.get(ClickHouseDynamicTableOption.URL);

        final JdbcOptions.Builder builder = JdbcOptions.builder()
            .setDriverName(DRIVER_NAME)
            .setDBUrl(url)
            .setTableName(readableConfig.get(ClickHouseDynamicTableOption.TABLE_NAME))
            .setDialect(new ClickHouseDialect());

        readableConfig.getOptional(ClickHouseDynamicTableOption.USERNAME).ifPresent(builder::setUsername);
        readableConfig.getOptional(ClickHouseDynamicTableOption.PASSWORD).ifPresent(builder::setPassword);
        return builder.build();
    }

}
