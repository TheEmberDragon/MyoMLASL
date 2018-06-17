import fnmatch
import numpy as np
import os
import pandas as pd
import re
from functools import reduce
from tkinter import Tk
from tkinter import filedialog

root = Tk()
root.withdraw()

# Get the directory containing the csv files
curDirectory = filedialog.askdirectory()

# Get a list of ids that are being used for the csv files
dataIDList = []
for file in os.listdir(curDirectory):
    if fnmatch.fnmatch(file, 'emg*.csv'):
        dataIDList.append((re.split('-',file))[1])

# Loop through the ids and combine the respective csv files
for dataID in dataIDList:
    print(dataID)
    # Get the data frames from the CSV files
    dataFrameAccel = pd.read_csv(curDirectory + "/accelerometer-" + dataID)
    dataFrameEmg = pd.read_csv(curDirectory + "/emg-" + dataID)
    dataFrameGyro = pd.read_csv(curDirectory + "/gyro-" + dataID)
    dataFrameOrientation = pd.read_csv(curDirectory + "/orientation-" + dataID)
    dataFrameEuler = pd.read_csv(curDirectory + "/orientationEuler-" + dataID)

    # print(dataFrameAccel)
    # print(dataFrameEmg)
    # print(dataFrameGyro)
    # print(dataFrameOrientation)
    # print(dataFrameEuler)

    # Get a list of all the data frames needed
    dataFrames = [dataFrameAccel, dataFrameEmg, dataFrameGyro, dataFrameOrientation, dataFrameEuler]

    # Combine the data frames on the timestamp column
    dataFrameResult = reduce(lambda left, right: pd.merge(left, right, on='timestamp', how='outer'), dataFrames)

    # Sort the data frame by the timestamp value
    dataFrameResult = dataFrameResult.sort_values(by=['timestamp'])

    # Fill blank values forward (taking previous value) whenever possible
    dataFrameResult = dataFrameResult.fillna(method='pad')

    # Fill the other blank values with zeros
    dataFrameResult = dataFrameResult.fillna(0)

    # print(dataFrameResult)

    # Write the data frame result to a CSV
    dataFrameResult.to_csv("result-" + dataID)