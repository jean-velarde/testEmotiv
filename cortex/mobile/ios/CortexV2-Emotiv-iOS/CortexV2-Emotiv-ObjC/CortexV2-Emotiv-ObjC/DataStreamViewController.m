//
//  DataStreamViewController.m
//  CortexV2-Emotiv-ObjC
//
//  Created by nvtu on 2/24/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//


#import "DataStreamViewController.h"
#import "Config.h"

@implementation DataStreamViewController

const int ERROR_CLOUDTOKEN_REFRESH = -32130;

- (void) viewDidLoad {
    [super viewDidLoad];
    
    self.headsetTableView.dataSource = self;
    self.headsetTableView.delegate = self;
    [self.headsetTableView registerNib:[UINib nibWithNibName:@"HeadsetTableViewCell" bundle:nil] forCellReuseIdentifier:@"cellID"];
    headsetList = [[NSArray alloc] init];
    // Do any additional setup after loading the view.
}

- (void)viewDidAppear:(BOOL)animated {
    [super viewDidAppear:animated];
}

- (void) onCortexStarted {
    NSLog(@"DataStreamViewController onCortexStarted");
    client = [CortexConnection shareInstance];
    if (@available(iOS 13.0, *))
        [client setDisplayContex:self];
    [self handleCortexEvent];
    dispatch_async(dispatch_get_main_queue(), ^{
        [self handleUI];
    });
}

-(ASPresentationAnchor)presentationAnchorForWebAuthenticationSession:(ASWebAuthenticationSession *)session
{
    return self.view.window;
}

- (IBAction)logout:(id)sender
{
    if(currentUser != nil)
        [client logout:currentUser];
    else
        NSLog(@"you did not login any emotiv Id. Can not call logout");
}

- (IBAction)login:(id)sender
{
    [client startAuthentication:[Config getClientID]];
}

- (IBAction)getUserLogin:(id)sender {
    [client getUserLogin];
}

- (IBAction)authorize:(id)sender {
    [client authorize:[Config getClientID] secret:[Config getClientSecret] license:self->license debit:self->activateSession ? 1 : 0];
}

- (IBAction)queryHeadset:(id)sender {
    [client refreshHeadsetScan];
    [client queryHeadset:@""];
}

- (IBAction)createSession:(id)sender {
    [client createSession:self->token headsetId:self->connectedHeadsetId activate:self->activateSession];
}

- (IBAction)createRecord:(id)sender {
    [client createRecord:self->token sessionId:self->sessionId title:@"Cortex Examples Swift"];
}

- (IBAction)injectMarker:(id)sender {
    NSTimeInterval milisecondedDate = ([[NSDate date] timeIntervalSince1970] * 1000);
    [client injectMarker:self->token sessionId:self->sessionId label:@"test1" value:41 time:(int64_t)milisecondedDate];
}

- (IBAction)stopRecord:(id)sender {
    [client stopRecord:self->token sessionId:self->sessionId];
}

- (IBAction)openTrainingView:(id)sender {
    if (!self->sessionId.length) {
        NSLog(@"Please create session");
    } else {
        if ([self->stream isEqual:@"session"]) {
            [client closeSession:self->token sessionId:self->sessionId];
        } else {
            [self->_dataView setHidden:true];
            [self->_trainingView setHidden:false];
        }
    }
}

- (IBAction)subcribe:(id)sender {
    [client subscribe:self->token sessionId:self->sessionId stream:self->stream];
}

- (IBAction)unsubcribe:(id)sender {
    [client unsubscribe:self->token sessionId:self->sessionId stream:self->stream];
}

- (IBAction)getLicenseInfo:(id)sender {
    [client getLicenseInfos:self->token];
}

- (IBAction)getUserInfo:(id)sender {
    [client getUserInformation:self->token];
}

- (IBAction)subscribeTraining:(id)sender {
    [client subscribe:self->token sessionId:self->sessionId stream:@"sys"];
}

- (IBAction)loadProfile:(id)sender {
    [client loadProfile:self->token headsetId:self->connectedHeadsetId profileName:[Config getTrainingProfileName]];
}

- (IBAction)createProfile:(id)sender {
    [client createProfile:self->token profileName:[Config getTrainingProfileName] headsetId:connectedHeadsetId];
}

- (IBAction)clickTraining1:(id)sender {
    self->action = @"neutral";
    [client training:self->token sessionId:self->sessionId detection:self->stream action:self->action control:@"start"];
    [self enableButton:false];
    NSLog(@"waiting training result from Cortex");
}

- (IBAction)clickTraining2:(id)sender {
    self->action = [self->stream isEqual:@"mentalCommand"] ? @"push" : @"smile";
    [client training:self->token sessionId:self->sessionId detection:self->stream action:self->action control:@"start"];
    [self enableButton:false];
    NSLog(@"waiting training result from Cortex");
}

- (IBAction)disconnectHeadset:(id)sender
{
    if(connectedHeadsetId != nil)
       [client disconectHeadset:connectedHeadsetId];
    else
        NSLog(@"You did not connect any Emotiv headset.");
}

- (IBAction)createVirtualHeadset:(id)sender
{
    if(createdVirtualHeadsetId != nil)
        NSLog(@"You already created a virtual headset");
    else
        [client createVirtualHeadset:token];
}

- (IBAction)queryVirtualHeadset:(id)sender
{
    // query virtual headset will get virtual headset info from database
    // it does not replace for queryHeadset api
    // virtual headset also appears in queryHeadset when it set power on
    [client queryVirtualHeadset:token];
}

- (IBAction)deleteVirtualHeadset:(id)sender
{
    [client deleteVirtualHeadset:token uuid:createdVirtualHeadsetId];
}

- (void) handleUI {
    NSString *path = [[NSBundle mainBundle] pathForResource:@"Info" ofType:@"plist"];
    if (path != nil) {
        NSDictionary * nsDictionary = [[NSDictionary alloc] initWithContentsOfFile:path];
        NSString *stream = nsDictionary[@"STREAM_NAME"];
        
        if (stream != nil) {
            NSLog(@"Running configuration: %@", stream);
            BOOL activateSession = false;
            if ([stream  isEqual: @"eeg"] || [stream  isEqual: @"marker"] || [stream isEqual:@"met"]) {
                NSLog(@"#");
                NSLog(@"#####");
                NSLog(@"Reminder: to subscribe to the EEG data stream or high resolution performance metrics. you must get an appropriate licence from Emotiv.");
                NSLog(@"#####");
                NSLog(@"#");
                activateSession = true;
            }
            self->stream = stream;
            self->license = [Config getLicense];
            self->activateSession = activateSession;
        }
        
    }
    NSString *nameStream = @"";
    if ([stream isEqual:@"mot"]) {
        nameStream = @"motion";
    } else if ([stream isEqual:@"eeg"]) {
        nameStream = @"eeg";
    } else if ([stream isEqual:@"pow"]) {
        nameStream = @"Band Power";
    } else if ([stream isEqual:@"com"]) {
        nameStream = @"MC";
    } else if ([stream isEqual:@"fac"]) {
        nameStream = @"FE";
    } else if ([stream isEqual:@"met"]) {
        nameStream = @"PM";
    } else if ([stream isEqual:@"facialExpression"]) {
        [_openTrainingViewBtn setHidden:false];
        [_subcribeBtn setHidden:true];
        [_unsubscribeBtn setHidden:true];
        [_training1Btn setTitle:@"train FE Neutral" forState:UIControlStateNormal];
        [_training2Btn setTitle:@"train FE Smile" forState:UIControlStateNormal];
    } else if ([stream isEqual:@"mentalCommand"]) {
        [_openTrainingViewBtn setHidden:false];
        [_subcribeBtn setHidden:true];
        [_unsubscribeBtn setHidden:true];
        [_training1Btn setTitle:@"train MC Neutral" forState:UIControlStateNormal];
        [_training2Btn setTitle:@"train MC Push" forState:UIControlStateNormal];
    } else if ([stream isEqual:@"marker"]) {
        [_subscribeView setHidden:true];
        [_markerView setHidden:false];
    }
    
    // auto handle login/authorize
    if(![stream isEqual:@"authorize"])
        [client getUserLogin];
    
    if([stream isEqual:@"virtualheadset"])
    {
        [_virtualHeadsetView setHidden:false];
    }
    else if(![stream isEqual:@"authorize"]) {
        [_authorizeView setHidden:true];
        [_dataView setHidden:false];
    }
    
    if([stream isEqual:@"session"]) {
        [_openTrainingViewBtn setHidden:false];
        [_disconnectHeasetBtn setHidden:false];
        [_subcribeBtn setHidden:true];
        [_unsubscribeBtn setHidden:true];
        [_openTrainingViewBtn setTitle:@"updateSession(Close)" forState:UIControlStateNormal];
    }

    [_subcribeBtn setTitle:[NSString stringWithFormat:@"%@ %@", @"subcribe ", nameStream] forState:UIControlStateNormal];
    [_unsubscribeBtn setTitle:[NSString stringWithFormat:@"%@ %@", @"unsubcribe ", nameStream] forState:UIControlStateNormal];
}

- (void) handleCortexEvent {
    __unsafe_unretained typeof(self) weakSelf = self;
    
    client.onErrorReceived = ^(NSString *method, int code, NSString *message) {
        // call authorize again if get error cloud token refresh
        if(code == ERROR_CLOUDTOKEN_REFRESH && [method isEqual:@"authorize"])
            [weakSelf->client authorize:[Config getClientID] secret:[Config getClientSecret] license:weakSelf->license debit:weakSelf->activateSession ? 1 : 0];
    };
    
    client.onAuthenticateOk = ^(NSString* code) {
        [weakSelf->client loginWithAuthenticationCode:code clientId:[Config getClientID] clientSecret:[Config getClientSecret]];
    };
    
    client.onLoggedinOk = ^(NSString * emotivId) {
        if(weakSelf->token == nil && ![weakSelf->stream isEqual:@"authorize"])
        {
            // auto authorize to get cortex token
            [weakSelf->client authorize:[Config getClientID] secret:[Config getClientSecret] license:weakSelf->license debit:weakSelf->activateSession ? 1 : 0];
        }
        NSLog(@"Logged in user %@", emotivId);
        weakSelf->currentUser = emotivId;
    };
    
    client.onLoggedoutOk = ^(NSString * emotivId) {
        weakSelf->currentUser = nil;
        NSLog(@"Logged out user %@", emotivId);
    };
    
    client.onGetUserLoginOk = ^(NSString * emotivId) {
        if ([emotivId isEqual: @""]) {
            NSLog(@"you did not login any emotiv Id");
            // auto login if don't run example authorize
            if(![weakSelf->stream isEqual:@"authorize"])
            {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakSelf->client startAuthentication:[Config getClientID]];
                });
            }
        }
        else
        {
            NSLog(@"You are logged in with the EmotivId %@", emotivId);
            weakSelf->currentUser = emotivId;
            // auto authorize to get cortex token
            if(![weakSelf->stream isEqual:@"authorize"])
            {
                [weakSelf->client authorize:[Config getClientID] secret:[Config getClientSecret] license:weakSelf->license debit:weakSelf->activateSession ? 1 : 0];
            }
        }
    };
    
    client.onUnsubscribeOk = ^(NSArray * streams) {
        NSLog(@"Unsubscription successful for data streams %@", streams);
    };
    
    client.onCloseSessionOk = ^(){
        NSLog(@"Session closed.");
    };
    
    client.onQueryHeadsetsOk = ^(NSArray *headsets) {
        NSLog(@"Query headset done.");
        weakSelf->headsetList = headsets;
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf->_headsetTableView reloadData];
        });
    };
    
    client.onHeadsetConnected = ^(NSString* headsetId) {
        NSLog(@"Get connected headset: %@", headsetId);
        // query headset again to reload status
        weakSelf->connectedHeadsetId = headsetId;
        dispatch_async(dispatch_get_main_queue(), ^{
            [NSTimer scheduledTimerWithTimeInterval:1 repeats:NO block:^(NSTimer *timer) {
                [weakSelf->client queryHeadset:@""];
            }];
        });
    };
    
    client.onCreateVirtualHeadsetOk = ^(NSString* hsid) {
        NSLog(@"create virtual headset successful");
        weakSelf->createdVirtualHeadsetId = hsid;
    };
    
    client.onQueryVirtualHeadsetOk = ^(NSArray* array) {
        if(array.count == 0)
        {
            dispatch_async(dispatch_get_main_queue(), ^{
                [weakSelf->_createVirtualHSBtn setEnabled:TRUE];
            });
        }
        else
        {
            NSDictionary *value = array.firstObject;
            weakSelf->createdVirtualHeadsetId = value[@"uuid"];
        }
        NSLog(@"query virtual headset done");
        NSLog(@"Data: %@", [array description]);
    };
    
    client.onDeleteVirtualHeadsetOk = ^(){
        NSLog(@"delete virtual headset successful");
        weakSelf->createdVirtualHeadsetId = nil;
    };
    
    client.onHeadsetDisconnected = ^() {
        NSLog(@"Get disconnected headset: %@", weakSelf->connectedHeadsetId);
        [weakSelf->client queryHeadset:@""];
    };
    
    client.onAuthorizeOk = ^(NSString *token) {
        weakSelf->token = token;
        NSLog(@"Authorize successful, token: %@",token);
        if(![weakSelf->stream isEqual:@"authorize"])
           [weakSelf->client queryVirtualHeadset:token];
    };
    
    client.onSubscribeOk = ^(NSArray * streams) {
        NSLog(@"Subscription successful for data streams %@", streams);
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf->_training1Btn setEnabled:true];
            [weakSelf->_training2Btn setEnabled:true];
        });
    };
    
    client.onSaveProfileOk = ^(NSString *profileName) {
        NSLog(@"Training profile saved %@", profileName);
    };
    
    client.onLoadProfileOk = ^(NSString *profileName) {
        NSLog(@"Training profile loaded %@",profileName);
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf->_training1Btn setEnabled:true];
            [weakSelf->_training2Btn setEnabled:true];
        });
    };
    
    client.onCreateProfileOk = ^(NSString *profileName) {
        NSLog(@"Training profile created %@", profileName);
    };

    
    client.onStreamDataReceived = ^(NSString *sessionId, NSString *stream, double time, NSArray *data) {
        if ([self->stream isEqual:@"facialExpression"] || [self->stream isEqual:@"mentalCommand"]) {
            if ([weakSelf isEvent: data event: @"Started"]) {
                NSLog(@"");
                NSLog(@"Please, focus on the action for a few seconds");
            } else if ([weakSelf isEvent: data event: @"Succeeded"]) {
                [weakSelf->client training: weakSelf->token sessionId: weakSelf->sessionId detection: weakSelf->stream action: weakSelf->action control: @"accept"];
            } else if ([weakSelf isEvent: data event: @"Failed"]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakSelf enableButton:true];
                });
            } else if ([weakSelf isEvent: data event: @"Completed"]) {
                dispatch_async(dispatch_get_main_queue(), ^{
                    [weakSelf enableButton:true];
                });
                NSLog(@"Well done! You successfully trained %@", weakSelf->action);
            }
        }
        NSLog(@"\(%@) \(%@)", stream, data);
    };
    
    
    client.onTrainingOk = ^(NSString *msg) {
        // this signal is not important
        // instead we need to watch the events from the sys stream
    };
    
    client.onCreateRecordOk = ^(NSString *recordId){
        weakSelf->recordId = recordId;
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf->_stopRecordBtn setEnabled:true];
            [weakSelf->_injectMarkerBtn setEnabled:true];
        });
        NSLog(@"Create Record Successful");
    };
    
    client.onInjectMarkerOk = ^(NSString *markerId) {
        NSLog(@"Inject marker OK, marker id %@", (markerId));
        weakSelf->markerId = markerId;
    };
    
    client.onStopRecordOk = ^(NSString *recordId) {
        dispatch_async(dispatch_get_main_queue(), ^{
            [weakSelf->_stopRecordBtn setEnabled:false];
            [weakSelf->_injectMarkerBtn setEnabled:false];
        });
        [weakSelf->client getRecordInfos:weakSelf->token recordId:weakSelf->recordId];
        NSLog(@"Stop Record Successful");
    };
    
    client.onCreateSessionOk = ^(NSString *sessionId) {
        weakSelf->sessionId = sessionId;
        NSLog(@"Create Session Successful");
    };
    
    client.onCloseSessionOk = ^(){
        NSLog(@"Close session successful");
    };
}

- (nonnull UITableViewCell *)tableView:(nonnull UITableView *)tableView cellForRowAtIndexPath:(nonnull NSIndexPath *)indexPath {
    NSString *cellIdentifier = @"cellID";
    
    HeadsetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier forIndexPath:indexPath];
    if (cell == nil) {
        cell = [[HeadsetTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    }
    Headset* hs = headsetList[indexPath.row];
    cell.headsetName.text = hs->headsetId;
    cell.status.text = hs->status;
    cell.connectedBy.text = hs->connectedBy;
    if(hs->isVirtualHeadset)
        cell.isVirtualHeadset.text = @"true";
    else
        cell.isVirtualHeadset.text = @"false";
    return cell;
}

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView
{
    return 1;
}

- (NSInteger)tableView:(nonnull UITableView *)tableView numberOfRowsInSection:(NSInteger)section {
    return [self->headsetList count];
}

- (CGFloat)tableView:(UITableView *)tableView heightForRowAtIndexPath:(NSIndexPath *)indexPath {
    return 50;
}

- (CGFloat)tableView:(UITableView *)tableView heightForHeaderInSection:(NSInteger)section {
    return 50;
}

- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    Headset* hs = headsetList[indexPath.row];
    if(![hs->status isEqual:@"connected"])
    {
        NSLog(@"Selec headset %@ to connect.", hs->headsetId);
        [client connectHeadset:hs->headsetId];
    }
}

- (UIView *)tableView:(UITableView *)tableView viewForHeaderInSection:(NSInteger)section
{
    NSString *cellIdentifier = @"cellID";
    
    HeadsetTableViewCell *cell = [tableView dequeueReusableCellWithIdentifier:cellIdentifier];
    if (cell == nil) {
        cell = [[HeadsetTableViewCell alloc] initWithStyle:UITableViewCellStyleDefault reuseIdentifier:cellIdentifier];
    }
    return cell;
}

- (BOOL) isEvent: (NSArray *) data event: (NSString *) event  {
    for (NSString * val in data) {
        if ([val containsString:event]) {
            return true;
        }
    }
    return false;
}

- (void) enableButton: (BOOL) enable {
    [_training1Btn setEnabled:enable];
    [_training2Btn setEnabled:enable];
    [_createProfileBtn setEnabled:enable];
    [_loadProfileBtn setEnabled:enable];
    [_subscribeTrainingBtn setEnabled:enable];
}

@end
