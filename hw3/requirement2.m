clear all;
close all;
clc;

fs = 48000;
f = 20000;
T = 0.025;
add =0;
codes = input('enter a binary:','s');
bin = [];
n_c=length(codes);
inv_c=regexp(codes,'[a-z_A-Z2-9]');
if(~isempty(inv_c))
    disp(['Invalid char: ' codes(inv_c) ' idx= ' num2str(inv_c)])
else
    disp([codes ' Valid input'])
end
cLen = length(codes);
if mod(cLen,2) == 1
    add = 1;
    codes = [codes,'0'];
    cLen = cLen + 1;
    
end

codes_odd = 0;
codes_even = 0;
N = fs * T;
t = (0:N-1)/fs;
sigI = sin(2*pi*f*t);
sigQ = cos(2*pi*f*t);
sigL = length(sigI);

sig = zeros(1,sigL*cLen/2);
for i = 1 : cLen/2
    if codes(i*2-1) =='0'
        codes_odd = 0;
    else
        codes_odd =1;
    end
    if codes(i*2) == '0'
        codes_even = 0;
    else
        codes_even = 1;
    end
    fI = (1-2*codes_odd)*sqrt(2)/2;
    fQ = (1-2*codes_even)*sqrt(2)/2;
    sig((i-1)*sigL+1:i*sigL) = fI*sigI+fQ*sigQ;
end
%sig = sig / max(abs(sig));
audiowrite('qpsk.wav',sig,fs);

[sig_,fs_] = audioread('qpsk.wav');
sig_ = sig_';
N = fs_*0.025;
num_symb = floor(length(sig_)/N);
Base_signal_I = sin(2*pi*f*t);
Base_signal_I = repmat(Base_signal_I, 1, num_symb); 
Base_signal_Q = cos(2*pi*f*t);
Base_signal_Q = repmat(Base_signal_Q, 1, num_symb); 
yI = sig_(1:N*num_symb).*Base_signal_I;
yQ = sig_(1:N*num_symb).*Base_signal_Q;
yI = lowpass(yI,f,fs);
yQ = lowpass(yQ,f,fs);


codesI = zeros(1,num_symb);
codesQ = zeros(1,num_symb);
if add ==0
    codesR = zeros(1,num_symb*2);
else
        codesR = zeros(1,num_symb*2-1);
end



for i = 1:num_symb
    codesQ(i) = sum(yQ((i-1)*N +1 : i*N));
            codesI(i) = sum(yI((i-1)*N +1 : i*N));

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
    if add == 1 && i == num_symb -1
    else
        codesR(i*2+2) = y2rmap(i+1);
    end
end
    disp(codesR)


