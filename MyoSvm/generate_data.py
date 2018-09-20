from listener import Listener
import myo as libmyo
import numpy as np
import time
from random import randint



#libmyo.init(r'C:\Users\Michael\Documents\School\Myo\myo-sdk-win-0.9.0\bin\myo64.dll')
libmyo.init(r'D:\SignLanguageProject\myo-sdk-win-0.9.0\bin\myo64.dll')
# libmyo.init(r'/mnt/d/SignLanguageProject/myo-sdk-win-0.9.0/bin/myo64.dll')
hub = libmyo.Hub()
listener = Listener(250)

letters = ['a', 'b', 'c', 'e', 'f', 'o']
record_count = 10
batch_size = 2

repetitons = record_count // batch_size

# Get data for each letter
for letter in letters:
    input("Press Enter to begin recording the letter {0}. . . ".format(letter))
    for k in range(repetitons):
        for i in range(batch_size):
            #print("recording instance {0} of letter {1}".format(i, letter))
            print("Rest")
            time.sleep(2)
            print("Make the letter {0} and Hold".format(letter))
            time.sleep(.5)
            hub.run(listener, 2500)
            data = np.array(listener.data)
            np.savetxt("data/{0}-{1}-{2}.csv".format(i+k*batch_size, letter, randint(10000, 99999)), data, delimiter=',')
        print("Please take off and put the Myo back on")
        input("Press Enter to continue ...")

