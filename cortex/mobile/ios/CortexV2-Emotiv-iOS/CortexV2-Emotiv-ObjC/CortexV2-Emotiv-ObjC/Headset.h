//
//  Headset.h
//  CortexV2-Emotiv-ObjC
//
//  Created by nvtu on 2/24/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface Headset : NSObject {
@public
    NSString *headsetId;
    NSString *label;
    NSString *connectedBy;
    NSString *status;
    BOOL isVirtualHeadset;
}

- (id) init;
- (id) initJson : (NSDictionary *) jHeadset;
- (NSString *) toString;

// these function use in Swfit class when run Swfit example
- (NSString *) getHeadsetId;
- (NSString *) getHeadsetStatus;
- (NSString *) getConnectedBy;
- (BOOL)isVirtualHeadset;
@end

NS_ASSUME_NONNULL_END
