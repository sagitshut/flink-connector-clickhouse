package com.thorntree.bigdata.table;

import java.io.ByteArrayInputStream;
import java.sql.SQLException;
import org.apache.flink.api.common.serialization.SerializationSchema;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.jdbc.internal.options.JdbcOptions;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.table.data.RowData;
import ru.yandex.clickhouse.ClickHouseConnection;
import ru.yandex.clickhouse.ClickHouseDataSource;
import ru.yandex.clickhouse.ClickHouseStatement;
import ru.yandex.clickhouse.Writer;
import ru.yandex.clickhouse.domain.ClickHouseFormat;
import ru.yandex.clickhouse.settings.ClickHouseProperties;
import ru.yandex.clickhouse.settings.ClickHouseQueryParam;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 14:24
 */
public class ClickHouseSinkFunction  extends RichSinkFunction<RowData> {
    private static final long serialVersionUID = 1L;

    private final JdbcOptions jdbcOptions;
    private final SerializationSchema<RowData> serializationSchema;

    public ClickHouseSinkFunction(JdbcOptions jdbcOptions, SerializationSchema<RowData> serializationSchema) {
        this.jdbcOptions = jdbcOptions;
        this.serializationSchema = serializationSchema;
    }

    private static final String MAX_PARALLEL_REPLICAS_VALUE = "2";
    private ClickHouseConnection conn;
    private ClickHouseStatement stmt;


    @Override
    public void open(Configuration parameters) throws Exception {
        ClickHouseProperties properties = new ClickHouseProperties();
        properties.setUser(jdbcOptions.getUsername().orElse(null));
        properties.setPassword(jdbcOptions.getPassword().orElse(null));
        try {
            if (null == conn) {
                conn = new ClickHouseDataSource(jdbcOptions.getDbURL(), properties).getConnection();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void invoke(RowData value, Context context) throws Exception {
        byte[] serialize = serializationSchema.serialize(value);
        stmt = conn.createStatement();
        Writer table = stmt.write().table(jdbcOptions.getTableName());
        table.data(new ByteArrayInputStream(serialize), ClickHouseFormat.JSONEachRow)
            .addDbParam(ClickHouseQueryParam.MAX_PARALLEL_REPLICAS,MAX_PARALLEL_REPLICAS_VALUE).send();
    }

    @Override
    public void close() throws Exception {
        if (stmt != null) {
            stmt.close();
        }
        if (conn != null) {
            conn.close();
        }
    }

}
