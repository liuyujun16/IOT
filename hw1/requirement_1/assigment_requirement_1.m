sample_rate = 8000;%采样率
duration = 2;%时间
phase = 3000;%初始相位
frequency = 440; %频率




%指定文件位置和文件名
folder = pwd;
baseFileName = 'make_sound.wav';
fullFileName = fullfile(folder, baseFileName);
fprintf('Full File Name = %s\n', fullFileName);
%产生声波信号
t = phase : duration * sample_rate;
Amplitude = 20000;
y = int16(Amplitude .* sin(2.*pi.*t/(sample_rate/frequency)));
%播放
audiowrite(fullFileName, y, sample_rate);
   player = audioplayer(y, sample_rate);
   play(player);
 
