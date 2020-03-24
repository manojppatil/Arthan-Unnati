package com.liveness.dflivenesslibrary.process;

import android.app.Activity;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;

import com.deepfinch.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.camera.CameraBase;
import com.liveness.dflivenesslibrary.liveness.util.Constants;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFSilentLivenessProcess extends DFActionLivenessProcess {

    public DFSilentLivenessProcess(Activity context, CameraBase cameraBase) {
        super(context, cameraBase);
    }

    @Override
    protected DFLivenessSDK.DFLivenessOutputType getOutputType(Bundle bundle) {
        return DFLivenessSDK.DFLivenessOutputType.getOutputTypeByValue(Constants.MULTIIMG);
    }

    @Override
    protected DFLivenessSDK.DFLivenessComplexity getComplexity(Bundle bundle) {
        return DFLivenessSDK.DFLivenessComplexity.WRAPPER_COMPLEXITY_HARD;
    }

    @Override
    protected boolean isSilent() {
        return true;
    }

    @Override
    protected DFLivenessSDK.DFLivenessMotion[] getMotionList() {
        DFLivenessSDK.DFLivenessMotion[] motions = new DFLivenessSDK.DFLivenessMotion[1];
        motions[0] = DFLivenessSDK.DFLivenessMotion.HOLD_STILL;
        return motions;
    }

    @Override
    protected void setDetectorParameters(DFLivenessSDK detector) {
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_HOLD_STILL, 0.0f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_TRACK_MISSING, 1.f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_DETECT_NUMBER, 1.f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_TIME_INTERVAL, 30.f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_FACE_RET_MAX_RATE, 0.75f);
        detector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_SILENT_FACE_OFFSET_RATE, 0.4f);

        setSilentDetectionRegion(detector);
//                    mDetector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_HOLD_STILL_FRAME, 10.f);
//                    mDetector.setThreshold(DFLivenessSDK.DFLivenessKey.KEY_HOLD_STILL_POS, 0.80f);
    }


    /**
     * This function is used to calculate the circle region in detection UI.
     * The region is used to limit the detection range of silent, if there is a human face in this region,
     * we will gather clear images of this face.
     * The default region is the image size.
     *
     * @param detector
     */
    private void setSilentDetectionRegion(DFLivenessSDK detector) {

        try {
            RectF region = new RectF();

            float scale = (float) mCameraBase.getPreviewWidth() / mCameraBase.getOverlapHeight();

            Rect rect = mCameraBase.getScanRect();
            region.left = (mCameraBase.getOverlapHeight() - rect.bottom + 0.f) * scale;
            region.top = (mCameraBase.getOverlapWidth() - rect.right + 0.f) * scale;
            region.right = region.left + rect.width() * scale;
            region.bottom = region.top + rect.height() * scale;

            int margin = (int) (rect.width() * 0.05);
            region.left = region.left - margin;
            region.top = region.top - margin;
            region.right = region.right + margin;
            region.bottom = region.bottom + margin;


            detector.setSilentDetectRegion(region);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
