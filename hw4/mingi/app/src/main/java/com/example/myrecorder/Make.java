package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Bundle;
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
            EditText frequency = (EditText) findViewById(R.id.frequency);
            EditText phase = (EditText) findViewById(R.id.phase);
            EditText time = (EditText) findViewById(R.id.time);
            EditText sample = (EditText)findViewById(R.id.sample);
            //final
            int m_fre,m_pha,m_time,m_sam;

            @Override
            public void onClick(View view) {

                if (frequency.getText().length() <= 0 ){
                    Toast.makeText(getApplicationContext(), "默认频率为440HZ.", Toast.LENGTH_SHORT).show();
                        m_fre = 440;
                }
                else{
                    m_fre = Integer.parseInt(frequency.getText().toString());
                }
                if(time.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "默认时间为3s.", Toast.LENGTH_SHORT).show();
                    m_time  = 3;
                }else{
                    m_time = Integer.parseInt(time.getText().toString());
                }
                if(phase.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "默认初始相位为300.", Toast.LENGTH_SHORT).show();
                    m_pha  = 300;
                }else{
                    m_pha = Integer.parseInt(phase.getText().toString());
                }
                if(sample.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "默认采样率为44000HZ.", Toast.LENGTH_SHORT).show();
                    m_sam  = 44000;
                }else{
                    m_sam = Integer.parseInt(sample.getText().toString());
                }
                int num_sam = m_time * m_sam;
                final double samples[] = new double[num_sam];
                final short buffer[] = new short[num_sam];

                    for (int i = m_pha; i < m_time*m_sam; ++i)
                    {
                        samples[i] =Math.sin(2 * Math.PI * i / (m_sam/m_fre)); // Sine wave
                        buffer[i] = (short) (samples[i] * Short.MAX_VALUE);  // Higher amplitude increases volume
                    }

                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            m_sam, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                            AudioTrack.MODE_STATIC);
                    audioTrack.write(buffer, 0, buffer.length);
                    audioTrack.play();
                }

        });
    }
}