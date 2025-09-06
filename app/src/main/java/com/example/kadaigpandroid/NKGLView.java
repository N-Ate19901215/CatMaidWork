package com.example.kadaigpandroid;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class NKGLView extends GLSurfaceView {

	// 現在レンダラー内で全て処理：いずれゲーム、描画で分割する
	NKGLRenderer renderer;

	public NKGLView(Context context) {
		super(context);
		renderer = new NKGLRenderer(context);
		setRenderer( renderer );
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		float x = event.getX(); 	// X座標を取得
		float y = event.getY(); 	// Y座標を取得
		renderer.touchXraw = x;
		renderer.touchYraw = y;

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				renderer.touch = 1;
				break;
			case MotionEvent.ACTION_UP:
				renderer.touch = 0;
				break;
			case MotionEvent.ACTION_MOVE:
				break;
			case MotionEvent.ACTION_CANCEL:
				break;
		}
//        Log.d("GL TouchEvent", "X:" + event.getX() + ",Y:" + event.getY());
		return true;
	}


}
