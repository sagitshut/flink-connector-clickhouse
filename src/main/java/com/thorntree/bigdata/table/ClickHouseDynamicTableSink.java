package com.thorntree.bigdata.table;

import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.table.connector.ChangelogMode;
import org.apache.flink.table.connector.format.EncodingFormat;
import org.apache.flink.table.connector.sink.DynamicTableSink;
import org.apache.flink.table.connector.sink.SinkFunctionProvider;
import org.apache.flink.table.data.RowData;
import org.apache.flink.table.types.DataType;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 14:03
 */
public class ClickHouseDynamicTableSink  implements DynamicTableSink {

    private final JdbcOptions jdbcOptions;
    private final EncodingFormat<SerializationSchema<RowData>> encodingFormat;
    private final DataType dataType;

    public ClickHouseDynamicTableSink(JdbcOptions jdbcOptions,
        EncodingFormat<SerializationSchema<RowData>> encodingFormat, DataType dataType) {
        this.jdbcOptions = jdbcOptions;
        this.encodingFormat = encodingFormat;
        this.dataType = dataType;
    }

    @Override
    public ChangelogMode getChangelogMode(ChangelogMode changelogMode) {
        return changelogMode;
    }

    @Override
    public SinkRuntimeProvider getSinkRuntimeProvider(Context context) {
        SerializationSchema<RowData> serializationSchema = encodingFormat.createRuntimeEncoder(context, dataType);
        ClickHouseSinkFunction clickHouseSinkFunction = new ClickHouseSinkFunction(jdbcOptions, serializationSchema);
        return SinkFunctionProvider.of(clickHouseSinkFunction);
    }

    @Override
    public DynamicTableSink copy() {
        return new ClickHouseDynamicTableSink(jdbcOptions, encodingFormat, dataType);
    }

    @Override
    public String asSummaryString() {
        return "ClickHouse Table Sink";
    }
}
