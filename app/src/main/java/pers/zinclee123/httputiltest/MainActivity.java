package pers.zinclee123.httputiltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.net.URLDecoder;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import pers.zinclee123.httputil.HttpUtils;
import pers.zinclee123.httputil.StringUtils;

public class MainActivity extends AppCompatActivity {

    TextView testTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        testTv = (TextView) findViewById(R.id.tv_test);

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
    }
}
