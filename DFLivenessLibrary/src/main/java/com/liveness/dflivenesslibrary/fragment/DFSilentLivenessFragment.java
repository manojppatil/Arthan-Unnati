package com.liveness.dflivenesslibrary.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.deepfinch.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.fragment.model.DFSilentOverlayModel;
import com.liveness.dflivenesslibrary.liveness.util.Constants;
import com.liveness.dflivenesslibrary.process.DFSilentLivenessProcess;
import com.liveness.dflivenesslibrary.utils.DFBitmapUtils;
import com.liveness.dflivenesslibrary.utils.DFViewShowUtils;

import java.util.HashMap;
import java.util.Map;

import static com.liveness.dflivenesslibrary.liveness.DFSilentLivenessActivity.KEY_HINT_MESSAGE_FACE_NOT_VALID;
import static com.liveness.dflivenesslibrary.liveness.DFSilentLivenessActivity.KEY_HINT_MESSAGE_HAS_FACE;
import static com.liveness.dflivenesslibrary.liveness.DFSilentLivenessActivity.KEY_HINT_MESSAGE_NO_FACE;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFSilentLivenessFragment extends DFActionLivenessFragment {
    private static final String TAG = DFSilentLivenessFragment.class.getSimpleName();
    private TextView mTvHintView;
    private String mHasFaceHint, mNoFaceHint, mFaceNotValid;
    private Map<String, DFSilentOverlayModel> mFaceHintMap;
    private String mFaceProcessResult;

    @Override
    protected void initialize() {
        super.initialize();
        mProcess = new DFSilentLivenessProcess(getActivity(), mCameraBase);
        mProcess.registerLivenessDetectCallback(mLivenessListener);
        mOverlayView.showBorder();
        mVGBottomDots.setVisibility(View.INVISIBLE);
        mTimeView.setVisibility(View.GONE);
        mGvView.setVisibility(View.GONE);
        mDetectList = new String[1];
        mDetectList[0] = Constants.HOLD_STILL;
        mTimeViewContoller = null;

        mTvHintView = (TextView) mRootView.findViewById(R.id.id_tv_silent_hint);
        mNoteTextView.setVisibility(View.GONE);
        mTvHintView.setVisibility(View.VISIBLE);
        mHasFaceHint = getActivity().getIntent().getStringExtra(KEY_HINT_MESSAGE_HAS_FACE);
        mNoFaceHint = getActivity().getIntent().getStringExtra(KEY_HINT_MESSAGE_NO_FACE);
        mFaceNotValid = getActivity().getIntent().getStringExtra(KEY_HINT_MESSAGE_FACE_NOT_VALID);
        if (mNoFaceHint != null) {
            mTvHintView.setText(mNoFaceHint);
        }

        initFaceHintMap();
    }

    @Override
    protected void startAnimation(int animation) {
    }

    @Override
    protected boolean isSilent() {
        return true;
    }

    @Override
    protected void updateUi(int stringId, int animationId, int number) {
    }

    @Override
    protected void onFaceDetectCallback(final boolean hasFace, boolean faceValid) {
        String hasFaceShow = DFViewShowUtils.booleanTrans(hasFace);
        String faceValidShow = DFViewShowUtils.booleanTrans(faceValid);
        String faceProcessResult = hasFaceShow.concat("_").concat(faceValidShow);
        if (!TextUtils.equals(mFaceProcessResult, faceProcessResult)) {
            mFaceProcessResult = faceProcessResult;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DFSilentOverlayModel silentOverlayModel = mFaceHintMap.get(mFaceProcessResult);
                    String hintID = silentOverlayModel.getShowHint();
                    int borderColor = silentOverlayModel.getBorderColor();
                    if (borderColor != -1) {
                        mOverlayView.setBorderColor(borderColor);
                    }
                    if (hintID != null) {
                        mTvHintView.setText(hintID);
                    }
                }
            });

        }
    }

    @Override
    protected void onLivenessDetectCallBack(final int value, final int status, final float qualityScore, final byte[] livenessEncryptResult, final byte[] videoResult, final DFLivenessSDK.DFLivenessImageResult[] imageResult) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (value == Constants.LIVENESS_SUCCESS) {
                    if (imageResult != null) {
                        for (DFLivenessSDK.DFLivenessImageResult itemImageResult : imageResult) {
                            byte[] image = itemImageResult.image;
                            Bitmap cropBitmap = null;
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                            cropBitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
                            Bitmap bmp = DFBitmapUtils.cropResultBitmap(cropBitmap, mCameraBase.getPreviewWidth(), mCameraBase.getPreviewHeight(), mCameraBase.getScanRatio());
                            itemImageResult.detectImage = DFBitmapUtils.convertBmpToJpeg(bmp);
                            DFBitmapUtils.recyleBitmap(cropBitmap);
                            DFBitmapUtils.recyleBitmap(bmp);
                        }
                    }

                    mLivenessResultFileProcess.saveFinalEncrytFile(livenessEncryptResult, videoResult, imageResult, qualityScore);
                } else if (value == Constants.DETECT_BEGIN_WAIT) {
                    showDetectWaitUI();
                } else if (value == Constants.DETECT_END_WAIT) {
                    removeDetectWaitUI();
                }
            }
        });
    }

    protected int isBottomDotsVisibility() {
        return View.INVISIBLE;
    }

    private void initFaceHintMap() {
        mFaceHintMap = new HashMap<>();
        mFaceHintMap.put("0_0", new DFSilentOverlayModel(mNoFaceHint, Color.RED));
        mFaceHintMap.put("0_1", new DFSilentOverlayModel(mNoFaceHint, Color.RED));
        mFaceHintMap.put("1_0", new DFSilentOverlayModel(mFaceNotValid, Color.RED));
        mFaceHintMap.put("1_1", new DFSilentOverlayModel(mHasFaceHint, Color.GREEN));
    }
}
