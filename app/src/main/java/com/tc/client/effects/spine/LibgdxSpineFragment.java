package com.tc.client.effects.spine;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.backends.android.AndroidFragmentApplication;
import com.badoo.mobile.util.WeakHandler;

import com.tc.client.R;
import com.tc.client.effects.base.InterceptableViewGroup;
import com.tc.client.effects.fireworks.GiftParticleContants;


/**
 * Created by QJoy on 2017.12.25.
 */
@SuppressWarnings("All")
public class LibgdxSpineFragment extends AndroidFragmentApplication implements InputProcessor {

	public static boolean openDEBUGLog = false;
    private static final String TAG = LibgdxSpineFragment.class.getSimpleName();
    private View m_viewRooter = null;
    //粒子效果UI容器层
    private InterceptableViewGroup mContainer;
    //粒子效果绘制层
    private LibgdxSpineEffectView spineEffectView;
    //Fragment 处于销毁过程中标志位
    private boolean m_isDestorying = false;
    //Fragment 处于OnStop标志位
    private boolean m_isStoping = false;
    //Screen 是否需要重建播放
    private boolean m_isNeedBuild =true;

	private boolean m_hasBuilt = false;

    private WeakHandler m_WeakHandler = new WeakHandler();

	public void setAction(String actionName){
		if (spineEffectView != null)
			spineEffectView.setAction(actionName);
	}

    public void preDestory(){

	    if (openDEBUGLog)
	        Log.d(TAG, "preDestory");

	    if (!m_hasBuilt)
		    return;

	    spineEffectView.forceOver();
	    spineEffectView.setCanDraw(false);

        m_isDestorying = true;
        m_isStoping = true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

	    if (openDEBUGLog)
	        Log.d(TAG, "onCreateView");

        m_viewRooter = inflater.inflate(R.layout.lf_layout_giftparticle, null);
        return m_viewRooter;
    }

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

		if (openDEBUGLog)
			Log.d(TAG, "onViewCreated");

		super.onViewCreated(view, savedInstanceState);
		buildGDX();
	}

    public void buildGDX(){

	    if (openDEBUGLog)
	        Log.d(TAG, "buildGDX");

        spineEffectView = new LibgdxSpineEffectView();
        View effectview = CreateGLAlpha(spineEffectView);
        mContainer = (InterceptableViewGroup) m_viewRooter.findViewById(R.id.container);
        mContainer.addView(effectview);
	    mContainer.setIntercept(true);
        Gdx.input.setInputProcessor(this);
        Gdx.input.setCatchBackKey(true);
	    m_hasBuilt = true;
    }

    @Override
    public void onStart() {

	    if (openDEBUGLog)
            Log.d(TAG, "onStart");

        m_isStoping = false;
        super.onStart();

	    if (spineEffectView != null)
	        spineEffectView.setCanDraw(true);
    }

    @Override
    public void onStop() {

	    if (openDEBUGLog)
            Log.d(TAG, "onStop");

        m_isStoping = true;
	    spineEffectView.setCanDraw(false);
        super.onStop();
    }

    @Override
    public void onResume() {

	    if (openDEBUGLog)
            Log.d(TAG, "onResume");

        super.onResume();

	    if (spineEffectView != null) {
		    spineEffectView.closeforceOver();
	    }
    }

    @Override
    public void onPause() {

	    if (openDEBUGLog)
            Log.d(TAG, "onPause");

	    if (spineEffectView != null) {
		    spineEffectView.forceOver();
	    }

	    super.onPause();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {

	    if (openDEBUGLog)
		    Log.d(TAG, "onConfigurationChanged");

        super.onConfigurationChanged(config);

        mContainer.removeAllViews();
        buildGDX();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private View CreateGLAlpha(ApplicationListener application) {

	    if (openDEBUGLog)
		    Log.d(TAG, "CreateGLAlpha");

        //	    GLSurfaceView透明相关
        AndroidApplicationConfiguration cfg = new AndroidApplicationConfiguration();
        cfg.r = cfg.g = cfg.b = cfg.a = 8;

        View view = initializeForView(application, cfg);

        if (view instanceof SurfaceView) {
            GLSurfaceView glView = (GLSurfaceView) graphics.getView();
            glView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
            glView.setZOrderMediaOverlay(true);
            glView.setZOrderOnTop(true);
        }

        return view;
    }

    @Override
    public boolean keyDown(int i) {

	    if (openDEBUGLog)
		    Log.d(TAG, "CreateGLAlpha");

        if (i == Input.Keys.BACK) {
            Intent intent = new Intent();
            intent.setAction(GiftParticleContants.BROADCAST_GIFTPARTICLE_BACKKEY);
	        if (getActivity() != null)
                getActivity().sendBroadcast(intent);
            return true;
        }
        return false;
    }

    @Override
    public boolean keyUp(int i) {
        return false;
    }

    @Override
    public boolean keyTyped(char c) {
        return false;
    }

    @Override
    public boolean touchDown(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchUp(int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int i, int i1, int i2) {
        return false;
    }

    @Override
    public boolean mouseMoved(int i, int i1) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }

    private boolean isScreenLock(){
        try {
            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
            boolean isScreenOn = pm.isScreenOn();//如果为true，则表示屏幕“亮”了，否则屏幕“暗”了。
            return !isScreenOn;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void startActivity(Intent intent) {

    }
}
