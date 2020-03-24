package com.liveness.dflivenesslibrary.utils;

/**
 * Copyright (c) 2018-2019 DEEPFINCH Corporation. All rights reserved.
 */

public class DFViewShowUtils {

    public static final String DF_BOOLEAN_TRUE_STRING = "1";
    public static final String DF_BOOLEAN_FALSE_STRING = "0";

    public static String booleanTrans(boolean value) {
        return value ? DF_BOOLEAN_TRUE_STRING : DF_BOOLEAN_FALSE_STRING;
    }
}
