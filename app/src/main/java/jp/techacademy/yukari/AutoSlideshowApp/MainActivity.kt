package jp.techacademy.yukari.AutoSlideshowApp

import android.Manifest
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.content.ContentUris
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import android.os.Handler
import android.view.View
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    val PERMISSIONS_REQUEST_CODE = 100
    private var mTimer: Timer? = null
    private var mHandler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


// Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSIONS_REQUEST_CODE
                )
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                } else {
                    val view = findViewById<View>(android.R.id.content)
                    Snackbar.make(view, "許可してください", Snackbar.LENGTH_LONG).setAction("Action") {}
                        .show()
                    start_button.isClickable = false
                    reset_button.isClickable = false
                    pause_button.isClickable = false
                }
        }

    }


    private fun getContentsInfo() {
        // 画像の情報を取得する
        val resolver = contentResolver
        val cursor = resolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
            null, // 項目（null = 全項目）
            null, // フィルタ条件（null = フィルタなし）
            null, // フィルタ用パラメータ
            null // ソート (nullソートなし）
        )
        if (cursor!!.moveToFirst()) {

// indexからIDを取得し、そのIDから画像のURIを取得する
            val fieldIndex =
                cursor.getColumnIndex(MediaStore.Images.Media._ID)   //データの中から画像のIDがセットされている位置を取得
            val id = cursor.getLong(fieldIndex) //画像ID取得
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }

        start_button.setOnClickListener {    //スタート

            if (mTimer == null) {
                pause_button.isClickable = false
                reset_button.isClickable = false
                start_button.text = "停止"
                mTimer = Timer()
                mTimer!!.schedule(object : TimerTask() {
                    override fun run() {

                        mHandler.post {
                            if (cursor.moveToNext() == false) {
                                cursor.moveToFirst()
                            }
                            val fieldIndex =
                                cursor.getColumnIndex(MediaStore.Images.Media._ID)   //データの中から画像のIDがセットされている位置を取得
                            val id = cursor.getLong(fieldIndex) //画像ID取得
                            val imageUri =
                                ContentUris.withAppendedId(
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                    id
                                )
                            imageView.setImageURI(imageUri)


                        }
                    }
                }, 2000, 2000) // 最初に始動させるまで100ミリ秒、ループの間隔を100ミリ秒 に設定
            } else {
                mTimer!!.cancel()
                mTimer = null
                pause_button.isClickable = true
                reset_button.isClickable = true
                start_button.text = "再生"

            }
        }


        pause_button.setOnClickListener() {     //次

            if (cursor.moveToNext() == false) {
                cursor.moveToFirst()
            }

            val fieldIndex =
                cursor.getColumnIndex(MediaStore.Images.Media._ID)   //データの中から画像のIDがセットされている位置を取得
            val id = cursor.getLong(fieldIndex) //画像ID取得
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)


        }


        reset_button.setOnClickListener {   //戻る
            if (cursor.moveToPrevious() == false) {
                cursor.moveToLast()
            }

            val fieldIndex =
                cursor.getColumnIndex(MediaStore.Images.Media._ID)   //データの中から画像のIDがセットされている位置を取得
            val id = cursor.getLong(fieldIndex) //画像ID取得
            val imageUri =
                ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)


        }


    }
}




