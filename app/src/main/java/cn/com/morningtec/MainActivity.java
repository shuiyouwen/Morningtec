package cn.com.morningtec;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import cn.com.morningtec.fontsizeselector.Font;
import cn.com.morningtec.fontsizeselector.FontSizeSelectorView;
import cn.com.morningtec.loadingview.LoadingView;

public class MainActivity extends AppCompatActivity {

    private LinearLayout mContainer2;
    private LinkedList<TextView> mTextViews = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final LoadingView loadingView = findViewById(R.id.loading_view);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingView.start();
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

        createTextLayout();
    }

    private void createTextLayout() {
        List<String> data = Arrays.asList("广告", "头像", "色情", "反动");
        mContainer2 = findViewById(R.id.ll_container2);
        for (String datum : data) {
            FrameLayout frameLayout = new FrameLayout(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 40));
            frameLayout.setLayoutParams(layoutParams);
            mContainer2.addView(frameLayout);

            TextView textView = new TextView(this);
            FrameLayout.LayoutParams fParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            fParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM;
            textView.setLayoutParams(fParams);
            textView.setText(datum);
            textView.setTextSize(16);
            textView.setAlpha(0f);
            frameLayout.addView(textView);
            mTextViews.add(textView);

            View lineView = new View(this);
            lineView.setBackgroundColor(Color.GRAY);
            LinearLayout.LayoutParams lineParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, Utils.dp2px(this, 0.5f));
            lineView.setLayoutParams(lineParams);
            mContainer2.addView(lineView);
        }

        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAnimation();
            }
        });
    }

    private void startAnimation() {
        if (mTextViews == null || mTextViews.size() <= 0) {
            return;
        }
        final TextView textview = mTextViews.pop();
        final int height = textview.getHeight();
        //86
        textview.post(new Runnable() {
            @Override
            public void run() {
                textview.offsetTopAndBottom(height);
                int transactionY = Utils.dp2px(MainActivity.this, 40) / 2
                        + (Utils.dp2px(MainActivity.this, 40) / 2 - height / 2);
                ViewPropertyAnimator viewPropertyAnimator = textview.animate().alpha(1f).translationY(-transactionY).setDuration(500);
                viewPropertyAnimator.setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        startAnimation();
                    }
                });
                viewPropertyAnimator.start();
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
