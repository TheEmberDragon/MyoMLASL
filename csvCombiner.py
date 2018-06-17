import numpy as np
import pandas as pd
from functools import reduce

dataID = "-1527282613.csv"

#Get the data frames from the CSV files
dataFrameAccel = pd.read_csv("accelerometer" + dataID)
dataFrameEmg = pd.read_csv("emg" + dataID)
dataFrameGyro = pd.read_csv("gyro" + dataID)
dataFrameOrientation = pd.read_csv("orientation" + dataID)
dataFrameEuler = pd.read_csv("orientationEuler" + dataID)

#print(dataFrameAccel)
#print(dataFrameEmg)
#print(dataFrameGyro)
#print(dataFrameOrientation)
#print(dataFrameEuler)

#Get a list of all the data frames needed
dataFrames = [dataFrameAccel,dataFrameEmg,dataFrameGyro,dataFrameOrientation,dataFrameEuler]

#Combine the data frames on the timestamp column
dataFrameResult = reduce(lambda left,right: pd.merge(left, right, on='timestamp', how='outer'), dataFrames)

#Sort the data frame by the timestamp value
dataFrameResult = dataFrameResult.sort_values(by=['timestamp'])

#Fill blank values forward (taking previous value) whenever possible
dataFrameResult = dataFrameResult.fillna(method='pad')

#Fill the other blank values with back propagation
dataFrameResult = dataFrameResult.fillna(method='bfill')

#print(dataFrameResult)

#Write the data frame result to a CSV
dataFrameResult.to_csv("result" + dataID)