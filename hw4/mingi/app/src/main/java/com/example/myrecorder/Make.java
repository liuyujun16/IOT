package com.example.myrecorder;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

public class Make extends AppCompatActivity {
Button To_main_3,make_sound;

    int SamplingRate = 48000;
    //格式：双声道
    int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
    //16Bit
    int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
    //是否在录制
    boolean isRecording = false;
    //每次从audiorecord输入流中获取到的buffer的大小
    int bufferSize = 0;

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
                    byte[] buffer = floatArrayToBytes(sound);
                    System.out.println("sound length");
                    System.out.println(sound.length);
                    System.out.println("byte length");

                    System.out.println(buffer.length);
                    AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                            m_sam, AudioFormat.CHANNEL_IN_STEREO,
                            AudioFormat.ENCODING_PCM_16BIT, buffer.length,
                            AudioTrack.MODE_STATIC);
                       //audioTrack.write(sound, 0, sound.length,AudioTrack.WRITE_BLOCKING);
                   // audioTrack.write(buffer, 0, buffer.length);
                    audioTrack.write(buffer,0,buffer.length);

                    audioTrack.play();





                    String file_name;

                    String name = Environment.getExternalStorageDirectory().getAbsolutePath()+"/myrecorder/raw.wav";
                    //调用开始录音函数，并把原始数据保存在指定的文件中
                    StartRecord(name,buffer);
                    Date now = Calendar.getInstance().getTime();
                    file_name = now.toString().substring(0,19);
                    file_name = file_name.replaceAll(" ","");
                    file_name = file_name.replaceAll(":",".");
                    System.out.println("이름이야"+file_name);
                    //用此刻时间为最终的录音wav文件命名
                    String filepath =Environment.getExternalStorageDirectory().getAbsolutePath()+"/myrecorder/"+file_name+".wav";
                    //把录到的原始数据写入到wav文件中。
                    copyWaveFile(name, filepath);
                }


            }




        });



    }






    //开始录音
    public void StartRecord(String name,byte[] buffer) {

        //生成原始数据文件
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
            //建立audioRecord实例
            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SamplingRate, channelConfiguration, audioEncoding, bufferSize);

            //设置用来承接从audiorecord实例中获取的原始数据的数组
            //byte[] buffer = new byte[bufferSize];
            //启动audioRecord
            audioRecord.startRecording();
            //设置正在录音的参数isRecording为true
            isRecording = true;
            //只要isRecording为true就一直从audioRecord读出数据，并写入文件输出流。
            //当停止按钮被按下，isRecording会变为false，循环停止
//            while (isRecording) {
//                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
//
//                for (int i = 0; i < bufferReadResult; i++) {
//                    dos.write(buffer[i]);
//                    System.out.println(buffer[i]);
//                }
//            }


            for (int i = 0; i < buffer.length; i++) {
                dos.write(buffer[i]);
            }
            //停止audioRecord，关闭输出流
            audioRecord.stop();
            dos.close();
        } catch (Throwable t) {
            Log.e("MainActivity", "录音失败");
        }
    }


    public static byte[] floatArrayToBytes(float[] d) {
        byte[] r = new byte[d.length * 4];
        for (int i = 0; i < d.length; i++) {
            byte[] s = floatToBytes(d[i]);
            for (int j = 0; j < 4; j++)
                r[4 * i + j] = s[j];/* w  w  w .ja v  a  2 s  .c om*/
        }
        return r;
    }

    public static byte[] floatToBytes(float d) {
        int i = Float.floatToRawIntBits(d);
        return intToBytes(i);
    }

    public static byte[] intToBytes(int v) {
        byte[] r = new byte[4];
        for (int i = 0; i < 4; i++) {
            r[i] = (byte) ((v >>> (i * 8)) & 0xFF);
        }
        return r;
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
