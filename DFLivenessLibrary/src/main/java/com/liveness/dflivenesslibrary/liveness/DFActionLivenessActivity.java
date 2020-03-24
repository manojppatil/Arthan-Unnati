package com.liveness.dflivenesslibrary.liveness;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;

import com.deepfinch.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.DFAcitivityBase;
import com.liveness.dflivenesslibrary.DFProductResult;
import com.liveness.dflivenesslibrary.DFTransferResultInterface;
import com.liveness.dflivenesslibrary.callback.DFLivenessResultCallback;
import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.fragment.DFActionLivenessFragment;
import com.liveness.dflivenesslibrary.fragment.DFProductFragmentBase;
import com.liveness.dflivenesslibrary.liveness.util.LivenessUtils;

import java.io.File;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFActionLivenessActivity extends DFAcitivityBase implements DFLivenessResultCallback {
    private static final String TAG = "LivenessActivity";

    /**
     * Error loading library file
     */
    public static final int RESULT_CREATE_HANDLE_ERROR = 1001;

    /**
     * Internal error
     */
    public static final int RESULT_INTERNAL_ERROR = 3;

    /**
     * Package name binding error
     */
    public static final int RESULT_SDK_INIT_FAIL_APPLICATION_ID_ERROR = 4;

    /**
     * License expired
     */
    public static final int RESULT_SDK_INIT_FAIL_OUT_OF_DATE = 5;

    /**
     * The file path where the result is saved is passed in
     */
    public static String EXTRA_RESULT_PATH = "com.deepfinch.liveness.resultPath";

    /**
     * The sequence of action motion
     */
    public static final String EXTRA_MOTION_SEQUENCE = "com.deepfinch.liveness.motionSequence";

    /**
     *  output type
     */
    public static final String OUTTYPE = "outType";

    /**
     * set complexity
     */
    public static final String COMPLEXITY = "complexity";

    /**
     * Sets whether to return picture results or not
     */
    public static final String KEY_DETECT_IMAGE_RESULT = "key_detect_image_result";

    /**
     * Sets whether to return the video result, and only the video mode returns
     */
    public static final String KEY_DETECT_VIDEO_RESULT = "key_detect_video_result";

    public static final String LIVENESS_FILE_NAME = "livenessResult";
    public static final String LIVENESS_VIDEO_NAME = "livenessVideoResult.mp4";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        EXTRA_RESULT_PATH = bundle.getString(EXTRA_RESULT_PATH);

        if (EXTRA_RESULT_PATH == null) {
            EXTRA_RESULT_PATH = Environment
                    .getExternalStorageDirectory().getAbsolutePath()
                    + File.separator
                    + "liveness" + File.separator;
        }
        File livenessFolder = new File(EXTRA_RESULT_PATH);
        if (!livenessFolder.exists()) {
            livenessFolder.mkdirs();
        }
    }

    @Override
    protected DFProductFragmentBase getFrament() {
        return new DFActionLivenessFragment();
    }

    @Override
    protected int getTitleString() {
        return R.string.string_action_liveness;
    }

    @Override
    public void saveFinalEncrytFile(byte[] livenessEncryptResult, byte[] videoResult, DFLivenessSDK.DFLivenessImageResult[] imageResult, float qualityScore) {
        Intent intent = new Intent();

        DFProductResult result = new DFProductResult();
        result.setQualityScore(qualityScore);
        boolean isReturnImage = getIntent().getBooleanExtra(KEY_DETECT_IMAGE_RESULT, false);
        LivenessUtils.logI(TAG, "imageResult", "length", imageResult.length);
        if (isReturnImage) {
            result.setLivenessImageResults(imageResult);
        }
        if (videoResult != null) {
            result.setLivenessVideoResult(videoResult);
        }

        if (livenessEncryptResult != null) {
            result.setLivenessEncryptResult(livenessEncryptResult);
            ((DFTransferResultInterface)getApplication()).setResult(result);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void deleteLivenessFiles() {
        LivenessUtils.deleteFiles(EXTRA_RESULT_PATH);
    }

    @Override
    public void saveFile(byte[] livenessEncryptResult) {
        LivenessUtils.saveFile(livenessEncryptResult, EXTRA_RESULT_PATH, LIVENESS_FILE_NAME);
    }
}
