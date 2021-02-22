package com.thorntree.bigdata.table;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;

/**
 * @description:
 * @author: lxs
 * @create: 2021-02-22 14:09
 */
public class ClickHouseDynamicTableOption {

    public static final ConfigOption<String> URL = ConfigOptions
        .key("url")
        .stringType()
        .noDefaultValue()
        .withDescription("the jdbc database url.");

    public static final ConfigOption<String> TABLE_NAME = ConfigOptions
        .key("table-name")
        .stringType()
        .noDefaultValue()
        .withDescription("the jdbc table name.");

    public static final ConfigOption<String> USERNAME = ConfigOptions
        .key("username")
        .stringType()
        .noDefaultValue()
        .withDescription("the jdbc user name.");

    public static final ConfigOption<String> PASSWORD = ConfigOptions
        .key("password")
        .stringType()
        .noDefaultValue()
        .withDescription("the jdbc password.");

    public static final ConfigOption<String> FORMAT = ConfigOptions
        .key("format")
        .stringType()
        .noDefaultValue()
        .withDescription("the format.");

}
