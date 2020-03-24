package com.liveness.dflivenesslibrary.liveness.util;

/**
 * Copyright (c) 2018-2019 DEEPFINCH Corporation. All rights reserved.
 **/

public class Constants {
    public static int PREVIEW_WIDTH = 1280;
    public static int PREVIEW_HEIGHT = 720;

    public static final String SEQUENCE = "sequence";
    public static final String OUTTYPE = "outType";
    public static final String RESULT = "result";
    public static final String THRESHOLD = "threshold";
    public static final String LOST = "lost";

    // motion value
    public static final String BLINK = "BLINK";
    public static final String NOD = "NOD";
    public static final String MOUTH = "MOUTH";
    public static final String YAW = "YAW";
    public static final String HOLD_STILL = "STILL";
    // outtype value
    public static final String SINGLEIMG = "singleImg";
    public static final String MULTIIMG = "multiImg";
    public static final String VIDEO = "video";
    public static final String FULLVIDEO = "fullVideo";
    // complexity value
    public static final String EASY = "easy";
    public static final String NORMAL = "normal";
    public static final String HARD = "hard";
    public static final String HELL = "hell";

    public static final int LIVENESS_SUCCESS = 0x86243331;
    public static final int LIVENESS_TRACKING_MISSED = 0x86243332;
    public static final int LIVENESS_TIME_OUT = 0x86243333;
    public static final int DETECT_BEGIN_WAIT = 5000;
    public static final int DETECT_END_WAIT = 5001;
}
