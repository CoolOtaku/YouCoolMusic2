package com.example.youcoolmusic2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.R;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.youcoolmusic2.App.main_player;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView logo;
    private CircleImageView img_developer;
    private int click = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(App.sp.getInt("id_theme",R.style.AppThemeDark));
        setContentView(R.layout.activity_about);

        logo = (ImageView)findViewById(R.id.logo);
        img_developer = (CircleImageView) findViewById(R.id.img_developer);

        logo.setOnClickListener(this);
        img_developer.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo:
                super.onBackPressed();
                break;
            case R.id.img_developer:
                click++;
                if (click == 2) {
                    img_developer.setImageResource(R.drawable.developer);
                } else if (click == 3) {
                 img_developer.setImageResource(R.drawable.developer1);
                } else if (click == 4) {
                    img_developer.setImageResource(R.drawable.developer2);
                } else if (click == 5) {
                    img_developer.setImageResource(R.drawable.myavatar);
                    click = 0;
                }
                break;
        }
    }

    @Override
    public void onStart() {
        Player.window = getWindow();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if(main_player.isFullScreen()){
            main_player.exitFullScreen();
        }else {
            super.onBackPressed();
        }
    }
}
