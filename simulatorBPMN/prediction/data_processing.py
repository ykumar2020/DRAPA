from datetime import date
from encodings import utf_8
from gc import callbacks
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
#%matplotlib inline
import os
import tensorflow as tf
from tensorflow import keras
from tensorflow.keras import layers
from tensorflow.keras.callbacks import ModelCheckpoint

class UsageDataProcessing:
    def usagefile(self, filename):
        current_work_dir = os.path.dirname(__file__)
        print("current_work_dir:" , current_work_dir)
        weight_path = os.path.join(current_work_dir, "./"+ filename +".csv")
        
        res01_data = open(current_work_dir + "./res01.csv", 'w', encoding= "utf_8")
        res02_data = open(current_work_dir + "./res02.csv", 'w', encoding= "utf_8")
        res03_data = open(current_work_dir + "./res03.csv", 'w', encoding= "utf_8")
        res04_data = open(current_work_dir + "./res04.csv", 'w', encoding= "utf_8")
        with open (weight_path, "r", encoding= "utf_8") as f:
            lines = f.readlines()
            print(len(lines))
            for line in lines: 
                #print(lines[0])
                if("res01" in line):
                    #print(lines[0][:5])
                    print(line.strip(), file= res01_data)
                if("res02" in line):
                    print(line.strip(), file= res02_data)
                if("res03" in line):
                    print(line.strip(), file= res03_data)
                if("res04" in line):
                    print(line.strip(), file= res04_data)
            f.close()

class PridictiveAnalytics:
    
    # init 
    def __init__(self, res_name):
        self.res_name = res_name
        self.current_work_dir = os.path.dirname(__file__)
        df =pd.read_csv(self.current_work_dir + './'+ self.res_name +'.csv', header =None)
        df.head()
        print(df.head())
        print(len(df))
        data1 = df.values[:, 3]
        #print(data1)
        data2 = [i for i in range(0, len(df.values))]
        #print(data2)
        self.data = pd.DataFrame(data1, data2)
        
    # Data Processing  
    def series_to_supervised(self, data, n_in=1, n_out=1, dropnan=True):
        n_vars = 1 if type(data) is list else data.shape[1]
        df = pd.DataFrame(data)
        cols, names = list(), list()
        # input sequence (t-n, ... t-1)
        for i in range(n_in, 0, -1):
            cols.append(df.shift(i))
            names += [('var%d(t-%d)' % (j+1, i)) for j in range(n_vars)]
        # forecast sequence (t, t+1, ... t+n)
        for i in range(0, n_out):
            cols.append(df.shift(-i))
            if i == 0:
                names += [('var%d(t)' % (j+1)) for j in range(n_vars)]
            else:
                names += [('var%d(t+%d)' % (j+1, i)) for j in range(n_vars)]
        # put it all together
        agg = pd.concat(cols, axis=1)
        agg.columns = names
        # drop rows with NaN values
        if dropnan:
            agg.dropna(inplace=True)
        return agg
    
    def get_data(self, train_days, valid_days, nb_history, nb_prediction):
        #scaler = MinMaxScaler(feature_range=(0,1))

        #scaled_data = scaler.fit_transform(data)
        scaled_data = self.data
        redf = self.series_to_supervised(scaled_data, nb_history, nb_prediction)
        print(redf.info())
        print(redf.head())

        train_days = train_days
        valid_days = valid_days
        values = redf.values
        #print(len(values), values)
        train = values[:train_days, :]
        valid = values[train_days:train_days+valid_days, :]
        test = values[train_days+valid_days:, :]
        train_X, train_y = train[:, :nb_history], train[:, nb_history:]
        valid_X, valid_y = valid[:, :nb_history], valid[:, nb_history:]
        test_X, test_y = test[:, :nb_history], test[:, nb_history:]
        #print(train_X.shape, train_y.shape, valid_X.shape, valid_y.shape, test_X.shape, test_y.shape)

        train_X = train_X.reshape((train_X.shape[0], 1, train_X.shape[1]))
        valid_X = valid_X.reshape((valid_X.shape[0], 1, valid_X.shape[1]))
        test_X = test_X.reshape((test_X.shape[0], 1, test_X.shape[1]))
        print(train_X.shape, train_y.shape, valid_X.shape, valid_y.shape, test_X.shape, test_y.shape)

        train_X = tf.convert_to_tensor(train_X, dtype=tf.float32)
        train_y = tf.convert_to_tensor(train_y, dtype=tf.float32)
        valid_X = tf.convert_to_tensor(valid_X, dtype=tf.float32)
        valid_y = tf.convert_to_tensor(valid_y, dtype=tf.float32)
        test_X = tf.convert_to_tensor(test_X, dtype=tf.float32)
        test_y = tf.convert_to_tensor(test_y, dtype=tf.float32)

        return values, train_X, train_y, valid_X, valid_y,test_X, test_y
    
    def PredictionModel(self, train_days, valid_days, nb_history, nb_prediction):
        values, train_X, train_y, valid_X, valid_y,test_X, test_y = self.get_data(train_days, valid_days, nb_history, nb_prediction)

        filepath = self.current_work_dir + './models/{val_mape:.2f}_{epoch:02d}' + f'history_{nb_history}_prediction_{nb_prediction}'

        # checkpoint = ModelCheckpoint(
        #     filepath = filepath,
        #     save_weight_only = True,
        #     monitor= 'val_mape',
        #     mode = 'auto',
        #     save_best_only = True)
        # callbacks_list = [checkpoint]
        
        model = keras.Sequential()
        # model.add(layers.LSTM(50, activation='relu',input_shape=(train_X.shape[1], train_X.shape[2]), return_sequences=True))
        model.add(layers.LSTM(50, activation='relu',input_shape=train_X.shape[1:], return_sequences=True))
        model.add(layers.Dropout(0.1))

        model.add(layers.LSTM(50, activation='relu', return_sequences=True))
        model.add(layers.Dropout(0.1))

        model.add(layers.LSTM(50, activation='relu'))
        model.add(layers.Dropout(0.1))

        model.add(layers.Dense(50, activation='relu'))
        #model.add(layers.Dense(1, activation='linear'))
        model.add(layers.Dense(1))

        # model.compile(loss='mean_squared_error', optimizer='adam', metrics = 'RootMeanSquaredError') 
        model.compile(loss='mean_squared_error', optimizer='adam', metrics = 'mape') 

        LSTM = model.fit(train_X, train_y, epochs=1000
                    , batch_size=32, validation_data=(valid_X, valid_y), verbose=2, shuffle=False)#, callbacks = callbacks_list)
        
        model.save(self.current_work_dir + "/"+ self.res_name + "model/")
        
        print(model.summary())
        print([layer.name for layer in model.layers] )

class TestModel:
    
    def testSimple(self):
        
        list_resname = ["res01", "res02", "res03", "res04"]
        udp = UsageDataProcessing()
        udp.usagefile("usage")
        
        # nb_history_list = [5, 10, 15, 20, 25,30]
        # nb_prediction_list = [1,2,3,4,5]

        for resname in list_resname:
            pmodel = PridictiveAnalytics(resname)
            data = pmodel.data
            # # train_days = 450
            # # valid_days = 70
            train_days = int(len(data) * 0.6)
            valid_days = int(len(data) * 0.2)
            nb_history = 5
            nb_prediction = 1
            pmodel.PredictionModel(train_days, valid_days, nb_history, nb_prediction)

    def testBest(self):
        
        list_resname = ["res01", "res02", "res03", "res04"]
        udp = UsageDataProcessing()
        udp.usagefile("usage")
        
        nb_history_list = [5, 10, 15, 20, 25,30]
        nb_prediction_list = [1,2,3,4,5]

        for resname in list_resname:
            pmodel = PridictiveAnalytics(resname)
            data = pmodel.data
            # # train_days = 450
            # # valid_days = 70
            train_days = int(len(data) * 0.6)
            valid_days = int(len(data) * 0.2)
            
            #nb_history = 5
            #nb_prediction = 1
            for nb_history in nb_history_list:
                for nb_prediction in nb_prediction_list:
                    pmodel.PredictionModel(train_days, valid_days, nb_history, nb_prediction)
    
if __name__ == '__main__':
    testModel = TestModel()
    testModel.testSimple()
    
   