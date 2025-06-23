//
//  AppDelegate.m
//  CortexV2-Emotiv-ObjC
//
//  Created by nvtu on 2/24/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

#import "AppDelegate.h"
#import "CortexConnection.h"
#import "DataStreamViewController.h"
#include <EmotivCortexLib/CortexLib.h>
@interface AppDelegate ()

@end

@implementation AppDelegate


- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions {
    // Override point for customization after application launch.
    NSLog(@"operatingSystemVersionString %@", [[NSProcessInfo processInfo] operatingSystemVersionString]);

    if (![[NSProcessInfo processInfo] isOperatingSystemAtLeastVersion:(NSOperatingSystemVersion){.majorVersion = 13, .minorVersion = 0, .patchVersion = 0}]) {
        DataStreamViewController* mainController = (DataStreamViewController*)  self.window.rootViewController;
        [CortexLib start:^(void){
            [mainController onCortexStarted];
        }];
    }
    return YES;
}

- (void) applicationWillTerminate:(UIApplication *)application
{
    [CortexLib stop];
}
#pragma mark - UISceneSession lifecycle


- (UISceneConfiguration *)application:(UIApplication *)application configurationForConnectingSceneSession:(UISceneSession *)connectingSceneSession options:(UISceneConnectionOptions *)options {
    // Called when a new scene session is being created.
    // Use this method to select a configuration to create the new scene with.
    return [[UISceneConfiguration alloc] initWithName:@"Default Configuration" sessionRole:connectingSceneSession.role];
}

- (void)application:(UIApplication *)application didDiscardSceneSessions:(NSSet<UISceneSession *> *)sceneSessions {
    // Called when the user discards a scene session.
    // If any sessions were discarded while the application was not running, this will be called shortly after application:didFinishLaunchingWithOptions.
    // Use this method to release any resources that were specific to the discarded scenes, as they will not return.
}


@end
