clear all;
close all;
clc;



figure(1);%第一个函数
tiledlayout(3,1)
ax1 = nexttile;
N=4;
n = 0:N-1;
x1= ones(1,N);
y1= fft(x1);
plot(ax1,y1);
title(ax1,"first N=4");
ax2 = nexttile;
 x2 = 1 - abs(n)/N;
y2 = fft(x2);
plot(ax2,abs(y2));
title(ax2,"second");
ax3 = nexttile;
x3 = sin(2*pi*n/N);
y3 = fft(x3);
plot(ax3,abs(y3));
title(ax3,"third");


figure(2);%第二个函数
tiledlayout(3,1)
ax1 = nexttile;
N=16;
n = 0:N-1;
x1= ones(1,N);
y1= fft(x1);
plot(ax1,y1);
title(ax1,"first N=16");
ax2 = nexttile;
 x2 = 1 - abs(n)/N;
y2 = fft(x2);
plot(ax2,abs(y2));
title(ax2,"second");
ax3 = nexttile;
x3 = sin(2*pi*n/N);
y3 = fft(x3);
plot(ax3,abs(y3));
title(ax3,"third");

figure(3);%第三个函数
tiledlayout(3,1)
ax1 = nexttile;
N=1024;
n = 0:N-1;
x1= ones(1,N);
y1= fft(x1);
plot(ax1,y1);
title(ax1,"first N=1024");
ax2 = nexttile;
 x2 = 1 - abs(n)/N;
y2 = fft(x2);
plot(ax2,abs(y2));
title(ax2,"second");
ax3 = nexttile;
x3 = sin(2*pi*n/N);
y3 = fft(x3);
plot(ax3,abs(y3));
title(ax3,"third");
