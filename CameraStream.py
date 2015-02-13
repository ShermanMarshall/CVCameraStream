from cv2 import *
from socket import *
cam = VideoCapture(0)
s = socket()
s.connect(('127.0.0.1', 27014))
msg = ''
while msg != 'exit':
    a, b = cam.read()
    for x in range(0, 480):
        string = ''
        for y in range(0, 640):
            for z in range(0, 3):
                string += chr(b[x][y][z])
        try:
            s.send(string)
        except Exception:
            print 'error. conn closed'
        while True:
            try:
                msg = s.recv(1024)
            except Exception:
                print 'error with conn'
            if not msg:
                continue
            else:
                if msg == 'next' or msg == 'exit':
                    break
                else:
                    print msg
