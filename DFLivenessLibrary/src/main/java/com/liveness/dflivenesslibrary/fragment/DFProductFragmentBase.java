package com.liveness.dflivenesslibrary.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.camera.CameraBase;
import com.liveness.dflivenesslibrary.view.DFLivenessOverlayView;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public abstract class DFProductFragmentBase extends Fragment {

    protected SurfaceView mSurfaceView;
    protected DFLivenessOverlayView mOverlayView;
    protected View mRootView;
    protected CameraBase mCameraBase;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mRootView = inflater.inflate(getLayoutResourceId(), container,
                false);
        mSurfaceView = (SurfaceView) mRootView.findViewById(R.id.surfaceViewCamera);
        mOverlayView = (DFLivenessOverlayView) mRootView.findViewById(R.id.id_ov_mask);
        mCameraBase = new CameraBase(getActivity(),mSurfaceView, mOverlayView, isFrontCamera());
        initialize();
        return mRootView;
    }

    protected void initialize() {

    }

    protected boolean isFrontCamera() {
        return true;
    }

    protected abstract int getLayoutResourceId();

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOverlayView != null){
            mOverlayView.releaseReSource();
        }
    }
}
