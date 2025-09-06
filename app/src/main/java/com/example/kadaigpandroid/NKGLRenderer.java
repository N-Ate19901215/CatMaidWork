package com.example.kadaigpandroid;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;

import android.content.Context;
import android.graphics.Point;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.nk.AndroidBaseSystem.R;

import java.util.Random;
import java.util.*;
import java.lang.*;
import java.io.*;



public class NKGLRenderer implements Renderer {

	int Screen_Scale = 1;				// 機種が変わった場合にスクリーンに合わせるか

	// ベース（設計した）スクリーンサイズ
	int SCREEN_BASE_WIDTH = 1024;
	int SCREEN_BASE_HEIGHT = 600;

	// システム
	private Context context;
	public NKGLRenderer(Context _context) {
		context = _context;
	}
	GL10 glx;
	TexSpr SystemTexture;               // システム文字表示用テクスチャ
	TexSpr[] tex = new TexSpr[32];      // テクスチャ 最大32枚まで
	float Alpha = 1.0f;					// 半透明情報
	int Game_Proc = 0;					// ゲームプロセス
	float Screen_Scale_Width;			// 画面比率横
	float Screen_Scale_Height;			// 画面比率縦

	// 画面タッチ関連
	public float touchXraw;             // Touch X(加工前)
	public float touchYraw;             // Touch Y(加工前)

	public float touchX;                // Touch X(画面比率加工後)
	public float touchY;                // Touch Y(画面比率加工後)
	public int touch = 0;               // 画面をタッチしている(0:していない、1:している)
	public int touchTrig;			    // タッチした時のみ1


	//
	// public static double cos(double a)

	public int touchRelease;			// 指を離した時のみ1
	public int touchOld = 0;            // 1フレーム前の情報を取っておく

	float scl = 1.0f;

	//-------------------------------------------------------------------------------------
	// テクスチャを列挙する [User]
	//-------------------------------------------------------------------------------------
	final int TexAll = 31;				// 登録するテクスチャの総数

	int TexName[] =
	{
			R.drawable.back,                // 0 背景
			R.drawable.cat1,              // 1 どうぶつ＆隕石
			R.drawable.bug,					//2
			R.drawable.title,//3
			R.drawable.catj,//4

			R.drawable.catj2,//5
			R.drawable.cat2,//6
			R.drawable.niku,//7
			R.drawable.title2,//8
			R.drawable.catin,//9

			R.drawable.botan0,//10
			R.drawable.catj2miror,//11
			R.drawable.sousa0,//12
			R.drawable.sousa1,//13
			R.drawable.sousa2,//14

			R.drawable.start,//15
			R.drawable.black,//16
			R.drawable.sousa3,//17
			R.drawable.resultui,//18
			R.drawable.photo1,//19

			R.drawable.photo2,//20
			R.drawable.photo3,//21
			R.drawable.photo4,//22
			R.drawable.bachi,//23
			R.drawable.botan1,//24

			R.drawable.botan2,//25
			R.drawable.botan3,//26
			R.drawable.botan4,//27
			R.drawable.botan5,//28
			R.drawable.time,//29

			R.drawable.sousa4,//30

	};

	//-------------------------------------------------------------------------------------
	// ユーザーの変数を定義 [User]
	//-------------------------------------------------------------------------------------
	Random random = new Random();           			 // 乱数
	int rnd(int num) {
		return random.nextInt(num);
	}	 // 0～numまでの乱数を生成 (例)r = rand(100);

	float px=0,py=0;
	int catP;
	float catPx = 0;
	float catPy = 420;
	float catCX,catCY = 0;

	public float fricXb =0;					//フリック判定用X
	public float fricX=0;					//フリック判定用Y
	public float fricH = 0;					//フリック判定
	public float fric = 0;
	public double sinB = 0;
	float bug1X=0;
	double bug1Y = 0;
	double bug1T = 0;

	int bug1Tf = 1;
	float bug2X=1024;
	double bug2Y = 0;
	double bug2T = 0;

	int bug2Tf = 1;
	float bug3X=1024;
	double bug3Y = 0;
	double bug3T = 0;

	int bug3Tf = 1;

	public int catS =0;
	public float jpx =0;
	public float jpy =0;
	public float jpxh =0;
	public float jpyh =0;
	public float jpxbeast =0;
	public float jpybeast =0;
	public float Score =0;

	int r3 = 0;
	int bug1 = 0;
	int bug1s = 0;
	int bug1f = 0;
	int bug1h = 0;
	float bug1A =0;
	int bug2 = 0;
	int bug2s = 0;
	int bug2f = 0;
	int bug2h = 0;
	float bug2A =0;
	int bug3 = 0;
	int bug3s = 0;
	int bug3f = 0;
	int bug3h = 0;
	float bug3A =0;
	int ok = 0;
	float tpx ;
	float tpy;
	int beastOn ;
	int beastS;
	int[] nikus = {0,200,600,1000};
	int botan = 0;
	int ok2=0;
	float time = 0;
	float clic=0;
	int stop=0;
	float count=0;
	float countFinish=0;
	int bugDeathCount = 0;
	float resultCount = 0;
	int beastCount =0;
	float photoY ;
	float photoAnimeCount;
	int beastBugPoint;
	int sousaScreen;
	int sousaFlame =0;


	//-------------------------------------------------------------------------------------
	// アプリ初期化時に一度だけ呼ばれる
	//-------------------------------------------------------------------------------------
	public void App_Init(GL10 gl) {
		// 拡大縮小を使用する場合、元画像との比率を計算する
		if(Screen_Scale != 0)
		{
			Screen_Scale_Width = (float)MainActivity.ViewSize.x / (float)SCREEN_BASE_WIDTH;
			Screen_Scale_Height = (float)MainActivity.ViewSize.y / (float)SCREEN_BASE_HEIGHT;
		}
	}

	//-------------------------------------------------------------------------------------
	// タイトル画面
	//-------------------------------------------------------------------------------------
	public int Title_Process(GL10 gl) {
		playBGM(0);
		/*if(touchTrig==1){
			sousaScreen += 1;
		}*/
		if(sousaScreen == 6){
			return 1;
		}

		drawTexture(3,1024/2,600/2);

		nikus[0]-= 5;
		if(nikus[0] < (-30)){
			nikus[0] = 1050;
		}
		nikus[2]-= 5;
		if(nikus[2] < (-30)){
			nikus[2] = 1050;
		}
		nikus[3]-= 5;
		if(nikus[3] < (-30)){
			nikus[3] = 1050;
		}
		nikus[1]-= 5;
		if(nikus[1] < (-30)){
			nikus[1] = 1050;
		}


		drawTexture(7,nikus[0],600/2);
		drawTexture(7,nikus[1],200);
		drawTexture(7,nikus[2],700);
		drawTexture(7,nikus[3],600);

		drawTexture(8,1024/2,600/2);
if(sousaScreen ==1) {
	drawTexture(12, 1024 / 2, 600 / 2);
	if(touchTrig==1){
		sousaScreen = 0;
	}
}
		if(sousaScreen ==2){
		drawTexture(13,1024/2,600/2);}
		if(sousaScreen ==3){
		drawTexture(14,1024/2,600/2);}
			if(sousaScreen ==4){
				drawTexture(17,1024/2,600/2);}

			if(sousaScreen ==5){
		drawTexture(30,1024/2,600/2);}

			if(sousaFlame >= 1){
				sousaFlame ++;
				if(sousaFlame >= 10){
					sousaFlame = 0;
				}
			}


		int touchBox[] = {(int) touchX, (int) touchY, 76 / 2, 76 / 2};
		int startBox[] = {227,425, 210 / 2, 3 / 2};
		int start2Box[] = {290,520, 130 / 2, 1 / 2};
		int start3Box[] = {500,520, 90 / 2, 1 / 2};

		if(checkHit(touchBox,start2Box) !=0)
		{
			if(touchTrig==1 && sousaScreen == 0) {
				if(sousaFlame == 0) {
					sousaScreen = 2;
					sousaFlame += 1;
				}
			}
		}

		if(touchTrig==1 && sousaScreen ==2){
			if(sousaFlame == 0) {
				sousaScreen = 3;
				sousaFlame += 1;
			}
		}
		if(touchTrig==1 && sousaScreen ==3){
			if(sousaFlame == 0) {
				sousaScreen = 4;
				sousaFlame += 1;
			}
		}
		if(touchTrig==1 && sousaScreen ==4){
			if(sousaFlame == 0) {
				sousaScreen = 5;
				sousaFlame += 1;
			}
		}
		if(touchTrig==1 && sousaScreen ==5){
			if(sousaFlame == 0) {
				sousaScreen = 0;
				sousaFlame += 1;
			}
		}

		if(checkHit(touchBox,start3Box) !=0)
		{
			if(touchTrig==1 && sousaScreen == 0) {
				if(sousaFlame == 0) {
					sousaScreen = 1;
					sousaFlame += 1;
				}
			}
		}

		if(touchRelease==1 && sousaScreen ==1){
			if(sousaFlame == 0) {
				sousaScreen = 0;
				sousaFlame += 1;
			}
		}

		if(checkHit(touchBox,startBox) !=0)
		{
			if(touchTrig==1 && sousaScreen ==0){
				return 1;
			}
		}


		drawStr(0,0,"Px,Py("+px+","+py+")");


		return 0;
	}

	//-------------------------------------------------------------------------------------
	// ゲーム初期化
	//-------------------------------------------------------------------------------------
	public int Game_Init(GL10 gl) {

		fricXb = 0;
		fricX = 0;
		catP = 2;
		sinB = 6;
		bug1Tf = 1 ;
		bug1T = 180;
		bug1X =0;
		catS = 0;
		Score = 0;
		r3=0;
		bug1s =1;
		bug1f = 1;
		bug1h = 1;
		bug1A = -10;
		jpxh = 100000;
		jpyh = 100000;
		jpxbeast = 100000;
		jpybeast = 100000;
		ok = 0;
		tpx = 100000;
		tpy = 100000;
		beastOn = 0;
		beastS = 0;
		time = 60;
		stop = 0;
		count=0;
		countFinish = 0;
		bugDeathCount = 0;
		resultCount=0;
		beastCount =0;
		photoY = -330;
		photoAnimeCount = 0;
		beastBugPoint =0;
		sousaScreen = 0;


	return 1;
	}


	//-------------------------------------------------------------------------------------
	// ゲーム中
	//-------------------------------------------------------------------------------------
	public int Game_Process(GL10 gl) {
		if(count == 1){
		playBGM(1);}

		if (count < 4)
			count += (float) deltaTime.getDeltaTime();
		stop = 1;


		if (count < 3)
			stop = 0;

		if (stop == 1 && time >= 0) {
			time -= (float) deltaTime.getDeltaTime();
		}

		if (time < 1) {
			countFinish += (float) deltaTime.getDeltaTime();
			stop = 0;
		}
		if (countFinish >= 3) {
			return 1;

		}


		px = touchX;
		py = touchY;


		if (touchTrig == 1) {
			fricX = touchX;
		}
		if (touchRelease == 1) {
			fricXb = touchX;
			if (fricXb == 0) {
				fricXb = fricX;
			}
			fricH = fricXb - fricX;
			if (fricH > -20 && fricH < 20) {
				if (stop == 1) {
					fric = 3;
					fricH = 0;
					fricX = 0;
					fricXb = 0;
					//playSE(0);
				}

			}


			if (fricH >= 200 && stop == 1) {
				fric = 1;
				fricH = 0;
				fricX = 0;
				fricXb = 0;
			}

			if (fricH <= -200 && stop == 1) {
				fric = 2;
				fricH = 0;
				fricX = 0;
				fricXb = 0;
			}

		}


		if (fric == 1) {
			catP += 1;
			fric = 0;
			if (catP > 3)
				catP = 3;
		}
		if (fric == 2) {
			catP -= 1;
			fric = 0;
			if (catP < 1)
				catP = 1;
		}


		switch (catP) {
			case 1: {
				catPx = 120;
				if (px < 342)
					ok = 1;
				else
					ok = 0;
				break;
			}

			case 2: {
				catPx = 512;
				if (px > 342 && px < 684)
					ok = 1;
				else
					ok = 0;
				break;
			}

			case 3: {
				catPx = 1024 - 120;
				if (px > 684)
					ok = 1;
				else
					ok = 0;
				break;
			}
		}

		if (beastOn >= 1000) {
			catP = 2;
			beastOn = 0;
		}
		if (beastOn >= 1) {
			beastOn++;
		}
		if (beastOn >= 1) {
			ok = 5;
		}
		if (beastOn >= 2 && beastOn < 3) {
			beastBugPoint = 0;
			beastCount++;
		}


		//虫3種類

		if (bug1X > 1024) {
			bug1 = 0;
		}


		bug1++;
		if (bug1 == 1) {
			bug1X = 0;
			bug1Y = 0;
			Random random = new Random();
			r3 = random.nextInt(3) + 1;
			bug1s = r3;
			r3 = random.nextInt(3) + 1;
			bug1f = r3;
			r3 = random.nextInt(3) + 1;
			bug1h = r3;
			r3 = random.nextInt(3) + 1;
			bug1h = r3;
		}


		switch (bug1s) {
			case 1: {
				bug1X += 8;
				break;
			}
			case 2: {
				bug1X += 4;
				break;
			}
			case 3: {
				bug1X += 2;
				break;
			}
		}


		switch (bug1f) {
			case 1: {
				switch (bug1Tf) {

					case 1: {
						bug1T += 9;
						if (bug1T > 180)
							bug1Tf = 2;
						break;
					}

					case 2: {
						bug1T -= 9;
						if (bug1T <= 0)
							bug1Tf = 1;
						break;
					}
				}
				break;
			}
			case 2: {
				switch (bug1Tf) {

					case 1: {
						bug1T += 6;
						if (bug1T > 180)
							bug1Tf = 2;
						break;
					}

					case 2: {
						bug1T -= 6;
						if (bug1T <= 0)
							bug1Tf = 1;
						break;
					}
				}
				break;
			}
			case 3: {
				switch (bug1Tf) {

					case 1: {
						bug1T += 14;
						if (bug1T > 180)
							bug1Tf = 2;
						break;
					}

					case 2: {
						bug1T -= 14;
						if (bug1T <= 0)
							bug1Tf = 1;
						break;
					}
				}
				break;
			}
		}
		switch (bug1h) {
			case 1: {
				bug1Y = Math.sin(bug1T * (3.141592f * 180.0f)) * 12000 + 200;
				break;
			}
			case 2: {
				bug1Y = Math.sin(bug1T * (3.141592f * 180.0f)) * 12000 + 280;
				break;
			}
			case 3: {
				bug1Y = Math.sin(bug1T * (3.141592f * 180.0f)) * 12000 + 350;
				break;
			}
			case 4: {
				bug1Y += 8;
				break;
			}
		}

		if (bug1 < 0) {
			bug1h = 4;
			bug1X = bug1A;
		}

//虫2
		if (bug2X < 0) {
			bug2 = 0;
		}
		bug2++;
		if (bug2 == 1) {
			bug2X = 1024;
			bug2Y = 0;
			Random random = new Random();
			r3 = random.nextInt(3) + 1;
			bug2s = r3;
			r3 = random.nextInt(3) + 1;
			bug2f = r3;
			r3 = random.nextInt(3) + 1;
			bug2h = r3;
			r3 = random.nextInt(3) + 1;
			bug2h = r3;
		}


		switch (bug2s) {
			case 1: {
				bug2X -= 8;
				break;
			}
			case 2: {
				bug2X -= 4;
				break;
			}
			case 3: {
				bug2X -= 2;
				break;
			}
		}


		switch (bug2f) {
			case 1: {
				switch (bug2Tf) {

					case 1: {
						bug2T += 9;
						if (bug2T > 180)
							bug2Tf = 2;
						break;
					}

					case 2: {
						bug2T -= 9;
						if (bug2T <= 0)
							bug2Tf = 1;
						break;
					}
				}
				break;
			}
			case 2: {
				switch (bug2Tf) {

					case 1: {
						bug2T += 6;
						if (bug2T > 180)
							bug2Tf = 2;
						break;
					}

					case 2: {
						bug2T -= 6;
						if (bug2T <= 0)
							bug2Tf = 1;
						break;
					}
				}
				break;
			}
			case 3: {
				switch (bug2Tf) {

					case 1: {
						bug2T += 14;
						if (bug2T > 180)
							bug2Tf = 2;
						break;
					}

					case 2: {
						bug2T -= 14;
						if (bug2T <= 0)
							bug2Tf = 1;
						break;
					}
				}
				break;
			}
		}
		switch (bug2h) {
			case 1: {
				bug2Y = Math.sin(bug2T * (3.141592f * 180.0f)) * 12000 + 200;
				break;
			}
			case 2: {
				bug2Y = Math.sin(bug2T * (3.141592f * 180.0f)) * 12000 + 280;
				break;
			}
			case 3: {
				bug2Y = Math.sin(bug2T * (3.141592f * 180.0f)) * 12000 + 350;
				break;
			}
			case 4: {
				bug2Y += 8;
				break;
			}
		}

		if (bug2 < 0) {
			bug2h = 4;
			bug2X = bug2A;
		}

		//3匹目
		if (bug3X > 1024) {
			bug3 = 0;
		}


		bug3++;
		if (bug3 == 1) {
			bug3X = 0;
			bug3Y = 0;
			Random random = new Random();
			r3 = random.nextInt(3) + 1;
			bug3s = r3;
			r3 = random.nextInt(3) + 1;
			bug3f = r3;
			r3 = random.nextInt(3) + 1;
			bug3h = r3;
		}


		switch (bug3s) {
			case 1: {
				bug3X += 8;
				break;
			}
			case 2: {
				bug3X += 4;
				break;
			}
			case 3: {
				bug3X += 2;
				break;
			}
		}


		switch (bug3f) {
			case 1: {
				switch (bug3Tf) {

					case 1: {
						bug3T += 9;
						if (bug3T > 180)
							bug3Tf = 2;
						break;
					}

					case 2: {
						bug3T -= 9;
						if (bug3T <= 0)
							bug3Tf = 1;
						break;
					}
				}
				break;
			}
			case 2: {
				switch (bug3Tf) {

					case 1: {
						bug3T += 6;
						if (bug3T > 180)
							bug3Tf = 2;
						break;
					}

					case 2: {
						bug3T -= 6;
						if (bug3T <= 0)
							bug3Tf = 1;
						break;
					}
				}
				break;
			}
			case 3: {
				switch (bug3Tf) {

					case 1: {
						bug3T += 14;
						if (bug3T > 180)
							bug3Tf = 2;
						break;
					}

					case 2: {
						bug3T -= 14;
						if (bug3T <= 0)
							bug3Tf = 1;
						break;
					}
				}
				break;
			}
		}
		switch (bug3h) {
			case 1: {
				bug3Y = Math.sin(bug3T * (3.141592f * 180.0f)) * 12000 + 200;
				break;
			}
			case 2: {
				bug3Y = Math.sin(bug3T * (3.141592f * 180.0f)) * 12000 + 280;
				break;
			}
			case 3: {
				bug3Y = Math.sin(bug3T * (3.141592f * 180.0f)) * 12000 + 350;
				break;
			}
			case 4: {
				bug3Y += 8;
				break;
			}
		}

		if (bug3 < 0) {
			bug3h = 4;
			bug3X = bug3A;
		}


		drawTexture(0, 1024 / 2, 600 / 2);        // 中央に配置

		if (catS == 0 && beastOn == 0) {
			drawTextureUV(1, (int) catPx, (int) catPy + 50, 0, 0, 262, 285);
		}
		if (catS == 0 && beastOn >= 1) {
			drawTextureUV(6, (int) 512, (int) catPy + 30, 0, 0, 262, 305);
		}
		if (beastOn >= 1 && beastOn <= 10) {
			fric = 0;
		}

		if (clic == 0) {
			if (fric == 3) {
				if (ok == 1 || ok == 5) {
					if (catS == 0) {
						jpx = px;
						jpxh = px;
						jpy = py;
						jpyh = py;
						if (beastOn > 0) {
							jpxbeast = px;
							jpybeast = py;
						}
					}
					catS++;
					fric = 0;
				}
			}
		}

		botan = 0;
		if (touchRelease == 1 && clic == 0) {
			clic += 1;
		}
		if (clic >= 1) {
			clic++;
			if (clic >= 50)
				clic = 0;
		}
		tpx = 10000;
		tpy = 10000;
		if (fric == 3) {
			tpx = px;
			tpy = py;
		}
		if (jpx > 600) {
			ok2 = 1;
		} else ok2 = 0;


		if (catS >= 1) {
			catS++;
			if (beastOn == 0) {
				drawTextureUV(4, -30 + (int) jpx + 330 / 2, -70 + (int) jpy + 560 / 2, 0, 0, 330, 560);
			}
			if (beastOn >= 1 && ok2 == 0) {
				drawTextureUV(5, -100 + (int) jpx + 420 / 2, -150 + (int) jpy + 560 / 2, 0, 0, 420, 560);
			}
			if (beastOn >= 1 && ok2 == 1) {
				drawTextureUV(11, +100 + (int) jpx - 420 / 2, -150 + (int) jpy + 560 / 2, 0, 0, 420, 560);
			}

			if (catS > 10) {
				jpxh = 100000;
				jpyh = 100000;
				jpxbeast = 100000;
				jpybeast = 100000;
				tpx = 100000;
				tpy = 100000;
			}
			if (catS > 50)
				catS = 0;
		}


		drawTextureUV(2, (int) bug1X, (int) bug1Y, 0, 0, 107, 160);
		drawTextureUV(2, (int) bug2X, (int) bug2Y, 0, 0, 107, 160);
		drawTextureUV(2, (int) bug3X, (int) bug3Y, 0, 0, 107, 160);
		if (beastOn <= 50 && beastOn >= 1) {
			botan = 1;
			drawTexture(9, 1024 / 2, 600 / 2);
		}
		if (beastOn > 0) {
			drawTexture(23, 1024 / 2, 600 / 2);
		}
		drawTexture(10, 1024 / 2, 600 / 2);//ビーストモード
		if (beastBugPoint >= 150) {
			drawTexture(24, 1024 / 2, 600 / 2);
		}
		if (beastBugPoint >= 300) {
			drawTexture(25, 1024 / 2, 600 / 2);
		}
		if (beastBugPoint >= 450) {
			drawTexture(26, 1024 / 2, 600 / 2);
		}
		if (beastBugPoint >= 600) {
			drawTexture(27, 1024 / 2, 600 / 2);
		}
		if (beastBugPoint >= 750) {
			drawTexture(28, 1024 / 2, 600 / 2);
		}

		drawTexture(29, 1024 / 2, 600 / 2);



		drawStr(800, 0, "" + (int) time + "");

		if (count <= 3 || stop == 0)
			drawTexture(16, 1024 / 2, 600 / 2);



		if (count >= 0 && count < 1) {
			drawTextureUV(15, 1050 / 2, 600 / 2,2400,0,300,600);
		}
		if (count >= 1 && count < 2) {
			drawTextureUV(15, 1050 / 2, 600 / 2,2070,0,300,600);
		}
		if (count >= 2 && count < 3) {
			drawTextureUV(15, 1001 / 2, 600 / 2,1700,0,300,600);
		}
		if (count >= 3 && count < 4) {
			drawTextureUV(15, 1050 / 2, 600 / 2,950,0,800,600);
		}
		if (countFinish > 0) {
			drawTextureUV(15, 1050 / 2, 600 / 2,125,0,800,600);
		}


		// 当たり判定用のBOXを作成する
		int animalBox[] = {0 + (int) px, 3 + (int) py, 34 / 2, 60 / 2};
		int inseBox[] = {1000, 300, 100 / 2, 100 / 2};
		int bug1Box[] = {+90 + (int) bug1X, +93 + (int) bug1Y, 65 / 2, 46 / 2};
		int bug2Box[] = {+90 + (int) bug2X, +93 + (int) bug2Y, 65 / 2, 46 / 2};
		int bug3Box[] = {+90 + (int) bug3X, +93 + (int) bug3Y, 65 / 2, 46 / 2};
		int beastBox[] = {50, 100, 100 / 2, 100 / 2};
		int handBox[] = {-100 + (int) jpxh + 420 / 2, -150 + (int) jpyh + 560 / 2, 50 / 2, 50 / 2};
		int beastHandBox[] = {-20 + (int) jpxbeast + 240 / 2, -60 + (int) jpybeast + 375 / 2, 76 / 2, 276 / 2};
		int touchBox[] = {(int) tpx, (int) tpy, 76 / 2, 76 / 2};
		int poseBox[] = {950, 100, 100 / 2, 100 / 2};


		if (stop == 1) {
			if (checkHit(handBox, bug1Box) != 0 && bug1 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug1s == 1) {
					Score += 100;
					beastBugPoint += 100;
				}
				if (bug1s == 2) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug1s == 3) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug1f == 1) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug1f == 2) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug1f == 3) {
					Score += 100;
					beastBugPoint += 100;
				}
				bug1 = -100;
				bug1A = bug1X;
			}
			if (checkHit(handBox, bug2Box) != 0 && bug2 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug2s == 1) {
					Score += 100;
					beastBugPoint += 100;
				}
				if (bug2s == 2) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug2s == 3) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug2f == 1) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug2f == 2) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug2f == 3) {
					Score += 100;
					beastBugPoint += 100;
				}
				bug2 = -100;
				bug2A = bug2X;
			}
			if (checkHit(handBox, bug3Box) != 0 && bug3 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug3s == 1) {
					Score += 100;
					beastBugPoint += 100;
				}
				if (bug3s == 2) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug3s == 3) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug3f == 1) {
					Score += 50;
					beastBugPoint += 50;
				}
				if (bug3f == 2) {
					Score += 30;
					beastBugPoint += 30;
				}
				if (bug3f == 3) {
					Score += 100;
					beastBugPoint += 100;
				}
				bug3 = -100;
				bug3A = bug3X;
			}

			if (checkHit(beastHandBox, bug1Box) != 0 && bug1 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug1s == 1) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				if (bug1s == 2) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug1s == 3) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug1f == 1) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug1f == 2) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug1f == 3) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				bug1 = -100;
				bug1A = bug1X;
			}
			if (checkHit(beastHandBox, bug2Box) != 0 && bug2 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug2s == 1) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				if (bug2s == 2) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug2s == 3) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug2f == 1) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug2f == 2) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug2f == 3) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				bug2 = -100;
				bug2A = bug2X;
			}
			if (checkHit(beastHandBox, bug3Box) != 0 && bug3 > 0) {
				beastS += 1;
				bugDeathCount += 1;
				if (bug3s == 1) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				if (bug3s == 2) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug3s == 3) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug3f == 1) {
					Score += 50;
					beastBugPoint += 50 / 7;
				}
				if (bug3f == 2) {
					Score += 30;
					beastBugPoint += 30 / 7;
				}
				if (bug3f == 3) {
					Score += 100;
					beastBugPoint += 100 / 7;
				}
				bug3 = -100;
				bug3A = bug3X;
			}


			if (checkHit(touchBox, beastBox) != 0 && beastS >= 0) {
				if (beastBugPoint >= 750) {
					beastOn += 1;
					//beastCount += 1;
					beastS = 0;
				}
			}
			if(checkHit(touchBox,poseBox) !=0 && stop == 1)
			{
				
			}
		}


		// 画面の情報を表示（デバッグ用）
		/*int tx = (int)touchX;
		int ty = (int)touchY;
		drawStr(0,0,"catPx,Py("+catPx+","+catPy+")");
		drawStr(0,32,"TouchXY("+tx+","+ty+")");
		if(fric==1) {
			drawStr(0, 64, "fric!!!!!");
			fric = 0;
		}
		drawStr(0,128,"("+beastOn+")");
		drawStr(0,150,"("+Score+")");*/
		//bugDeathCount = 9;
		return 0;
	}

	//-------------------------------------------------------------------------------------
	// リザルト
	//-------------------------------------------------------------------------------------
	public int GameOver_Process(GL10 gl) {

		if(touchTrig==1 && resultCount > 7){
			return 1;
		}

		resultCount += (float) deltaTime.getDeltaTime();

		drawTexture(3,1024/2,600/2);
		nikus[0]-= 5;
		if(nikus[0] < (-30)){
			nikus[0] = 1050;
		}
		nikus[2]-= 5;
		if(nikus[2] < (-30)){
			nikus[2] = 1050;
		}
		nikus[3]-= 5;
		if(nikus[3] < (-30)){
			nikus[3] = 1050;
		}
		nikus[1]-= 5;
		if(nikus[1] < (-30)){
			nikus[1] = 1050;
		}

		drawTexture(7,nikus[0],600/2);
		drawTexture(7,nikus[1],200);
		drawTexture(7,nikus[2],700);
		drawTexture(7,nikus[3],600);


		drawTexture(18,1024/2,600/2);

		if(resultCount >= 5) {
			photoAnimeCount++;
			if (photoAnimeCount > 0 && photoAnimeCount <= 9) {
				if (photoY <= 250)
					photoY += 70;
			}
			if (photoAnimeCount > 9 && photoAnimeCount <= 20) {
				photoY -= 3;
			}
			if (photoAnimeCount > 15 && photoAnimeCount <= 30) {
				photoY += 2;
			}
		}
if(bugDeathCount>=30) {
	drawTexture(19, 1100 / 2, (int) photoY);
}
if(bugDeathCount < 20) {
	drawTexture(20, 1100 / 2, (int) photoY);
}
		if(bugDeathCount >=20 && bugDeathCount < 30) {
			drawTexture(21, 1100 / 2, (int) photoY);
		}
		if(bugDeathCount >= 40 ) {
			if (beastCount >= 3) {
				drawTexture(22, 1100 / 2, (int) photoY);
			}
		}

		if(resultCount >= 2){
		drawStr(300,220,""+bugDeathCount+"");}
		if(resultCount >= 3){
		drawStr(350,390,""+beastCount+"");}

		drawStr(660,+30+(int)photoY,""+(int)Score+"");


		return 0;
	}














	//------------------------------------------------------------------------------
	// 以下システム部分：設計、構築中、改造自由
	//------------------------------------------------------------------------------
	//------------------
	// テクスチャ描画処理
	//------------------
	public void drawTexture(int texno, int Px, int Py) {

		if(Screen_Scale != 0)
		{
			drawTextureScaleSys(texno,Px,Py,Screen_Scale_Width,Screen_Scale_Height);
		}
		else {
			//テクスチャ0番をアクティブにする
			glx.glActiveTexture(GL10.GL_TEXTURE0);
			//テクスチャIDに対応するテクスチャをバインドする
			glx.glBindTexture(GL10.GL_TEXTURE_2D, tex[texno].TexSprNo);
			//テクスチャの座標と幅と高さを指定
			int[] rect = {tex[texno].texX, tex[texno].texY + tex[texno].texHeight, tex[texno].texWidth, -tex[texno].texHeight};

			// 座標を中心座標に変換
			Px -= (tex[texno].texWidth / 2);
			Py -= (tex[texno].texHeight / 2);

			//描画 拡大縮小あり、回転無し
			((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
			((GL11Ext) glx).glDrawTexfOES(Px, MainActivity.ViewSize.y - (Py + tex[texno].texHeight), 0, tex[texno].texWidth, tex[texno].texHeight);
		}
	}

	public void drawTextureScale(int texno, int Px, int Py,float scaleW,float scaleH) {
		//テクスチャ0番をアクティブにする
		glx.glActiveTexture(GL10.GL_TEXTURE0);
		//テクスチャIDに対応するテクスチャをバインドする
		glx.glBindTexture(GL10.GL_TEXTURE_2D, tex[texno].TexSprNo);
		//テクスチャの座標と幅と高さを指定
		int[] rect = {tex[texno].texX, tex[texno].texY + tex[texno].texHeight, tex[texno].texWidth, -tex[texno].texHeight};

		scaleW *= Screen_Scale_Width;
		scaleH *= Screen_Scale_Height;

		// 座標を中心座標に変換
		float pfx,pfy;

		pfx = (float)Px;
		pfy = (float)Py;

		pfx -= (float)(tex[texno].texWidth/2)*scaleW;
		pfy -= (float)(tex[texno].texHeight/2)*scaleH;

		Px = (int)pfx;
		Py = (int)pfy;

		float sx = (float)tex[texno].texWidth * scaleW;
		float sy = (float)tex[texno].texHeight * scaleH;

		//描画 拡大縮小あり、回転無し
		((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
		((GL11Ext) glx).glDrawTexfOES( Px, MainActivity.ViewSize.y - (Py+tex[texno].texHeight*scaleH), 0, sx, sy);
	}

	public void drawTextureScaleSys(int texno, int Px, int Py,float scaleW,float scaleH) {

		//テクスチャ0番をアクティブにする
		glx.glActiveTexture(GL10.GL_TEXTURE0);
		//テクスチャIDに対応するテクスチャをバインドする
		glx.glBindTexture(GL10.GL_TEXTURE_2D, tex[texno].TexSprNo);
		//テクスチャの座標と幅と高さを指定
		int[] rect = {tex[texno].texX, tex[texno].texY + tex[texno].texHeight, tex[texno].texWidth, -tex[texno].texHeight};

		// 座標を中心座標に変換
		float pfx,pfy;

		pfx = (float)Px;
		pfy = (float)Py;

		pfx -= (float)(tex[texno].texWidth/2);
		pfy -= (float)(tex[texno].texHeight/2);

		pfx *= scaleW;
		pfy *= scaleH;

		Px = (int)pfx;
		Py = (int)pfy;

		float sx = (float)tex[texno].texWidth * scaleW;
		float sy = (float)tex[texno].texHeight * scaleH;

		//描画 拡大縮小あり、回転無し
		((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
		((GL11Ext) glx).glDrawTexfOES( Px, MainActivity.ViewSize.y - (Py+tex[texno].texHeight*scaleH), 0, sx, sy);
	}

	public void drawTextureUV(int texno,int Px,int Py,int u,int v,int w,int h){
		if(Screen_Scale != 0)
		{
			drawTextureUVScaleSys(texno,Px,Py,u,v,w,h,Screen_Scale_Width,Screen_Scale_Height);
		}
		else {

			//テクスチャ0番をアクティブにする
			glx.glActiveTexture(GL10.GL_TEXTURE0);
			//テクスチャIDに対応するテクスチャをバインドする
			glx.glBindTexture(GL10.GL_TEXTURE_2D, tex[texno].TexSprNo);

			//テクスチャの座標と幅と高さを指定
			int[] rect = {u, v + h, w, -h};

			// 座標を中心座標に変換
			Px -= w / 2;
			Py -= h / 2;

			//描画 拡大縮小あり、回転無し
			((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
			((GL11Ext) glx).glDrawTexfOES(Px, MainActivity.ViewSize.y - (Py + h), 0, w, h);
		}
	}

	public void drawTextureUVScaleSys(int texno,int Px,int Py,int u,int v,int w,int h,float scaleW,float scaleH){
		//テクスチャ0番をアクティブにする
		glx.glActiveTexture(GL10.GL_TEXTURE0);
		//テクスチャIDに対応するテクスチャをバインドする
		glx.glBindTexture(GL10.GL_TEXTURE_2D, tex[texno].TexSprNo);
		//テクスチャの座標と幅と高さを指定
		int[] rect = { u,  v+h,  w, -h};
		// 座標を中心座標に変換
		float pfx,pfy;

		pfx = (float)Px;
		pfy = (float)Py;

		pfx -= (float)(w/2);
		pfy -= (float)(h/2);

		pfx *= scaleW;
		pfy *= scaleH;

		Px = (int)pfx;
		Py = (int)pfy;

		float sx = (float)w * scaleW;
		float sy = (float)h * scaleH;

//		Log.d("PX", "X:" + Px + ",Y:" + Py+" SX="+sx+",SY="+sy+" SCW="+scaleW+",ScH="+scaleH);

		//描画 拡大縮小あり、回転無し
		((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
		((GL11Ext) glx).glDrawTexfOES( Px, MainActivity.ViewSize.y - (Py+h*scaleH), 0, sx, sy);
	}

	void printDec(int x, int y, int dec) {
		String s;
		s = Integer.valueOf(dec).toString();
		drawStr(x, y, s);
	}

	//---------------------------------------------
	// 画面への文字表示
	//---------------------------------------------
	public void drawStr(int Px, int Py, String s) {

		// アスキーコードの文字を表示するためのテクスチャ座標
		int[] AsciiCode = {
				71, 220, 12, 54, 0, 220, 14, 54, 270, 165, 16, 54, 271, 0, 29, 54, 483, 55, 22, 54, 38, 0, 37, 54, 421, 0, 28, 54, 94, 220, 9, 54,
				372, 165, 16, 54, 355, 165, 16, 54, 0, 110, 22, 54, 331, 0, 29, 54, 30, 220, 13, 54, 321, 165, 16, 54, 44, 220, 13, 54, 406, 165, 16, 54,
				23, 110, 22, 54, 46, 110, 22, 54, 69, 110, 22, 54, 92, 110, 22, 54, 115, 110, 22, 54, 138, 110, 22, 54, 253, 110, 22, 54, 161, 110, 22, 54,
				184, 110, 22, 54, 207, 110, 22, 54, 472, 165, 15, 54, 456, 165, 15, 54, 391, 0, 29, 54, 211, 0, 29, 54, 241, 0, 29, 54, 193, 165, 19, 54,
				0, 0, 37, 54, 191, 55, 24, 54, 216, 55, 24, 54, 241, 55, 24, 54, 450, 0, 27, 54, 230, 110, 22, 54, 66, 165, 21, 54, 111, 55, 26, 54,
				28, 55, 27, 54, 440, 165, 15, 54, 338, 165, 16, 54, 316, 55, 24, 54, 88, 165, 20, 54, 180, 0, 30, 54, 56, 55, 27, 54, 478, 0, 27, 54,
				0, 165, 21, 54, 0, 55, 27, 54, 84, 55, 26, 54, 341, 55, 23, 54, 413, 55, 23, 54, 138, 55, 26, 54, 165, 55, 25, 54, 76, 0, 36, 54,
				291, 55, 24, 54, 365, 55, 23, 54, 389, 55, 23, 54, 287, 165, 16, 54, 287, 165, 16, 54, 304, 165, 16, 54, 301, 0, 29, 54, 266, 55, 24, 54,
				276, 110, 22, 54, 22, 165, 21, 54, 299, 110, 22, 54, 232, 165, 18, 54, 322, 110, 22, 54, 44, 165, 21, 54, 488, 165, 14, 54, 345, 110, 22, 54,
				437, 110, 22, 54, 84, 220, 9, 54, 58, 220, 12, 54, 109, 165, 20, 54, 104, 220, 9, 54, 113, 0, 35, 54, 368, 110, 22, 54, 483, 110, 21, 54,
				391, 110, 22, 54, 414, 110, 22, 54, 389, 165, 16, 54, 213, 165, 18, 54, 15, 220, 14, 54, 460, 110, 22, 54, 130, 165, 20, 54, 149, 0, 30, 54,
				151, 165, 20, 54, 172, 165, 20, 54, 251, 165, 18, 54, 437, 55, 22, 54, 423, 165, 16, 54, 460, 55, 22, 54, 361, 0, 29, 54,
		};

		glx.glActiveTexture(GL10.GL_TEXTURE0);
		glx.glBindTexture(GL10.GL_TEXTURE_2D, SystemTexture.TexSprNo);

		char c;
		int idx;
		int len = s.length();
		int u, v, w, h;

		for (int i = 0; i != len; i++) {
			c = s.charAt(i);
			if ((c >= ' ') && (c <= 126)) {
				c -= ' ';
				idx = c * 4;
				u = AsciiCode[idx];
				v = AsciiCode[idx + 1];
				w = AsciiCode[idx + 2];
				h = AsciiCode[idx + 3];
				int[] rect = {u, v + h, w, -h};
				((GL11) glx).glTexParameteriv(GL10.GL_TEXTURE_2D, GL11Ext.GL_TEXTURE_CROP_RECT_OES, rect, 0);
				((GL11Ext) glx).glDrawTexfOES(Px, MainActivity.ViewSize.y - (Py + h), 0, w, h);
				Px += AsciiCode[idx + 2];
			}
		}
	}

	// 当たり判定チェック（参考)

	int checkHit(int[] a,int[] b)
	{
		// a の x と b の x の差を絶対値で出す
		int z = Math.abs(a[0] - b[0]);
		if(z < (a[2]+b[2]))
		{
			z = Math.abs(a[1] - b[1]);
			if(z < (a[3]+b[3]))
			{
				return 1;
			}
		}
		return 0;
	}

	// 画面のサイズをデバッグ表示する
	void printDisplaySize()
	{
		drawStr(0,32*2,"DisplaySize("+MainActivity.DisplaySize.x+","+MainActivity.DisplaySize.y+")");
		drawStr(0,32*3,"RealSize("+MainActivity.RealSize.x+","+MainActivity.RealSize.y+")");
		drawStr(0,32*4,"ViewSize("+MainActivity.ViewSize.x+","+MainActivity.ViewSize.y+")");
	}

	// BGMの再生
	void playBGM(int num) {
		MainActivity.snd.playBGM(num);
	}
	void stopBGM() {
		MainActivity.snd.stopBGM();
	}
	void playSE(int num) {
		MainActivity.snd.playSE(num);
	}

	///-------------------------------------------------------------------------------------
	// 初期化処理
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		glx = gl;
		//背景色をクリア
		gl.glClearColor(0, 0, 0, 0);
		//ディザを無効化
		gl.glDisable(GL10.GL_DITHER);
		//深度テストを有効化
		gl.glEnable(GL10.GL_DEPTH_TEST);
		//テクスチャ機能ON
		gl.glEnable(GL10.GL_TEXTURE_2D);
		//透明可能に
		gl.glEnable(GL10.GL_ALPHA_TEST);
		//ブレンド可能に
		gl.glEnable(GL10.GL_BLEND);
		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		// システムで表示するためのテクスチャを登録
		SystemTexture = new TexSpr();
		SystemTexture.setTexSpr(gl, context.getResources(), R.drawable.sys0);

		// 全てのテクスチャを読み込む
		for (int i = 0; i != TexAll; i++) {
			tex[i] = new TexSpr();
			tex[i].setTexSpr(gl, context.getResources(), TexName[i]);
		}

		// 起動直後の処理を行う
		App_Init(gl);
		deltaTime.calcBeginTime();

	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		gl.glViewport(0, 0, width,height);
		gl.glMatrixMode(GL10.GL_PROJECTION);				//プロジェクションモードに設定
		GLU.gluOrtho2D(gl, 0.0f, width,0,height);//平行投影用のパラメータをセット
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		// スクリーン拡大縮小している場合、座標を加工する
		touchX = touchXraw / Screen_Scale_Width;
		touchY = touchYraw / Screen_Scale_Height;

		// タッチ処理の加工
		if(touch != 0)
			touchTrig = touch ^ touchOld;
		else
			touchTrig = 0;
		touchRelease = 0;
		if((touchOld != 0)&&(touch == 0))touchRelease = 1;
		touchOld = touch;

		// 描画用バッファをクリア
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glColor4f(1.0f,1.0f,1.0f,1.0f);

		deltaTime.calcDeltaTime();

		switch (Game_Proc) {
			case 0:
				if (Title_Process(gl) != 0) {
					Game_Proc = 1;
					Game_Init(gl);
				}
				break;
			case 1:
				int temp = Game_Process(gl);
				if (temp != 0) {
					Game_Proc = 1 + temp;
				}
				break;
			case 2:
				if (GameOver_Process(gl) != 0) {
					Game_Proc = 0;
				}
				break;
		}
	}

}

// 更新履歴
//　Ver0.1 GP13回目 初回バージョン
//　Ver0.2 GP15回目 VewSizeを修正、HITBOXを追加
