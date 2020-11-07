clear all;
close all;
clc;
SNR_0 = 0;
SNR_10 = 10;
SNR_20 =  20;
cLen = 100000;
codes=randn(1,cLen)>=0;
if mod(cLen,2) == 1
    codes = [codes , 0] ;
    cLen = cLen+1;
end
fs = 48000;
f = 20000;
T = 0.025;
N = fs * T;
t = (0:N-1)/fs;


sigI = sin(2*pi*f*t);
sigQ = cos(2*pi*f*t);
sigL = length(sigI);

sig = zeros(1,sigL*cLen/2);

for i = 1 : cLen/2
    fI = (1-2*codes(i*2-1))*sqrt(2)/2;
    fQ = (1-2*codes(i*2))*sqrt(2)/2;
    sig((i-1)*sigL+1:i*sigL) = fI*sigI+fQ*sigQ;
end


sig_20 = awgn(sig,SNR_20,'measured');
noise = sig_20 - sig;
sig_20 = sig_20 / max(abs(sig_20));
audiowrite('snr_20.wav',sig_20,fs);

sig_10 = awgn(sig,SNR_10,'measured');
sig_10 = sig_10 / max(abs(sig_10));
audiowrite('snr_10.wav',sig_10,fs);

sig_0 = awgn(sig,SNR_0,'measured');
sig_0 = sig_0 / max(abs(sig_0));
audiowrite('snr_0.wav',sig_0,fs);



[snr_0,fs_] = audioread('snr_0.wav');
snr_0 = snr_0';
N = fs_*0.025;
num_symb = floor(length(snr_0)/N);
Base_signal_I = sin(2*pi*f*t);
Base_signal_I = repmat(Base_signal_I, 1, num_symb);
Base_signal_Q = cos(2*pi*f*t);
Base_signal_Q = repmat(Base_signal_Q, 1, num_symb);
yI = snr_0(1:N*num_symb).*Base_signal_I;
yQ = snr_0(1:N*num_symb).*Base_signal_Q;
yI1 = lowpass(yI,f,fs);
yQ1 = lowpass(yQ,f,fs);

codesI = zeros(1,num_symb);
codesQ = zeros(1,num_symb);
codesR = zeros(1,num_symb*2);

for i = 1:num_symb
    codesQ(i) = sum(yQ1((i-1)*N +1 : i*N));
    codesI(i) = sum(yI1((i-1)*N +1 : i*N));
    
end


y1d=sign(codesI);%符号函数,x<0,sign(x)=-1;x=0,sign(x)=0;x>0,sign(x)=1
y2d=sign(codesQ);
%反映射
y1rmap(y1d==1)=0;
y1rmap(y1d==-1)=1;
y2rmap(y2d==1)=0;
y2rmap(y2d==-1)=1;

for i = 0:num_symb-1
    codesR(i*2+1) = y1rmap(i+1);
    codesR(i*2+2) = y2rmap(i+1);
end

result0 = 0;
for i =1 : num_symb*2
    if codes(i) == codesR(i)
        result0 = result0 + 1;
    end
end

disp (sprintf('当信噪比为0db 传输成功率为 : %d %%',result0/cLen *100));





[snr_10,fs_] = audioread('snr_10.wav');
snr_10 = snr_10';
N = fs_*0.025;
num_symb = floor(length(snr_10)/N);
Base_signal_I = sin(2*pi*f*t);
Base_signal_I = repmat(Base_signal_I, 1, num_symb);
Base_signal_Q = cos(2*pi*f*t);
Base_signal_Q = repmat(Base_signal_Q, 1, num_symb);
yI = snr_10(1:N*num_symb).*Base_signal_I;
yQ = snr_10(1:N*num_symb).*Base_signal_Q;
yI1 = lowpass(yI,f,fs);
yQ1 = lowpass(yQ,f,fs);

codesI = zeros(1,num_symb);
codesQ = zeros(1,num_symb);
codesR = zeros(1,num_symb*2);

for i = 1:num_symb
    codesQ(i) = sum(yQ1((i-1)*N +1 : i*N));
    codesI(i) = sum(yI1((i-1)*N +1 : i*N));
    
end


y1d=sign(codesI);%符号函数,x<0,sign(x)=-1;x=0,sign(x)=0;x>0,sign(x)=1
y2d=sign(codesQ);
%反映射
y1rmap(y1d==1)=0;
y1rmap(y1d==-1)=1;
y2rmap(y2d==1)=0;
y2rmap(y2d==-1)=1;

for i = 0:num_symb-1
    codesR(i*2+1) = y1rmap(i+1);
    codesR(i*2+2) = y2rmap(i+1);
end

result10 = 0;
for i =1 : num_symb*2
    if codes(i) == codesR(i)
        result10 = result10 + 1;
    end
end

disp (sprintf('当信噪比为10db 传输成功率为 : %d %%',result10/cLen *100));



[snr_20,fs_] = audioread('snr_20.wav');
snr_20 = snr_20';
N = fs_*0.025;
num_symb = floor(length(snr_20)/N);
Base_signal_I = sin(2*pi*f*t);
Base_signal_I = repmat(Base_signal_I, 1, num_symb);
Base_signal_Q = cos(2*pi*f*t);
Base_signal_Q = repmat(Base_signal_Q, 1, num_symb);
yI = snr_20(1:N*num_symb).*Base_signal_I;
yQ = snr_20(1:N*num_symb).*Base_signal_Q;
yI1 = lowpass(yI,f,fs);
yQ1 = lowpass(yQ,f,fs);

codesI = zeros(1,num_symb);
codesQ = zeros(1,num_symb);
codesR = zeros(1,num_symb*2);

for i = 1:num_symb
    codesQ(i) = sum(yQ1((i-1)*N +1 : i*N));
    codesI(i) = sum(yI1((i-1)*N +1 : i*N));
    
end


y1d=sign(codesI);%符号函数,x<0,sign(x)=-1;x=0,sign(x)=0;x>0,sign(x)=1
y2d=sign(codesQ);
%反映射
y1rmap(y1d==1)=0;
y1rmap(y1d==-1)=1;
y2rmap(y2d==1)=0;
y2rmap(y2d==-1)=1;

for i = 0:num_symb-1
    codesR(i*2+1) = y1rmap(i+1);
    codesR(i*2+2) = y2rmap(i+1);
end

result20 = 0;
for i =1 : num_symb*2
    if codes(i) == codesR(i)
        result20 = result20 + 1;
    end
end

disp (sprintf('当信噪比为20db 传输成功率为 : %d %%',result20/cLen *100));
