from socket import *
from cv2 import *
import numpy
s = socket()
s.bind(('127.0.0.1', 27014))
s.listen(1)
row = []
column = []
msg = ''
done = False
ch = 0
sent= 0
while True:
    conn, addr = s.accept()
    while not done:        
        while (len(column) < 480) and not done:
            while True:
                try:
                    msg = conn.recv(640*3)
                    if not msg:
                        continue
                    else:
                        if ch == '27':
                            done = True
                            conn.send('exit')
                            break
                        else:
                            for x in range(0, 640):
                                pixel = []
                                for y in range(0, 3):
                                    pixel.append(ord(msg[(x*3)+y]))
                                row.append(pixel)
                            conn.send('next')
                            break
                except Exception:
                    print 'error'
                    for x in range(0, 479 - len(column)):
                        column.append(x)
                    break
            column.append(row)
            row = []
        if not conn:
            continue
        else:
            if not done:
                img = numpy.array(column, numpy.uint8)
                imshow('win', img)
                ch = waitKey(1)
                column = []
                sent += 1
                print sent
            else:
                conn.close()
                s.close()
                break
