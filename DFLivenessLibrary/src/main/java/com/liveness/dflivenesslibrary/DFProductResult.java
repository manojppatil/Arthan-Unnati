package com.liveness.dflivenesslibrary;

import com.deepfinch.liveness.DFLivenessSDK;

import java.util.ArrayList;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFProductResult {
    private ArrayList<byte[]> mResultImages; // jpeg images
    private byte[] mLivenessEncryptResult;
    private DFLivenessSDK.DFLivenessImageResult[] mLivenessImageResults;
    private byte[] mLivenessVideoResult;
    private float mQualityScore;

    public DFProductResult() {

    }


    public ArrayList<byte[]> getResultImages() {
        return mResultImages;
    }

    public void setResultImages(ArrayList<byte[]> resultImages) {
        this.mResultImages = resultImages;
    }

    public byte[] getLivenessEncryptResult() {
        return mLivenessEncryptResult;
    }

    public void setLivenessEncryptResult(byte[] livenessResult) {
        this.mLivenessEncryptResult = livenessResult;
    }

    public DFLivenessSDK.DFLivenessImageResult[] getLivenessImageResults() {
        return mLivenessImageResults;
    }

    public void setLivenessImageResults(DFLivenessSDK.DFLivenessImageResult[] imageResults) {
        this.mLivenessImageResults = imageResults;
    }

    public byte[] getLivenessVideoResult() {
        return mLivenessVideoResult;
    }

    public void setLivenessVideoResult(byte[] livenessVideoResult) {
        this.mLivenessVideoResult = livenessVideoResult;
    }

    public float getQualityScore() {
        return mQualityScore;
    }

    public void setQualityScore(float qualityScore) {
        this.mQualityScore = qualityScore;
    }
}
