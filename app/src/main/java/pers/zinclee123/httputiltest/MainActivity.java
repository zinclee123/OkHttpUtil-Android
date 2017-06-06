package pers.zinclee123.httputiltest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import pers.zinclee123.httputil.HttpUtils;
import pers.zinclee123.httputil.StringUtils;

public class MainActivity extends AppCompatActivity {

    TextView testTv;

    ImageView testIv;

    static final int REQUEST_ALBUM_REQUEST_CODE = 100;

    static final int REQUEST_READ_EXTERNAL_REQUEST_CODE = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTv = (TextView) findViewById(R.id.tv_test);
        testIv = (ImageView) findViewById(R.id.iv_test);

        findViewById(R.id.btn_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HttpUtils.getInstance()
                        .get("http://47.93.115.65/index.php/Api/Goods/goods",null)
                        .subscribe(new Consumer<String>() {
                            @Override
                            public void accept(@NonNull String s) throws Exception {
                                String text = StringUtils.decodeUnicode(s);
                                testTv.setText(text);
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(@NonNull Throwable throwable) throws Exception {
                                throwable.printStackTrace();
                                testTv.setText(throwable.getMessage());
                            }
                        });
            }
        });


        findViewById(R.id.btn_upload_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent albumIntent = new Intent(Intent.ACTION_PICK, null);
                albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(albumIntent, REQUEST_ALBUM_REQUEST_CODE);
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_EXTERNAL_REQUEST_CODE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            if (requestCode == REQUEST_ALBUM_REQUEST_CODE){
                Uri selectedImage = data.getData();
                String[] filePathColumns = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumns, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePathColumns[0]);
                String imagePath = c.getString(columnIndex);

                Bitmap bm = null;
                try{
                    File file = new File(imagePath);
                    if (file.exists()) {
                        bm = BitmapFactory.decodeFile(imagePath);
                        //将图片显示到ImageView中
                        testIv.setImageBitmap(ThumbnailUtils.extractThumbnail(bm, testIv.getMeasuredWidth(), testIv.getMeasuredHeight(), ThumbnailUtils.OPTIONS_RECYCLE_INPUT));
                    }

                    //上传一下
                    Map<String,File> params = new HashMap<>();
                    params.put("android_test.jpg",file);

                    HttpUtils.getInstance()
                            .postFile("http://47.93.115.65/index.php/Api/Index/head",null,params)
                            .subscribe(new Consumer<String>() {
                                @Override
                                public void accept(@NonNull String s) throws Exception {
                                    testTv.setText(s);
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    throwable.printStackTrace();
                                    testTv.setText(throwable.getMessage());
                                }
                            });
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    c.close();
                    if (bm != null && !bm.isRecycled()){
                        bm.recycle();
                    }
                }
            }
        }
    }

}
