clear all;
close all;
clc;
tiledlayout(3,1);

ax1 = nexttile;%分析信号频率组成
[testY,Fs] = audioread('res1.wav');
m = length(testY);
y1 = fft(testY);
f = (0:m-1)*(Fs/m);
power = abs(y1);
plot(ax1,f(1:floor(m)),power(1:floor(m)),'b:.');
grid on;


m2 = length(testY)*10;
ax2 = nexttile;%补零
y2 = fft(testY,m2);
power2 = abs(y2);
f = (0:m2-1)*(Fs/m2);
plot(ax2,f(1:floor(m2)),power2(1:floor(m2)),'b:.');
grid on;


ax3 = nexttile;%时频分析
spectrogram(testY,128,[],[],Fs,'yaxis');






