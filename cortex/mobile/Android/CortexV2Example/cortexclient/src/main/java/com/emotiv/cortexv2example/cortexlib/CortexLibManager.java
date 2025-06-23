package com.emotiv.cortexv2example.cortexlib;

import android.app.Application;

import com.emotiv.CortexLibInterface;
import com.emotiv.CortexLogHandler;
import com.emotiv.CortexLogLevel;
import com.emotiv.EmotivLibraryLoader;
import com.emotiv.CortexLib;
import com.emotiv.cortexv2example.interfaces.CortexConnectionInterface;

public class CortexLibManager {
    // This method should be called before start(), stop()
    public static void load(Application application) {
        EmotivLibraryLoader loader = new EmotivLibraryLoader(application);
        loader.load();
    }

    // This method should be called after load() and before start()
    public static void setLogHandler(CortexLogLevel logLevel, CortexLogHandler logHandler) {
        CortexLib.setLogHandler(logLevel, logHandler);
    }

    // CortexLib requires some permissions and
    // this method should be called after users granted permissions to the app
    public static boolean start(CortexLibInterface cortexLibInterface) {
        return CortexLib.start(cortexLibInterface);
    }

    // This method should be called before the app is about to quit
    public static void stop() {
        CortexLib.stop();
    }

    // This method should be called after CortexLibInterface.onCortexStarted() callback is triggered
    public static CortexConnection createConnection(CortexConnectionInterface cortexConnectionInterface) {
        CortexConnection connection = new CortexConnection();
        connection.setCortexLibConnectionInterface(cortexConnectionInterface);
        connection.open();
        return connection;
    }

    // This method should be called before stop()
    public static void closeConnection(CortexConnection connection) {
        connection.close();
    }
}
