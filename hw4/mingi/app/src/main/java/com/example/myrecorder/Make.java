package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
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
            EditText binary_num = (EditText)findViewById(R.id.binary_num);
            //final
            int m_fre,m_pha,m_sam,N,sum = 0;
            double m_time,sample;
            String bin;
            @Override
            public void onClick(View view) {
                m_sam = 48000;
                m_fre = 2200;
                m_time = 0.01;
                sample  = m_sam * m_time;

                N = (int) (m_sam * m_time);

                if(binary_num.getText().length() <= 0){
                    Toast.makeText(getApplicationContext(), "不能什么都不输入.", Toast.LENGTH_SHORT).show();

                }else{
                    bin = binary_num.getText().toString();
                    System.out.println(bin.length());
                    sum+=(bin.length()+1)*N;
                    System.out.println(sum);
                    for (int i = 0 ;i < bin.length(); i++)
                    {
                        if(bin.charAt(i) == '0' || bin.charAt(i) == '1') {
                            if(bin.charAt(i)=='0'){
                                sum+=N;
                                System.out.println("0 is working");
                            }
                            else if (bin.charAt(i) == '1' )
                            {
                                sum+=2*N;
                                System.out.println("1 is working");

                            }
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "请输入二进数.", Toast.LENGTH_SHORT).show();

                            return;
                        }
                    }
                    System.out.println(sum);
                    // impulse 만들어야하고 pause0 ,pasue1 만들어서 buffer애다가 때려박아야함
                    float sound[] = new float[sum];
                    final float pause0[] = new float[N];
                    final float pause1[] = new float[2*N];
                    final float impulse[] = new float[N];
//                    for (int i = 0 ; i < N; i++){
//                        pause0[i] = 0;
//                        pause1[i] = 0;
//                        pause1[i*2] = 0;
//                    }

                    for (int i = 1; i < N; i++)
                    {
                        impulse[i-1] =(float)Math.sin(2 * Math.PI * (float)i *m_fre/(m_sam)); // Sine wave
                        //System.out.println(impulse[i-1]);
                        //System.out.println((float)i/48000);
                        //System.out.println(i/m_sam);

                    }

                    final float set0[] = new float[2*N];
                    final float set1[] = new float[3*N];
                    System.arraycopy(impulse,0,set0,0,N);
                    System.arraycopy(pause0,0,set0,N,N);

                    System.arraycopy(impulse,0,set1,0,N);
                    System.arraycopy(pause1,0,set1,N,2*N);


                    int one_count = 0;
                    int zero_count = 0;
                    for (int i =0; i<bin.length();i++){
                        if(bin.charAt(i) == '1'){
                            System.arraycopy(set1,0,sound,N+3*N*one_count+2*N*zero_count,3*N);
                            one_count ++;

                        }
                        else{
                            System.arraycopy(set0,0,sound,N+3*N*one_count+2*N*zero_count,2*N);
                            zero_count++;

                        }

                    }
                    System.out.println("sum의 크리");

                    System.out.println(sum);
                    System.out.println(impulse.length);


                    System.out.println(sound.length);

//                    for (int i = 0 ; i < sound.length;i++){
//                        System.out.println(sound[i]);
//                        System.out.println("working?");
//
//                    }

                    //wav파일로 만드는것도 추가해야함

                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            m_sam, AudioFormat.CHANNEL_OUT_MONO,
                            AudioFormat.ENCODING_PCM_FLOAT, sound.length,
                            AudioTrack.MODE_STATIC);
                       audioTrack.write(sound, 0, sound.length,AudioTrack.WRITE_BLOCKING);

                    audioTrack.play();
                }

                }

        });
    }
}