package com.example.new_recorder;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button StartRecord, StopRecord,make_sound;
    //48K采样率
    int SamplingRate = 48000;
    //格式：双声道
    int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
    //16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    boolean isRecording = false;
    //每次从audiorecord输入流中获取到的buffer的大小
    String music_file;
    MediaPlayer mp  = new MediaPlayer();
    String file_name;

    private Button play;
    private Button pause;
    private Button restart;
    private int playbackPosition =0;


    int bufferSize = 0;
    int record_sam;

    final int duration = 20;
    final int sampleRate = 22050;
    final double freqOfTone = 440;

    final int numSamples = duration * sampleRate;
    final double samples[] = new double[numSamples];
    final short buffer[] = new short[numSamples];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        GetPermission();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StartRecord = (Button) findViewById(R.id.start_record);
        StopRecord = (Button) findViewById(R.id.stop_record);
        make_sound = (Button) findViewById(R.id.make_sound);
        play = (Button)findViewById(R.id.play_button);
        pause = (Button)findViewById(R.id.pause_button);
        restart = (Button)findViewById(R.id.replay_button);
        play.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);
        restart.setVisibility(View.INVISIBLE);

//在录音键被按下之前，不允许按停止键
        StopRecord.setEnabled(false);






//完成每个按钮的功能
        StartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                EditText record_sample = (EditText) findViewById(R.id.editText6);
                System.out.println(record_sample.getText().toString());
                if (record_sample.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "값을 입력하세요.", Toast.LENGTH_SHORT).show();


                } else {
                    record_sam = Integer.parseInt(record_sample.getText().toString());
                    //恢复停止录音按钮，并禁用开始录音按钮
                    StopRecord.setEnabled(true);
                    StartRecord.setEnabled(false);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            //设置用于临时保存录音原始数据的文件的名字
                            String name = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecorder/raw.wav";
                            //调用开始录音函数，并把原始数据保存在指定的文件中
                            StartRecord(name);
                            //获取此刻的时间
                            Date now = Calendar.getInstance().getTime();
                            //由于文件名无法播放，所以进行改文件名。
                            file_name = now.toString().replaceAll(" ","");
                            file_name = file_name.replaceAll("\\+","");
                            file_name= file_name.replaceAll(":","");

                            //file_name = file_name.substring(18);
                            //System.out.println("이게이름이냐"+file_name);
                            //用此刻时间为最终的录音wav文件命名
                            String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecorder/" + file_name + ".wav";
                            //把录到的原始数据写入到wav文件中。
                            copyWaveFile(name, filepath);
                        }
                    });
                    //开启线程
                    thread.start();
                }
            }
        });

        StopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //停止录音
                isRecording = false;
                //恢复开始录音按钮，并禁用停止录音按钮
                StopRecord.setEnabled(false);
                StartRecord.setEnabled(true);
            }
        });








        final String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/myrecorder";
        final File directory = new File(path);
        //如果文件不存在，就进行创建该文件。
        directory.mkdirs();

        File[] files = directory.listFiles();
        final List<String> filesNameList = new ArrayList<>();
        System.out.println(files);
        if(files == null){

        }else {


            for (int i = 0; i < files.length; i++) {
                filesNameList.add(files[i].getName());
                System.out.println(files[i].getName());


            }
        }
        System.out.println(filesNameList);
        ListView rListView = (ListView) findViewById(R.id.list_record);
        ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_list_item_1, filesNameList) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                TextView textView = (TextView) view.findViewById(android.R.id.text1);
                //改变显示的字的颜色
                textView.setTextColor(Color.BLACK);

                return view;
            }
        };

        rListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getApplicationContext(), filesNameList.get(position), Toast.LENGTH_SHORT).show();
                //System.out.println("씨발뭐야아아");

                System.out.println(filesNameList.get(position));
                music_file = filesNameList.get(position);


                //System.out.println("엠피가뭘까?" + mp);
                mp.reset();
                // MediaPlayer mp = new MediaPlayer();
                    System.out.println(path + '/' + music_file);

                    try {
                        mp.setDataSource(path + '/' + music_file);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    //System.out.println("1번");

                    mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer media) {

                            media.start();

                        }

                    });

                    mp.prepareAsync();
                    //System.out.println("2번");


                    pause.setVisibility(View.VISIBLE);
                    restart.setVisibility(View.VISIBLE);


                }

        });

        rListView.setAdapter(adapter);


        restart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                try {
                    mp.prepare();
                }
                catch(IOException ie) {
                    ie.printStackTrace();
                }

                play.setVisibility(View.INVISIBLE);
                pause.setVisibility(View.VISIBLE);
               // pause.setVisibility(View.INVISIBLE);


            }


        });

        pause.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mp !=null){
                    //保存停止的位置
                    playbackPosition = mp.getCurrentPosition();
                    mp.pause();
                    Toast.makeText(MainActivity.this,"Pause",Toast.LENGTH_LONG).show();;
                }
                pause.setVisibility(View.INVISIBLE);
                play.setVisibility(View.VISIBLE);
                restart.setVisibility(View.VISIBLE);


            }

        });

        play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(mp !=null && !mp.isPlaying()){
                    mp.start();
                    mp.seekTo(playbackPosition);
                    Toast.makeText(MainActivity.this,"Restart",Toast.LENGTH_LONG).show();
                }
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.INVISIBLE);
                restart.setVisibility(View.VISIBLE);
            }
        });


        make_sound.setOnClickListener(new View.OnClickListener(){
            EditText freq = (EditText) findViewById(R.id.editText2);
            EditText phase = (EditText) findViewById(R.id.editText3);
            EditText time = (EditText) findViewById(R.id.editText4);
            EditText sample = (EditText)findViewById(R.id.editText);

            @Override
            public void onClick(View view) {
                //System.out.println("씨발");

                //System.out.println(freq.getText().toString());
                //System.out.println(phase.getText().toString());
                //System.out.println(time.getText().toString());
                //System.out.println(sample.getText().toString());


                if (freq.getText().length() <= 0 || time.getText().length() <= 0 || phase.getText().length() <= 0 || sample.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "请输入该值.", Toast.LENGTH_SHORT).show();


                }
                else if(Integer.parseInt(sample.getText().toString())<4410){
                    Toast.makeText(getApplicationContext(), "采样率需要高于4410.", Toast.LENGTH_SHORT).show();

                }
                else {
                    int fre = Integer.parseInt(freq.getText().toString());
                    int pha = Integer.parseInt(phase.getText().toString());
                    int tim = Integer.parseInt(time.getText().toString());
                    int sam = Integer.parseInt(sample.getText().toString());

                    for (int i = pha; i < tim*sam; ++i)
                {
                    samples[i] =Math.sin(2 * Math.PI * i / (sam/fre)); // Sine wave
                    buffer[i] = (short) (samples[i] * Short.MAX_VALUE);  // Higher amplitude increases volume
                }

                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                        sam, AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                        AudioTrack.MODE_STATIC);
                audioTrack.write(buffer, 0, buffer.length);
                audioTrack.play();
                }
            }
        });










    }



    private void GetPermission() {

        /*在此处插入运行时权限获取的代码*/
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)!=
                PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)!=
                        PackageManager.PERMISSION_GRANTED
        )
        {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.RECORD_AUDIO,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE}, 0);
        }
    }

    //开始录音
    public void StartRecord(String name) {

        //生成原始数据文件
        File file = new File(name);
        //如果文件已经存在，就先删除再创建
        file.mkdirs();

        if (file.exists())
            file.delete();
        try {
            file.createNewFile();
        } catch (IOException e) {
            throw new IllegalStateException("未能创建" + file.toString());
        }
        try {
            //文件输出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            //获取在当前采样和信道参数下，每次读取到的数据buffer的大小
            bufferSize = AudioRecord.getMinBufferSize(record_sam, channelConfiguration, audioEncoding);
            //建立audioRecord实例
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, record_sam, channelConfiguration, audioEncoding, bufferSize);

            //设置用来承接从audiorecord实例中获取的原始数据的数组
            byte[] buffer = new byte[bufferSize];
            //启动audioRecord
            audioRecord.startRecording();
            //设置正在录音的参数isRecording为true
            isRecording = true;
            //只要isRecording为true就一直从audioRecord读出数据，并写入文件输出流。
            //当停止按钮被按下，isRecording会变为false，循环停止
            while (isRecording) {
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
                for (int i = 0; i < bufferReadResult; i++) {
                    dos.write(buffer[i]);
                }
            }
            //停止audioRecord，关闭输出流
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }
    }


    private void copyWaveFile(String inFileName, String outFileName)
    {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        //wav文件比原始数据文件多出了44个字节，除去表头和文件大小的8个字节剩余文件长度比原始数据多36个字节
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = record_sam;
        int channels = 2;
        //每分钟录到的数据的字节数
        long byteRate = 16 * record_sam * channels / 8;
        byte[] data = new byte[bufferSize];
        try
        {
            in = new FileInputStream(inFileName);
            out = new FileOutputStream(outFileName);
            //获取真实的原始数据长度
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            //为wav文件写文件头
            WriteWaveFileHeader(out, totalAudioLen, totalDataLen, longSampleRate, channels, byteRate);
            //把原始数据写入到wav文件中。
            while(in.read(data) != -1)
            {
                out.write(data);
            }
            in.close();
            out.close();
        } catch (FileNotFoundException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private void WriteWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W';
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // WAV type format = 1
        header[21] = 0;
        header[22] = (byte) channels; //指示是单声道还是双声道
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff); //采样频率
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff); //每分钟录到的字节数
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (2 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd';
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff); //真实数据的长度
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        //把header写入wav文件
        out.write(header, 0, 44);
    }

}