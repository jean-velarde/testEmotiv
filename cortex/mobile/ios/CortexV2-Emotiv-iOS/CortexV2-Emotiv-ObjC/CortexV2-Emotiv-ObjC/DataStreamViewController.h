//
//  DataStreamViewController.h
//  CortexV2-Emotiv-ObjC
//
//  Created by nvtu on 2/24/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "CortexConnection.h"
#import "HeadsetTableViewCell.h"

@interface DataStreamViewController : UIViewController<UITableViewDataSource, UITableViewDelegate, ASWebAuthenticationPresentationContextProviding> {
    CortexConnection *client;
    NSString *currentUser;
    NSString *connectedHeadsetId;
    NSString *createdVirtualHeadsetId;
    NSArray *headsetList;
    BOOL activateSession;
    NSString *license;
    NSString *token;
    NSString *sessionId;
    NSString *stream;
    NSString *action;
    NSString *recordId;
    NSString *markerId;
}
- (void) onCortexStarted;
- (void) handleCortexEvent;
- (void) handleUI;

@property (weak, nonatomic) IBOutlet UIView *authorizeView;
@property (weak, nonatomic) IBOutlet UIView *trainingView;
@property (weak, nonatomic) IBOutlet UIView *dataView;
@property (weak, nonatomic) IBOutlet UIView *subscribeView;
@property (weak, nonatomic) IBOutlet UIView *markerView;
@property (weak, nonatomic) IBOutlet UIView *virtualHeadsetView;
@property (weak, nonatomic) IBOutlet UIButton *getUserLoginBtn;
@property (weak, nonatomic) IBOutlet UIButton *authorizeBtn;
@property (weak, nonatomic) IBOutlet UIButton *subscribeTrainingBtn;
@property (weak, nonatomic) IBOutlet UIButton *loadProfileBtn;
@property (weak, nonatomic) IBOutlet UIButton *training1Btn;
@property (weak, nonatomic) IBOutlet UIButton *training2Btn;
@property (weak, nonatomic) IBOutlet UIButton *createProfileBtn;
@property (weak, nonatomic) IBOutlet UIButton *queryHeadsetBtn;
@property (weak, nonatomic) IBOutlet UIButton *createSessionBtn;
@property (weak, nonatomic) IBOutlet UIButton *createRecordBtn;
@property (weak, nonatomic) IBOutlet UIButton *injectMarkerBtn;
@property (weak, nonatomic) IBOutlet UIButton *stopRecordBtn;
@property (weak, nonatomic) IBOutlet UIButton *openTrainingViewBtn;
@property (weak, nonatomic) IBOutlet UIButton *unsubscribeBtn;
@property (weak, nonatomic) IBOutlet UIButton *getLicenseInfoBtn;
@property (weak, nonatomic) IBOutlet UIButton *getUserInformationBtn;
@property (weak, nonatomic) IBOutlet UIButton *subcribeBtn;
@property (weak, nonatomic) IBOutlet UITableView *headsetTableView;
@property (weak, nonatomic) IBOutlet UIButton *disconnectHeasetBtn;
@property (weak, nonatomic) IBOutlet UIButton *createVirtualHSBtn;

@end

