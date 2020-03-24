package com.liveness.dflivenesslibrary.fragment;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.deepfinch.liveness.DFLivenessSDK;
import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.callback.DFLivenessResultCallback;
import com.liveness.dflivenesslibrary.liveness.DFActionLivenessActivity;
import com.liveness.dflivenesslibrary.liveness.util.Constants;
import com.liveness.dflivenesslibrary.liveness.util.DFSensorManager;
import com.liveness.dflivenesslibrary.liveness.util.LivenessUtils;
import com.liveness.dflivenesslibrary.process.DFActionLivenessProcess;
import com.liveness.dflivenesslibrary.view.CircleTimeView;
import com.liveness.dflivenesslibrary.view.DFGifView;
import com.liveness.dflivenesslibrary.view.DeepFinchAlertDialog;
import com.liveness.dflivenesslibrary.view.TimeViewContoller;

import static android.app.Activity.RESULT_CANCELED;
import static com.liveness.dflivenesslibrary.liveness.DFActionLivenessActivity.EXTRA_MOTION_SEQUENCE;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFActionLivenessFragment extends DFProductFragmentBase {
    private static final String TAG = "DFLivenessFragment";

    private static final int CURRENT_ANIMATION = -1;

    protected DFActionLivenessProcess mProcess;
    protected DFGifView mGvView;
    protected TextView mNoteTextView;
    protected ViewGroup mVGBottomDots;
    private RelativeLayout mWaitDetectView;
    private View mAnimFrame;
    protected CircleTimeView mTimeView;
    protected String[] mDetectList = null;
    private DeepFinchAlertDialog mDialog;
    protected TimeViewContoller mTimeViewContoller;
    protected DFLivenessSDK.DFLivenessMotion[] mMotionList = null;
    protected DFSensorManager mSensorManger;
    private boolean mIsStart = false;
    protected DFLivenessResultCallback mLivenessResultFileProcess;

    @Override
    protected int getLayoutResourceId() {
        return R.layout.layout_liveness_fragment;
    }

    protected void initView() {
        mGvView = (DFGifView) mRootView.findViewById(R.id.id_gv_play_action);
        mNoteTextView = (TextView) mRootView.findViewById(R.id.noteText);
        mWaitDetectView = (RelativeLayout) mRootView.findViewById(R.id.wait_time_notice);
        mWaitDetectView.setVisibility(View.VISIBLE);
        mAnimFrame = mRootView.findViewById(R.id.anim_frame);
        mAnimFrame.setVisibility(View.INVISIBLE);
        mVGBottomDots = (ViewGroup) mRootView.findViewById(R.id.viewGroup);
        if (mDetectList != null && mDetectList.length >= 1) {
            for (int i = 0; i < mDetectList.length; i++) {
                TextView tvBottomCircle = new TextView(getActivity());
                tvBottomCircle.setBackgroundResource(R.drawable.drawable_liveness_detect_bottom_cicle_bg_selector);
                tvBottomCircle.setEnabled(i == 0 ? false : true);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dp2px(8),
                        dp2px(8));
                layoutParams.leftMargin = dp2px(8);
                mVGBottomDots.addView(tvBottomCircle, layoutParams);
            }
        }

        mTimeView = (CircleTimeView) mRootView.findViewById(R.id.time_view);
        mTimeViewContoller = new TimeViewContoller(mTimeView);
    }

    @Override
    protected void initialize() {
        mLivenessResultFileProcess = (DFLivenessResultCallback) getActivity();
        mSensorManger = new DFSensorManager(getActivity());
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null) {
            String motionString = bundle.getString(EXTRA_MOTION_SEQUENCE);
            if (motionString != null) {
                mDetectList = LivenessUtils.getDetectActionOrder(motionString);
                mMotionList = LivenessUtils.getMctionOrder(motionString);
            }
        }

        mProcess = new DFActionLivenessProcess(getActivity(), mCameraBase);
        mProcess.registerLivenessDetectCallback(mLivenessListener);

        initView();
    }

    @Override
    public void onResume() {
        super.onResume();

        mSensorManger.registerListener(mSensorEventListener);
        if (mIsStart && !isSilent()) {
            showDialog();
        }
    }

    private void restartAnimationAndLiveness() {
        setLivenessState(false);
        mLivenessResultFileProcess.deleteLivenessFiles();
        if (mDetectList.length >= 1) {
            View childAt = mVGBottomDots.getChildAt(0);
            childAt.setEnabled(false);
        }
        startAnimation(CURRENT_ANIMATION);
    }


    @Override
    public void onPause() {
        super.onPause();
        mSensorManger.unregisterListener(mSensorEventListener);
    }

    protected DFActionLivenessProcess.OnLivenessCallBack mLivenessListener = new DFActionLivenessProcess.OnLivenessCallBack() {
        @Override
        public void onLivenessDetect(final int value, final int status, float qualityScore, byte[] livenessEncryptResult,
                                     byte[] videoResult, DFLivenessSDK.DFLivenessImageResult[] imageResult) {
            Log.i(TAG, "onLivenessDetect" + "***value***" + value);
            onLivenessDetectCallBack(value, status, qualityScore, livenessEncryptResult, videoResult, imageResult);
        }

        @Override
        public void onFaceDetect(boolean hasFace, boolean faceValid) {
            onFaceDetectCallback(hasFace, faceValid);
        }
    };

    protected void removeDetectWaitUI() {
        mWaitDetectView.setVisibility(View.GONE);
        setLivenessState(false);
        mAnimFrame.setVisibility(View.VISIBLE);
        if (isSilent() == false) {
            onLivenessDetectCallBack(mMotionList[0].getValue(), 0, 0, null, null, null);
        }
    }

    protected void showDetectWaitUI() {
        mWaitDetectView.setVisibility(View.VISIBLE);
        mIsStart = true;
        if (mTimeViewContoller != null) {
            mTimeViewContoller.setCallBack(null);
        }
    }

    protected boolean isSilent() {
        return false;
    }

    private void setLivenessState(boolean pause) {
        if (null == mProcess) {
            return;
        }
        if (pause) {
            mProcess.stopLiveness();
        } else {
            mProcess.startLiveness();
        }
    }

    protected void onLivenessDetectCallBack(final int value, final int status, final float qualityScore, final byte[] livenessEncryptResult, final byte[] videoResult, final DFLivenessSDK.DFLivenessImageResult[] imageResult) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (value == DFLivenessSDK.DFLivenessMotion.BLINK.getValue()) {
                    updateUi(R.string.note_blink, R.raw.raw_liveness_detect_blink, status);
                } else if (value == DFLivenessSDK.DFLivenessMotion.MOUTH.getValue()) {
                    updateUi(R.string.note_mouth, R.raw.raw_liveness_detect_mouth, status);
                } else if (value == DFLivenessSDK.DFLivenessMotion.NOD.getValue()) {
                    updateUi(R.string.note_nod, R.raw.raw_liveness_detect_nod, status);
                } else if (value == DFLivenessSDK.DFLivenessMotion.YAW.getValue()) {
                    updateUi(R.string.note_yaw, R.raw.raw_liveness_detect_yaw, status);
                } else if (value == DFLivenessSDK.DFLivenessMotion.HOLD_STILL.getValue()) {
                    updateUi(R.string.note_hold_still, R.raw.raw_liveness_detect_holdstill, status);
                } else if (value == Constants.LIVENESS_SUCCESS) {
                    mLivenessResultFileProcess.saveFinalEncrytFile(livenessEncryptResult, videoResult, imageResult, qualityScore);
                } else if (value == Constants.LIVENESS_TRACKING_MISSED) {
                    showDialog();
                    mLivenessResultFileProcess.saveFile(livenessEncryptResult);
                } else if (value == Constants.LIVENESS_TIME_OUT) {
                    showDialog();
                    mLivenessResultFileProcess.saveFile(livenessEncryptResult);
                } else if (value == Constants.DETECT_BEGIN_WAIT) {
                    showDetectWaitUI();
                } else if (value == Constants.DETECT_END_WAIT) {
                    removeDetectWaitUI();
                }
            }
        });
    }

    protected void onFaceDetectCallback(boolean hasFace, boolean faceValid) {

    }

    protected int isBottomDotsVisibility() {
        return View.VISIBLE;
    }

    protected void showIndicateView() {
        if (mGvView != null) {
            mGvView.setVisibility(View.VISIBLE);
        }
        if (mVGBottomDots != null) {
            mVGBottomDots.setVisibility(isBottomDotsVisibility());
        }
        if (mNoteTextView != null && !isSilent()) {
            mNoteTextView.setVisibility(View.VISIBLE);
        }
    }

    private boolean isDialogShowing() {
        return mDialog != null && mDialog.isShowing();
    }

    private void hideTimeContoller() {
        if (mTimeViewContoller != null) {
            mTimeViewContoller.hide();
        }
    }

    private void hideIndicateView() {
        if (mGvView != null) {
            mGvView.setVisibility(View.GONE);
        }
        if (mVGBottomDots != null) {
            mVGBottomDots.setVisibility(View.GONE);
        }
        if (mNoteTextView != null) {
            mNoteTextView.setVisibility(View.GONE);
        }
    }

    protected void showDialog() {
        if (isDialogShowing()) {
            return;
        }
        if (mDetectList.length >= 1) {
            for (int i = 0; i < mDetectList.length; i++) {
                View childAt = mVGBottomDots.getChildAt(i);
                if (childAt != null) {
                    childAt.setEnabled(true);
                }
            }
        }

        hideTimeContoller();
        hideIndicateView();
        mDialog = new DeepFinchAlertDialog(getActivity()).builder().setCancelable(false).
                setTitle(getStringWithID(R.string.livenesslibrary_failure_dialog_title)).setNegativeButton(getStringWithID(R.string.cancel), new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                ((DFActionLivenessActivity) getActivity()).onErrorHappen(RESULT_CANCELED);
            }
        }).setPositiveButton(getStringWithID(R.string.restart_preview), new android.view.View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                showIndicateView();
                mProcess.registerLivenessDetectCallback(mLivenessListener);
                restartAnimationAndLiveness();
            }
        });
        if (getActivity().isFinishing()) {
            return;
        }
        mDialog.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mProcess != null) {
            mProcess.registerLivenessDetectCallback(null);
            mProcess.stopDetect();
            mProcess.exitDetect();

            mProcess = null;
        }
        if (mTimeViewContoller != null) {
            mTimeViewContoller.setCallBack(null);
            mTimeViewContoller = null;
        }
    }

    protected void startAnimation(int animation) {
        if (animation != CURRENT_ANIMATION) {
            mGvView.setMovieResource(animation);
            if (isDialogShowing()) {
                return;
            }
        }
        if (mTimeViewContoller != null) {
            mTimeViewContoller.start();
            mTimeViewContoller.setCallBack(new TimeViewContoller.CallBack() {
                @Override
                public void onTimeEnd() {
                    mProcess.onTimeEnd();
                }
            });
        }
    }

    protected void updateUi(int stringId, int animationId, int number) {
        mNoteTextView.setText(getStringWithID(stringId));
        if (animationId != 0) {
            startAnimation(animationId);
        }
        if (number >= 0) {
            View childAt = mVGBottomDots.getChildAt(number);
            childAt.setEnabled(false);
        }
    }

    private String getStringWithID(int id) {
        return getResources().getString(id);
    }

    protected SensorEventListener mSensorEventListener = new SensorEventListener() {

        @Override
        public void onAccuracyChanged(Sensor arg0, int arg1) {
        }

        @Override
        public void onSensorChanged(SensorEvent event) {
            mProcess.addSequentialInfo(event.sensor.getType(), event.values);
        }
    };

    private int dp2px(float dpValue) {
        int densityDpi = this.getResources().getDisplayMetrics().densityDpi;
        return (int) (dpValue * (densityDpi / 160));
    }

}
