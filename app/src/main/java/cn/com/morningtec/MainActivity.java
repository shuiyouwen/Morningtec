package cn.com.morningtec;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cn.com.morningtec.fontsizeselector.Font;
import cn.com.morningtec.fontsizeselector.FontSizeSelectorView;
import cn.com.morningtec.loadingview.LoadingView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LinearLayout llContainer = findViewById(R.id.ll_container);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < llContainer.getChildCount(); i++) {
                    LoadingView loadingView = (LoadingView) llContainer.getChildAt(i);
                    loadingView.start();
                }
            }
        });


        FontSizeSelectorView fontSizeSelectorView = findViewById(R.id.font_size_selector);
        fontSizeSelectorView.setFonts(generateFont("小", Color.parseColor("#333333"), 14)
                , generateFont("标准", Color.parseColor("#b5b5b5"), 16)
                , generateFont("", 0, 20)
                , generateFont("大", Color.parseColor("#333333"), 24));
        fontSizeSelectorView.setOnFontSelectListener(new FontSizeSelectorView.OnFontSelectListener() {
            @Override
            public void onFontSelect(Font font) {
                Toast.makeText(MainActivity.this, "选中字体大小：" + font.getSize(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Font generateFont(String topTextStr, int topTextColor, int size) {
        Font font = new Font();
        font.setSize(size);

        Font.Text topText = font.new Text();
        topText.setText(topTextStr);
        topText.setColor(topTextColor);
        font.setTopText(topText);

        Font.Text bottomText = font.new Text();
        bottomText.setColor(Color.parseColor("#212121"));
        bottomText.setText(String.valueOf(size));
        font.setBottomText(bottomText);

        return font;
    }
}
