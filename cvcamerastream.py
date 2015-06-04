from cv2 import *
from socket import *
cam = VideoCapture(0)
socket = socket()

a, b = cam.read()
msg = ''
num_sent = 0
for x in range(0, 480):
    for y in range(0, 640):
        for z in range(0, 3):
            msg += chr(b[x][y][z])

socket.connect(('localhost', 27014))
socket.send(msg)
num_sent += 1
print 'Msgs sent: ' + str(num_sent)
try:
   while True:
        a, b = cam.read()
        msg = ''
        for x in range(0, 480):
            for y in range(0, 640):
                for z in range(0, 3):
                    msg += chr(b[x][y][z])

        socket.send(msg)
        num_sent += 1
        print 'Msgs sent: ' + str(num_sent)
except Exception:
    cam.release()
    socket.close()

    
