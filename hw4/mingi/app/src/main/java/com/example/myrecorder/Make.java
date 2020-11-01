package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Make extends AppCompatActivity {
Button To_main_3,make_sound;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make);
        To_main_3 = (Button) findViewById(R.id.to_main_3);
        make_sound = (Button) findViewById(R.id.make_sound);

        To_main_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"返回主菜单",Toast.LENGTH_LONG).show();
                finish();
            }
        });



        make_sound.setOnClickListener(new View.OnClickListener(){
            EditText binary_num = (EditText)findViewById(R.id.binary_num);
            //final
            int m_fre,m_pha,m_sam;
            double m_time,sample;
            String bin;
            @Override
            public void onClick(View view) {

                m_sam = 48000;
                m_fre = 18000;
                m_time = 0.01;
                sample  = m_sam * m_time;


                if(binary_num.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "不能什么都不输入.", Toast.LENGTH_SHORT).show();

                }else{
                    bin = binary_num.getText().toString();
                    for (int i = 0 ;i < bin.length(); i++)
                    {
                        if(bin.charAt(i) == '0' || bin.charAt(i) == '1') {
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "请输入二进数.", Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }

                    // impulse 만들어야하고 pause0 ,pasue1 만들어서 buffer애다가 때려박아야함
                    final double samples[] = new double[num_sam];
                    final short buffer[] = new short[num_sam];

                    for (int i = m_pha; i < m_time*m_sam; ++i)
                    {
                        samples[i] =Math.sin(2 * Math.PI * i / (m_sam/m_fre)); // Sine wave
                        buffer[i] = (short) (samples[i] * Short.MAX_VALUE);  // Higher amplitude increases volume
                    }



                    //wav파일로 만드는것도 추가해야함

                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            m_sam, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                            AudioTrack.MODE_STATIC);
                    audioTrack.write(buffer, 0, buffer.length);
                    audioTrack.play();
                }

                }

        });
    }
}