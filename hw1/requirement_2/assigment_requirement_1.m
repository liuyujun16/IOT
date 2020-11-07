


[k,FF] = audioread('hoho.wav');
info = audioinfo('hoho.wav');
sound(k,FF)
tt = 0:seconds(1/FF):seconds(info.Duration);
tt = tt(1:end-1);
plot(tt, k, 'b-');
set(gcf, 'units','normalized','outerposition',[0 0 1 1]);

 
