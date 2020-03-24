package com.liveness.dflivenesslibrary.liveness;

import com.liveness.dflivenesslibrary.R;
import com.liveness.dflivenesslibrary.fragment.DFProductFragmentBase;
import com.liveness.dflivenesslibrary.fragment.DFSilentLivenessFragment;

/**
 * Copyright (c) 2017-2019 DEEPFINCH Corporation. All rights reserved.
 **/
public class DFSilentLivenessActivity extends DFActionLivenessActivity {

    public static final String KEY_HINT_MESSAGE_HAS_FACE = "com.deepfinch.liveness.message.hasface";
    public static final String KEY_HINT_MESSAGE_NO_FACE = "com.deepfinch.liveness.message.noface";
    public static final String KEY_HINT_MESSAGE_FACE_NOT_VALID = "com.deepfinch.liveness.message.facenotvalid";

    @Override
    protected DFProductFragmentBase getFrament() {
        return new DFSilentLivenessFragment();
    }

    @Override
    protected int getTitleString() {
        return R.string.string_silent_liveness;
    }
}
