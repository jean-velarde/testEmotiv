//
//  AppDelegate.swift
//  CortexV2-Example-Swift
//
//  Created by Emotiv Inc on 2/21/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

import UIKit

@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    var window: UIWindow?
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Override point for customization after application launch.
        let onCortexStarted: () -> () = {
            DispatchQueue.main.async() {
                let mainController = self.window?.rootViewController as!DataStreamViewController
                mainController.onCortexStarted()
            }
        }

        CortexLib.start(onCortexStarted)
        if let path = Bundle.main.path(forResource: "Info", ofType: "plist") {
            let nsDictionary = NSDictionary(contentsOfFile: path)
            let stream = nsDictionary!["STREAM_NAME"] as? String ?? ""
            
            let view = DataStreamViewController()
            view.stream = stream
            
            if stream == "eeg" || stream == "marker" || stream == "met" {
                NSLog("#")
                NSLog("#####")
                NSLog("Reminder: to subscribe to the EEG data stream or high resolution performance metrics. you must get an appropriate licence from Emotiv.")
                NSLog("#####")
                NSLog("#")
                view.activateSession = true
            }
            view.license = License
            self.window?.rootViewController = view
            self.window!.backgroundColor = UIColor.white

            self.window!.makeKeyAndVisible()
        }
        return true
    }
    
    func applicationWillTerminate(_ application: UIApplication) {
        CortexLib.stop()
    }
}

