package com.appzone.mrsool.activities_fragments.terms_conditions;

import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.appzone.mrsool.R;
import com.appzone.mrsool.language.Language_Helper;

import java.util.Locale;

import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import io.paperdb.Paper;

public class TermsConditionsActivity extends AppCompatActivity {

    private ImageView arrow;
    private LinearLayout ll_back;
    private TextView tv_content;
    private SmoothProgressBar smoothprogressbar;
    private String current_lang;

    @Override
    protected void attachBaseContext(Context base) {
        Paper.init(base);
        current_lang = Paper.book().read("lang", Locale.getDefault().getLanguage());
        super.attachBaseContext(Language_Helper.setLocality(base,current_lang));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        initView();
    }

    private void initView() {
        Paper.init(this);
        current_lang = Paper.book().read("lang","ar");
        arrow = findViewById(R.id.arrow);
        if (current_lang.equals("ar"))
        {
            arrow.setImageResource(R.drawable.ic_right_arrow);
            arrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_IN);

        }else
            {
                arrow.setImageResource(R.drawable.ic_left_arrow);
                arrow.setColorFilter(ContextCompat.getColor(this,R.color.white), PorterDuff.Mode.SRC_IN);


            }
        ll_back = findViewById(R.id.ll_back);
        tv_content = findViewById(R.id.tv_content);
        smoothprogressbar = findViewById(R.id.smoothprogressbar);

        ll_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getTerms();
    }



    private void getTerms() {

    }

    private void updateTermsContent(String content) {
        tv_content.setText(content);
    }

}
