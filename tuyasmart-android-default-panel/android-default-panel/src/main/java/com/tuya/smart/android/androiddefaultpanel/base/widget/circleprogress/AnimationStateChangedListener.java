package com.tuya.smart.android.demo.base.widget.circleprogress;

public interface AnimationStateChangedListener{

    /**
     * Call if animation state changes.
     * This code runs in the animation loop, so keep your code short!
     * @param _animationState
     */
    void onAnimationStateChanged(AnimationState _animationState);
}
