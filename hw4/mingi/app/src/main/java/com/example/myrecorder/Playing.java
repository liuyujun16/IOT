package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Playing extends AppCompatActivity {
Button To_main_2,again;
ToggleButton control;
    String audio;
    MediaPlayer media_player  = new MediaPlayer();
    int playbackPosition =0;
    boolean on;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playing);
        To_main_2 = (Button) findViewById(R.id.to_main_2);
        again = (Button)findViewById(R.id.again);
        control = (ToggleButton) findViewById(R.id.control);
        final ListView rListView = (ListView) findViewById(R.id.sound_list);
        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecorder";
        final File directory = new File(path);

        control.setVisibility(View.INVISIBLE);
        again.setVisibility(View.INVISIBLE);
        To_main_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"返回主菜单",Toast.LENGTH_LONG).show();
                finish();
            }
        });




        directory.mkdirs();

        File[] folder = directory.listFiles();
        final List<String> Filename = new ArrayList<>();

        for (int i = 0; i < folder.length; i++) {
            Filename.add(folder[i].getName());
            System.out.println(folder[i].getName());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, Filename) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                textView.setTextColor(Color.BLACK);
                return view;
            }
        };

        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), Filename.get(position), Toast.LENGTH_SHORT).show();

                audio = Filename.get(position);
                media_player.reset();
                String audio_path = path + '/'+audio;
                try {
                    media_player.setDataSource(audio_path);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                media_player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                media_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                media_player.prepareAsync();
                control.setVisibility(View.VISIBLE);
                again.setVisibility(View.VISIBLE);
                control.setChecked(false);
            }

        });

        rListView.setAdapter(adapter);


        again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media_player.stop();
                try {
                    media_player.prepare();
                }
                catch(IOException ie) {
                    ie.printStackTrace();
                }
                control.setChecked(false);



            }


        });




    }


    public void onToggleClicked(View v){
        on = ((ToggleButton)v).isChecked();
        if(on){
            if(media_player !=null){
                //保存停止的位置
                playbackPosition = media_player.getCurrentPosition();
                media_player.pause();
            }
        }
        else{
            if(media_player !=null && !media_player.isPlaying()){
                media_player.start();
                media_player.seekTo(playbackPosition);
            }
        }
    }
}