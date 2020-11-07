
function varargout = gui(varargin)

gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @gui_OpeningFcn, ...
                   'gui_OutputFcn',  @gui_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end

function gui_OpeningFcn(hObject, eventdata, handles, varargin)
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

function varargout = gui_OutputFcn(hObject, eventdata, handles) 
varargout{1} = handles.output;



function pushbutton10_Callback(hObject, eventdata, handles)

global sequence
global original
time = 0.01;
f = 440;

[data,fs] = audioread('record.wav');%데이터를 받기 float 형태로
N = fs *time;
[n,~] = size(data); %데이터의 길이 
window = fs*time;%윈도우 그키 설정 
impulse_fft = zeros(n,1);
for i = 1 : n-window
    y = fft(data(i:i+window-1));
       Y = abs(y);
       index_impulse = round(f/fs*window);
       impulse_fft(i) = max(Y(index_impulse-2:index_impulse+2));
end
sliding_window = 5;
impulse_fft_tmp = impulse_fft;
for i = 1+sliding_window:1:n-sliding_window
    impulse_fft_tmp(i) = mean(impulse_fft(i-sliding_window:i+sliding_window));
end
impulse_fft = impulse_fft_tmp;
position_impulse = [];
half_window = window/2;
for i = half_window+1:n-half_window
    if impulse_fft(i)>0.3 && impulse_fft(i) == max(impulse_fft(i-half_window:i+half_window))
        position_impulse = [position_impulse,i];
    end
end
[~,N]= size(position_impulse);
delta_impulse=zeros(1,N-1);
for i = 1:N-1    
    delta_impulse(i) = position_impulse(i+1) -  position_impulse(i) -window;
end
decode_message4 = zeros(1,N-1)-1;
for i = 1:N-1
    if delta_impulse(i) - window >-10 &&delta_impulse(i) - window <10
        decode_message4(i) = 0;
    elseif delta_impulse(i) - 2*window >-10 &&delta_impulse(i) - 2*window <10
        decode_message4(i) = 1;
   
    end
end
result = decode_message4 >=0 ;
real = decode_message4(result);
out = string(real);


A_cell = cellstr(num2str(real));

set(handles.edit2,'String', A_cell );
global log
global pocket_loss_rate
global bit_loss_rate
global bit_transfer_speed
add = string(original(sequence));
add = add + ' demodulate into ';
add = add + string(A_cell);

if original(sequence) ~= string(A_cell)
    pocket_loss = 'packet_loss_rate is 100% ';
    pocket_loss_rate = [pocket_loss_rate,100];
else
    pocket_loss = 'packet_loss_rate is 0% ';
    pocket_loss_rate = [pocket_loss_rate,0];
    
end
add = add + newline + pocket_loss;    

bit_loss_num = 0;
disp(original(sequence))
b_demodul = cell2mat(original(sequence));
a_demodul = real;
for i = 1:length(a_demodul)
    if i > length(b_demodul)
        break
    end
    if string(a_demodul(i)) == b_demodul(i)
        bit_loss_num = bit_loss_num +1 ;
    end
end
disp(bit_loss_num);
bit_loss =(1- (bit_loss_num / length(b_demodul))) * 100;
disp(bit_loss);
bit_loss_rate = [bit_loss_rate,bit_loss];
add = add + newline +'bit loss rate is ' +string(bit_loss) + '%';


bit_transfer_speed = [bit_transfer_speed, round(bit_loss_num*fs/n,3)];
add = add + newline+'bit transfer speed is ' +string(round(bit_loss_num*fs/n,3))+'bit/sec';


       

    
    
    
log = [log,add];

set(handles.log_window,'String', log );
sequence = sequence + 1;



function pushbutton1_Callback(hObject, eventdata, handles)
    fs = 48000;
    f = 440;
    time = 0.01;
    t = 0:1/fs:time;

    N = fs *time;
    impulse = sin(2*pi*f*t);

    pause0 = zeros(1,480);
    pause1 = zeros(1,960);
    edit1 = get(handles.edit1,'String');
    disp(edit1)
    
    bin = [];
    inv_c=regexp(edit1,'[a-z_A-Z2-9]');
    if(~isempty(inv_c))
        disp(['Invalid char: ' edit1(inv_c) ' idx= ' num2str(inv_c)])
    else
        disp([edit1 ' Valid input'])
    end
    for i=1:length(edit1)
        if(edit1(i) == '1')
            bin = [bin,impulse,pause1];
        else
            bin = [bin,impulse,pause0];
        end
    end
    bin = [pause1,bin,impulse];
    audiowrite('record.wav',bin,fs);
    [data,fs] = audioread('record.wav');
    sound(data,fs)





function edit1_Callback(hObject, eventdata, handles)
   

function edit1_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function edit2_Callback(hObject, eventdata, handles)



function edit2_CreateFcn(hObject, eventdata, handles)
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


function stop_record_Callback(hObject, eventdata, handles)
global recObj
stop(recObj); % Stop
y = getaudiodata(recObj);
audiowrite('record.wav',y,48000);
sound(y,48000);






function start_record_Callback(hObject, eventdata, handles)


global recObj
recObj = audiorecorder(48000,8,1); %create object
record(recObj); %start Recording


function plotting_Callback(hObject, eventdata, handles)
global pocket_loss_rate
global bit_loss_rate
global bit_transfer_speed


figure(1)
tiledlayout(3,1)
ax1 = nexttile;
plot(ax1,pocket_loss_rate,'-o');
title('丢包率')

ax2 = nexttile;
plot(ax2,bit_loss_rate,'-o');
title('比特错误率')


ax3 = nexttile;
plot(ax3,bit_transfer_speed,'-o');
title('传输速率')






function log_window_Callback(hObject, eventdata, handles)
% hObject    handle to log_window (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of log_window as text
%        str2double(get(hObject,'String')) returns contents of log_window as a double


% --- Executes during object creation, after setting all properties.
function log_window_CreateFcn(hObject, eventdata, handles)
% hObject    handle to log_window (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in refresh.
function refresh_Callback(hObject, eventdata, handles)


global original
global sequence
sequence = 1;
original  = {'0101010101','1010101010','1111111111','0000000000','1000010000','0000100001','1001001001','0010010010','1111100000','0000011111'};
global log
global pocket_loss_rate
global bit_loss_rate
global bit_transfer_speed
log = [];
pocket_loss_rate = [];
bit_loss_rate = [];
bit_transfer_speed = [];
% hObject    handle to refresh (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
