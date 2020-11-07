clear all;
close all;
clc;

figure(1)
tiledlayout(2,1);
ax1 = nexttile;
[testY,Fs] = audioread('res1.wav');
info = audioinfo('res1.wav');
m = length(testY);
 f = (0:m-1)*(Fs/m);

time = 0:seconds(1/Fs):seconds(info.Duration);
time = time(1:end-1);
 sampleTime = seconds(1/Fs);
 power = abs(fft(testY));
plot(ax1,f(1:floor(m)),power(1:floor(m)));
 title('before AMF');
ax2 = nexttile;

amf = movmean(testY,3);
power1 = abs(fft(amf));
plot(ax2,f(1:floor(m)),power1(1:floor(m)));
 title('after AMF window=3')
 
 figure(2)
tiledlayout(3,1);
ax1 = nexttile;
amf1 = movmean(testY,4);
power2 = abs(fft(amf1));
plot(ax1,f(1:floor(m)),power2(1:floor(m)));
 title('after AMF window=4')

ax2 = nexttile;
amf2 = movmean(testY,8);
power3 = abs(fft(amf2));
plot(ax2,f(1:floor(m)),power3(1:floor(m)));
 title('after AMF window=8')
 
 ax3 = nexttile;
amf3 = movmean(testY,16);
power4 = abs(fft(amf3));
plot(ax3,f(1:floor(m)),power4(1:floor(m)));

 title('after AMF window=16')



