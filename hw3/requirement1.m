clear all;
close all;
clc;

fs = 48000;
f = 18000;
time = 0.01;
t = 0:1/fs:time;

N = fs *time;
impulse = sin(2*pi*f*t);

pause0 = zeros(1,480);
pause1 = zeros(1,960);


binstr = input('enter a binary:','s');
bin = [];
n_c=length(binstr);
inv_c=regexp(binstr,'[a-z_A-Z2-9]');
if(~isempty(inv_c))
    disp(['Invalid char: ' binstr(inv_c) ' idx= ' num2str(inv_c)])
else
    disp([binstr ' Valid input'])
end
for i=1:length(binstr)
    if(binstr(i) == '1')
        bin = [bin,impulse,pause1];
    else
        bin = [bin,impulse,pause0];
    end
end
bin = [pause1,bin,impulse];
figure(1);
plot(bin);
audiowrite('first.wav',bin,fs);




[data,fs] = audioread('first.wav');%데이터를 받기 float 형태로

[n,~] = size(data); %데이터의 길이 

window = fs*time;%윈도우 그키 설정 
impulse_fft = zeros(n,1);
for i = 1 : n-window
    y = fft(data(i:i+window-1));
       Y = abs(y);
       index_impulse = round(f/fs*window);
       impulse_fft(i) = max(Y(index_impulse-2:index_impulse+2));
       disp(index_impulse)

end
sliding_window = 5;
impulse_fft_tmp = impulse_fft;
for i = 1+sliding_window:1:n-sliding_window
    impulse_fft_tmp(i) = mean(impulse_fft(i-sliding_window:i+sliding_window));
end
impulse_fft = impulse_fft_tmp;
position_impulse = [];
half_window = 240;
for i = half_window+1:n-half_window
    if impulse_fft(i)>0.3 && impulse_fft(i) == max(impulse_fft(i-half_window:i+half_window))
        position_impulse = [position_impulse,i];
    end
end
[~,N]= size(position_impulse);
delta_impulse=zeros(1,N-1);
for i = 1:N-1    
    delta_impulse(i) = position_impulse(i+1) -  position_impulse(i) -480;
end


decode_message4 = zeros(1,N-1)-1;
for i = 1:N-1
    if delta_impulse(i) - 480 >-10 &&delta_impulse(i) - 480 <10
        decode_message4(i) = 0;
    elseif delta_impulse(i) - 960 >-10 &&delta_impulse(i) - 960 <10
        decode_message4(i) = 1;
   
    end
end
disp(decode_message4)

