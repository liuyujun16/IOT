clear all;
close all;
clc;


tiledlayout(3,1);
ax1 = nexttile;

[testY,Fs] = audioread('res2.wav');
info = audioinfo('res2.wav');
m = length(testY);
time = 0:seconds(1/Fs):seconds(info.Duration);
time = time(1:end-1);
 sampleTime = seconds(1/Fs);
 f = (0:m-1)*(Fs/m);

 plot(f,abs(fft(testY)));
title("before band pass filter");

 
 bpf1 = bandpass(testY,[17000,18000],Fs);%17Khz~18kHz
 ax2 = nexttile;
power = abs(fft(bpf1));
plot(ax2,f(1:floor(m)),power(1:floor(m)));

title("band pass filter 17kHz~18kHz");


 bpf2 = bandpass(testY,[20000,21000],Fs);%20kHz~21kHz
 ax3 = nexttile;
 power1 = abs(fft(bpf2));
plot(ax3,f(1:floor(m)),power1(1:floor(m)));

plot(f,(abs(fft(bpf2))));

title("band pass filter 20kHz~21kHz");



