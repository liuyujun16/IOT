import numpy as np
import sys

from PyQt5.QtCore import QThread
from PyQt5.QtCore import Qt

from PyQt5.QtWidgets import *
from PyQt5 import uic
from scipy.io.wavfile import write
import pyaudio
import wave
import librosa
import matplotlib.pyplot as plt


form_class = uic.loadUiType("gui.ui")[0]

fs = 48000
f = 440
time = 0.01
N = fs*time
Ts = 1 / fs  # sampling interval
t = np.arange(0, time, Ts)  # time array
impulse = np.sin(2 * np.pi * f * t)
pause0 = np.zeros(int(N))
pause1 = np.zeros(int(2*N))


chunk = 1024

global index
index = 0
kospi_top5 = {
    'original': ['01010101010101', '010101001010101', '101010101010', '10010101010', '1010101010','101010101010101','101000010001','11111000000'],
    'demodulate': [],
    'speed': [],
    'packet_loss' : [],
    'byte_error':[]
}
column_idx_lookup = {'original': 0, 'demodulate': 1, 'speed': 2,'packet_loss':3,'byte_error':4}
speed_graph = []
packet_graph = []
bit_graph = []
class ThreadClass(QThread):
    def __init__(self):
        super().__init__()

    def run(self):

        global flag
        flag = 0
        # the file name output you want to record into
        # set the chunk size of 1024 samples
        filename = "first.wav"

        # sample format
        FORMAT = pyaudio.paInt16
        channels = 1
        p = pyaudio.PyAudio()
        stream = p.open(format=FORMAT,
                        channels=channels,
                        rate=fs,
                        input=True,
                        output=True,
                        frames_per_buffer=chunk)
        frames = []

        while 1:
            if flag == 0:
                data = stream.read(chunk)
                frames.append(data)
            else:
                break
        stream.stop_stream()
        stream.close()
        p.terminate()
        wf = wave.open(filename, "wb")
        wf.setnchannels(channels)
        wf.setsampwidth(p.get_sample_size(FORMAT))
        wf.setframerate(fs)
        wf.writeframes(b"".join(frames))
        wf.close()
        print('working')


class WindowClass(QMainWindow, form_class) :
    def __init__(self) :
        super().__init__()
        self.setupUi(self)
        self.play_sound.clicked.connect(self.modulate_signal)
        self.start_record.clicked.connect(self.start_recording)
        self.stop_record.clicked.connect(self.stop_recording)
        self.demodulation.clicked.connect(self.demodulate)
        self.accept_button = QPushButton()
        self.cancel_button = QPushButton()
        self.init_ui()
        self.threadclass = ThreadClass()

        self.tableWidget.setRowCount(100)
        self.tableWidget.setColumnCount(5)
        self.tableWidget.setEditTriggers(QAbstractItemView.NoEditTriggers)

        self.setTableWidgetData()
        self.reload.clicked.connect(self.setTableWidgetData)
        self.draw.clicked.connect(self.drawingplot)


    def drawingplot(self):


        fig = plt.figure(figsize = (10,6))
        plt.subplot(311)
        plt.plot(speed_graph)
        plt.suptitle('speed')
        plt.grid()

        plt.subplot(312)
        plt.plot(bit_graph)

        plt.grid()

        plt.subplot(313)
        plt.plot(packet_graph)
        plt.suptitle('packet_loss_rate')

        plt.grid()

        plt.show()
    def setTableWidgetData(self):
        column_headers = ['愿数据', '解调数据', '传输速率','丢包率','比特错误率']
        self.tableWidget.setHorizontalHeaderLabels(column_headers)

        for k, v in kospi_top5.items():
            col = column_idx_lookup[k]
            print(col)
            for row, val in enumerate(v):
                item = QTableWidgetItem(val)
                if col == 2:
                    item.setTextAlignment(Qt.AlignVCenter | Qt.AlignRight)

                self.tableWidget.setItem(row, col, item)

        self.tableWidget.resizeColumnsToContents()
        self.tableWidget.resizeRowsToContents()

    def demodulate(self):
        global index

        audio_path = 'first.wav'
        data, sr = librosa.load(audio_path,sr=None)
        n = len(data)
        window = time*sr;
        impulse_fft = np.zeros(n)
        for i in range(int(n-window)):
            y = np.fft.fft(data[i:i+int(window)-1])
            y = np.abs(y)
            index_impulse = round(f / sr * window)
            impulse_fft[i] = max(y[index_impulse - 2:index_impulse + 2])


        sliding_window = 5
        impulse_fft_tmp = impulse_fft
        for i in range(1+sliding_window,n-sliding_window):
            impulse_fft_tmp[i] = np.mean(impulse_fft[i - sliding_window:i + sliding_window])
        impulse_fft = impulse_fft_tmp

        position_impulse = []
        half_window = window/2
        for i in range(int(half_window)+1,int(n-half_window)):
            if impulse_fft[i] > 0.3 and impulse_fft[i] == max(impulse_fft[i - int(half_window): int(i + half_window)]):
                 position_impulse.append(i)


        Num = len(position_impulse)
        delta_impulse =np.zeros(Num - 1)
        for i in range(Num-1):
            delta_impulse[i] = position_impulse[i + 1] - position_impulse[i] - window;
        decode_message4 = np.zeros(Num - 1) - 1

        for i in range(Num-1):
            if delta_impulse[i] - window > -10 and delta_impulse[i] - window < 10:
                decode_message4[i] = 0
            elif delta_impulse[i] - 2*window > -10 and delta_impulse[i] - 2*window < 10:
                decode_message4[i] = 1
        print(decode_message4)
        a = ''
        for element in decode_message4:
            if 1 == int(element):
                a = a + '1'
            else:
                a = a + '0'

        self.decode_binary.setText(a)
        print(kospi_top5.get('demodulate'))
        kospi_top5.get('demodulate').append(a)
        print(kospi_top5.get('demodulate'))
        print('aaaaa')
        print( kospi_top5.get('original')[index])
        print(a)

        if kospi_top5.get('original')[index] != a:
            kospi_top5.get('packet_loss').append('100%')
            packet_graph.append(100)
        else:
            kospi_top5.get('packet_loss').append('0%')
            packet_graph.append(0)
        bit_loss = 0
        for i in range(len(a)):
            try:
                kospi_top5.get('original')[index][i]
            except:
                break
            if a[i] == kospi_top5.get('original')[index][i]:
                bit_loss = bit_loss + 1
        print(bit_loss)

        bit_loss_rate =(1- bit_loss/len(kospi_top5.get('original')[index]))*100
        bit_graph.append(bit_loss_rate)
        kospi_top5.get('byte_error').append(str(bit_loss_rate)+'%')

        kospi_top5.get('speed').append(str(round(bit_loss*sr/n,3))+'bit/s')
        speed_graph.append(round(bit_loss*sr/n,3))
        index = index + 1
        print('이게 쓰벌 인덱스')
        print(index)


    def init_ui(self):
        pass

    def stop_recording(self):
        global flag
        flag =1
        print(flag)
    def start_recording(self):
        self.threadclass.start()

    def modulate_signal(self):
        binary = self.input_binary.toPlainText()
        print(binary)
        if binary == '':
            QMessageBox.about(self, "错误输入", "不能什么都不输入")
            return

        for element in binary:
            if element == '1' or element == '0':
                pass
            else:
                QMessageBox.about(self, "错误输入", "只能输入0和1")
                return
        sound = np.array(pause0)
        for i in binary:
            if i == '1':
                sound = np.append(sound,impulse)
                sound = np.append(sound,pause1)
            elif i == '0':
                sound=np.append(sound,impulse)
                sound=np.append(sound,pause0)
        scaled = np.int16(sound / np.max(np.abs(sound)) * 32767)

        write('first.wav', fs, scaled)
        wf = wave.open('first.wav', 'rb')

        data = wf.readframes(chunk)
        p = pyaudio.PyAudio()

        FORMAT = p.get_format_from_width(wf.getsampwidth())
        CHANNELS = wf.getnchannels()
        RATE = wf.getframerate()


        stream = p.open(format=FORMAT,

                        channels=CHANNELS,
                        rate=RATE,
                        frames_per_buffer=chunk,
                        output=True)
        while len(data) > 0:
            stream.write(data)
            data = wf.readframes(chunk)


if __name__ == "__main__" :
    app = QApplication(sys.argv)

    myWindow = WindowClass()

    myWindow.show()

    app.exec_()



