package com.example.kadaigpandroid;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Build;

import com.nk.AndroidBaseSystem.R;

//
// サウンドプレイヤー
// BGM:MediaPlayer
// SE:SoundPool
//
public class SoundPlayer {

    // 読み込むサウンドのIDと数
    // BGM
    // 再生するBGMを列挙
    int BgmFile[] =
            {
                    R.raw.title,
                    R.raw.game,
            };

    // SE
    // 再生する効果音を列挙。メモリに全て読み込んでおくため総数全体数が必要。
    static final int AllSE = 1;    //

    // リソース登録してあるSEファイルを列挙する
    int SEDataTable[] =
    {
            R.raw.kettei,
    };

    // SEに関するインスタンス、配列
    private static SoundPool soundPool;
    private static int[] SeSound = new int[AllSE];

    private Context mContext;
    private static MediaPlayer mediaPlayer=null;

    private AudioAttributes audioAttributes;

    public SoundPlayer(Context context) {

        mContext = context;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            audioAttributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();

            soundPool = new SoundPool.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setMaxStreams(2)
                    .build();

        } else {
            soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        }

        // SEを読み込む
        for (int i = 0; i < AllSE; i++){
            SeSound[i] = soundPool.load(context, SEDataTable[i], 1);
        }
    }
    public void playSE(int z) {
        soundPool.play(SeSound[z], 1.0f, 1.0f, 1, 0, 1.0f);
    }

    //-------------
    // BGM
    //-------------
    public void playBGM(int num){

        if (mediaPlayer != null) {
            stopBGM();
        }

        // rawにファイルがある場合
        mediaPlayer = MediaPlayer.create(mContext, BgmFile[num]);
        mediaPlayer.start();

        // 音量調整を端末のボタンに任せる
//        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    public void stopBGM()
    {
        if (mediaPlayer == null) {
            return;
        }
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
        }
        mediaPlayer.release();
        mediaPlayer = null;
    }

}