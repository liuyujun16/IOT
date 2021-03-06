package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.EditText;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Calendar;
import java.util.Date;

public class Recording extends AppCompatActivity {
    Button To_main, StartRecord, StopRecord;
    //48K采样率
    int SamplingRate = 44000;
    //格式：双声道
    int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
    //16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    boolean isRecording = false;
    //每次从audiorecord输入流中获取到的buffer的大小
    int bufferSize = 0;
    byte[] data = new byte[bufferSize];
    String filepath ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording);
        StartRecord = (Button)findViewById(R.id.start_record);
        StopRecord = (Button)findViewById(R.id.stop_record);
//在录音键被按下之前，不允许按停止键
        StopRecord.setEnabled(false);

        //完成每个按钮的功能
        StartRecord.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                EditText record_sample_rate = (EditText) findViewById(R.id.sample_rate);
                System.out.println(record_sample_rate.getText().toString());
                if (record_sample_rate.getText().length() <= 0) {
                    Toast.makeText(getApplicationContext(), "默认值为44000.", Toast.LENGTH_SHORT).show();
                }
                else{
                    SamplingRate = Integer.parseInt(record_sample_rate.getText().toString());
                    System.out.println(record_sample_rate.getText().toString());

                }

                //恢复停止录音按钮，并禁用开始录音按钮
                StopRecord.setEnabled(true);
                StartRecord.setEnabled(false);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //设置用于临时保存录音原始数据的文件的名字
                        String file_name;

                        String name = Environment.getExternalStorageDirectory().getAbsolutePath()+"/myrecorder/raw.wav";
                        //调用开始录音函数，并把原始数据保存在指定的文件中
                        data = StartRecord(name);
//                        System.out.println(bufferSize);
//                        for(int i =0; i<bufferSize;i++){
//                            System.out.println(data[i]);
//                        }
                        //获取此刻的时间
                        Date now = Calendar.getInstance().getTime();
                        file_name = now.toString().substring(0,19);
                        file_name = file_name.replaceAll(" ","");
                        file_name = file_name.replaceAll(":",".");
                        System.out.println("이름이야"+file_name);
                        //用此刻时间为最终的录音wav文件命名
                        filepath =Environment.getExternalStorageDirectory().getAbsolutePath()+"/myrecorder/"+file_name+".wav";
                        System.out.println(filepath);

                        //把录到的原始数据写入到wav文件中。
                        copyWaveFile(name, filepath);

                        System.out.println(filepath);
                        File file = new File(filepath);
                        InputStream fis = null;
                        try {
                            fis = new FileInputStream(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        byte[] audio_data = new byte[(int)file.length()];


                        try {
                            fis.read(audio_data, 0, audio_data.length);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            fis.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        demodulate(audio_data);

                    }
                });
                //开启线程
                thread.start();
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
        To_main = (Button) findViewById(R.id.to_main);

        To_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(),"返回主菜单",Toast.LENGTH_LONG).show();
                finish();
            }
        });


    }
    public static float[] byteArrayToFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        FloatBuffer fb = buffer.asFloatBuffer();
        float[] floatArray = new float[fb.limit()];
        fb.get(floatArray);
        return floatArray;
    }


    public void demodulate(byte[] audio_data){
        double time =0.01;
        int n,fs = 48000,window;
        float[] audio = byteArrayToFloatArray(audio_data);

        for (int i = 0 ;i<audio.length;i++){
            if (Float.isNaN(audio[i])){
                audio[i] = 0;
            }
        }
        n = audio.length;
        window = (int) (fs*time);
        float[] impulse_fft = new float[n];




    }


    //开始录音
    public byte[] StartRecord(String name) {

        //生成原始数据文件
        System.out.println(name);
        File file = new File(name);
        file.mkdirs();

        //如果文件已经存在，就先删除再创建
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
            bufferSize = AudioRecord.getMinBufferSize(SamplingRate, channelConfiguration, audioEncoding);

            System.out.println(bufferSize);

            //建立audioRecord实例
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, audioEncoding, bufferSize);

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

            return buffer;
        } catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }
        byte [] aa = new byte[1];
        return aa;
    }
    private void copyWaveFile(String inFileName, String outFileName)
    {
        FileInputStream in = null;
        FileOutputStream out = null;
        long totalAudioLen = 0;
        //wav文件比原始数据文件多出了44个字节，除去表头和文件大小的8个字节剩余文件长度比原始数据多36个字节
        long totalDataLen = totalAudioLen + 36;
        long longSampleRate = SamplingRate;
        int channels = 2;
        //每分钟录到的数据的字节数
        long byteRate = 16 * SamplingRate * channels / 8;

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
            System.out.println("제발 시발 좀");
            System.out.println(data.length);

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