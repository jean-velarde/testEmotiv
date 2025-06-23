//
//  CortexConnection.m
//  CortexV2-Emotiv-ObjC
//
//  Created by nvtu on 2/24/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

#import "CortexConnection.h"
#import "Config.h"

@implementation CortexConnection

CortexConnection *cortexConnection;
const int WARNING_CODE_HEADSET_CONNECTED = 104;
+(id) shareInstance {
    if(!cortexConnection) {
        cortexConnection = [[CortexConnection alloc] initAfterCortexStarted];
    }
    return cortexConnection;
}

-(id) init {
    return nil;
}

-(id) initAfterCortexStarted {
    self = [super init];
    if(self) {
        nextRequestId = 1;
        methodForRequestId = [[NSMutableDictionary alloc] init];
        cortexClient = [[CortexClient alloc] init];
        cortexClient.delegate = self;
    }
    return self;
}

-(void)setDisplayContex:(id<ASWebAuthenticationPresentationContextProviding>)contex
{
    cortexClient.displayContext = contex;
}

-(void)startAuthentication:(NSString *)clientId
{
    [cortexClient authenticate:clientId];
}

#pragma mark Request Function

-(void) queryHeadset: (NSString *) headsetId {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    if (![headsetId  isEqual: @""]) {
        params[@"id"] = headsetId;
    }
    [self sendRequest:@"queryHeadsets" params:params];
}

-(void) logout:(NSString *)userName {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"username"] = userName;
    [self sendRequest:@"logout" params:params];
}

-(void) loginWithAuthenticationCode:(NSString *)code clientId:(NSString *)clientId clientSecret:(NSString *)clientSecret {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"clientId"] = clientId;
    params[@"clientSecret"] = clientSecret;
    params[@"code"] = code;
    [self sendRequest:@"loginWithAuthenticationCode" params:params];
}

-(void) getUserLogin {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    [self sendRequest:@"getUserLogin" params:params];
}

-(void) authorize: (NSString *) clientId secret: (NSString *) clientSecret license: (NSString *) license debit: (int) debit {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"clientId"] = clientId;
    params[@"clientSecret"] = clientSecret;
    if(![license  isEqual: @""]) {
        params[@"license"] = license;
    }
    params[@"debit"] = [NSNumber numberWithInt:debit];
    [self sendRequest:@"authorize" params:params];
}

-(void) connectHeadset:(NSString *)headsetId
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"command"] = @"connect";
    params[@"headset"] = headsetId;
    [self sendRequest:@"controlDevice" params:params];
}

-(void) disconectHeadset:(NSString *)headsetId
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"command"] = @"disconnect";
    params[@"headset"] = headsetId;
    [self sendRequest:@"controlDevice" params:params];
}

// should call refresh headset scan before queryHeadset
// See more detail https://emotiv.gitbook.io/cortex-api/headset/controldevice#headset-scanning-flow
-(void)refreshHeadsetScan
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"command"] = @"refresh";
    [self sendRequest:@"controlDevice" params:params];
}

-(void) createSession: (NSString *) token headsetId: (NSString *) headsetId activate: (BOOL) activate {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"headset"] = headsetId;
    params[@"status"] = activate ? @"active" : @"open";
    [self sendRequest:@"createSession" params:params];
}

-(void) closeSession: (NSString *) token sessionId: (NSString *) sessionId {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"status"] = @"close";
    [self sendRequest:@"updateSession" params:params];
}

-(void) subscribe: (NSString *) token sessionId: (NSString *) sessionId stream: (NSString *) stream {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"streams"] = [NSArray arrayWithObjects:stream, nil];
    [self sendRequest:@"subscribe" params:params];
}

-(void) unsubscribe: (NSString *) token sessionId: (NSString *) sessionId stream: (NSString *) stream {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"streams"] = [NSArray arrayWithObjects:stream, nil];
    [self sendRequest:@"unsubscribe" params:params];
}

-(void) queryProfile: (NSString *) token {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    [self sendRequest:@"queryProfile" params:params];
}

-(void) getUserInformation: (NSString *) token {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    [self sendRequest:@"getUserInformation" params:params];
}

-(void) getLicenseInfos: (NSString *) token {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    [self sendRequest:@"getLicenseInfo" params:params];
}

-(void) createProfile: (NSString *) token profileName: (NSString *) profileName headsetId:(nonnull NSString *)headsetId {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"profile"] = profileName;
    params[@"status"] = @"create";
    params[@"headset"] = headsetId;
    [self sendRequest:@"setupProfile" params:params];
}

-(void) loadProfile: (NSString *) token headsetId: (NSString *) headsetId profileName: (NSString *) profileName {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"headset"] = headsetId;
    params[@"profile"] = profileName;
    params[@"status"] = @"load";
    [self sendRequest:@"setupProfile" params:params];
}

-(void) saveProfile: (NSString *) token headsetId: (NSString *) headsetId profileName: (NSString *) profileName {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"headset"] = headsetId;
    params[@"profile"] = profileName;
    params[@"status"] = @"save";
    [self sendRequest:@"setupProfile" params:params];
}

-(void) getDetectionInfo: (NSString *) detection {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"detection"] = detection;
    [self sendRequest:@"getDetectionInfo" params:params];
}

-(void) training: (NSString *) token sessionId: (NSString *) sessionId detection: (NSString *) detection action: (NSString *) action control: (NSString *) control {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"detection"] = detection;
    params[@"action"] = action;
    params[@"status"] = control;
    [self sendRequest:@"training" params:params];
}

-(void) createRecord: (NSString *) token sessionId: (NSString *) sessionId title: (NSString *) title {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"title"] = title;
    [self sendRequest:@"createRecord" params:params];
}

-(void) stopRecord: (NSString *) token sessionId: (NSString *) sessionId {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    [self sendRequest:@"stopRecord" params:params];
}

-(void) getRecordInfos: (NSString *) token recordId: (NSString *) recordId {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"recordIds"] = [NSArray arrayWithObjects:recordId, nil];
    [self sendRequest:@"getRecordInfos" params:params];
}

-(void) injectMarker: (NSString *) token sessionId: (NSString *) sessionId label: (NSString *) label value: (int) value time: (int64_t) time {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"label"] = label;
    params[@"value"] = [NSNumber numberWithInt: value];
    params[@"port"] = @"Cortex Example";
    params[@"time"] = [NSNumber numberWithLongLong: time];
    [self sendRequest:@"injectMarker" params:params];
    
}
-(void) updateMarker: (NSString *) token sessionId: (NSString *) sessionId markerId: (NSString *) markerId time: (int64_t) time {
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"session"] = sessionId;
    params[@"markerId"] = markerId;
    params[@"time"] = [NSNumber numberWithLongLong: time];
    [self sendRequest:@"updateMarker" params:params];
}

-(void)createVirtualHeadset:(NSString *)token
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"type"] = @"INSIGHT";
    params[@"powerOn"] = [NSNumber numberWithBool:TRUE];
    NSMutableDictionary *cq_data = [[NSMutableDictionary alloc] init];
    // Config good CQ for Insight headset channel
    cq_data[@"AF3"] = @(4);
    cq_data[@"AF4"] = @(4);
    cq_data[@"T7"] = @(4);
    cq_data[@"T8"] = @(4);
    cq_data[@"Pz"] = @(4);
    params[@"cq"] = cq_data;
    [self sendRequest:@"createVirtualHeadset" params:params];
}

-(void)queryVirtualHeadset:(NSString *)token
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    [self sendRequest:@"queryVirtualHeadsets" params:params];
}

-(void)deleteVirtualHeadset:(NSString *)token uuid:(NSString *)uuid
{
    NSMutableDictionary *params = [[NSMutableDictionary alloc] init];
    params[@"cortexToken"] = token;
    params[@"virtualHeadsetId"] = uuid;
    [self sendRequest:@"deleteVirtualHeadset" params:params];
}

-(void) sendRequest: (NSString *) method params: (NSDictionary *) params {
    NSMutableDictionary *request = [[NSMutableDictionary alloc] init];
    request[@"jsonrpc"] = @"2.0";
    request[@"id"] = [NSNumber numberWithInt:nextRequestId];
    request[@"method"] = method;
    request[@"params"] = params;
    
    NSError *error;
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:request options:NSJSONWritingPrettyPrinted // Pass 0 if you don't care about the readability of the generated string
      error:&error];
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
    NSLog(@"request %@", jsonString);
    self->methodForRequestId[[NSString stringWithFormat:@"%d", self->nextRequestId]] = method;
    self->nextRequestId += 1;
    [cortexClient sendRequest:jsonString];
}

# pragma mark Handle Reponse Function

-(void) handleResponse: (NSString *) data {
    NSError *error;
    NSDictionary * response = [NSJSONSerialization JSONObjectWithData:[data dataUsingEncoding:NSUTF8StringEncoding] options:NSJSONReadingMutableLeaves error:&error];
    if (!error) {
        NSNumber *idResponse = [response objectForKey:@"id"];
        NSString *idString = [[response objectForKey:@"id"] stringValue];
        NSString *sid = [response objectForKey:@"sid"];
        NSDictionary *warning = [response objectForKey:@"warning"];
        if (idResponse > 0) {
            NSLog(@"receive %@", data);
            if ([methodForRequestId valueForKey:idString] != nil) {
                id result = [response objectForKey:@"result"];
                NSString *method = [methodForRequestId objectForKey:idString];
                NSDictionary *error = [response objectForKey:@"error"];
                if (error != NULL) {
                    [self emitError:method error:error];
                } else {
                    [self handleResponse:method result:result];
                }
                [methodForRequestId removeObjectForKey:idString];
            }
        } else if (sid != nil) {
            // this message has a sid (subscription id)
            // so this is some data from a data stream
            double time = [[response objectForKey:@"time"] doubleValue];
            NSArray *data = [[NSArray alloc] init];
            NSString *stream = @"";
            
            // find the data field inside the response
            for(NSString *key in response.allKeys) {
                id value = response[key];
                if (![key isEqual: @"sid"] && ![key isEqual: @"time"] && [value isKindOfClass:[NSArray class]] ) {
                    stream = key;
                    data = (NSArray *)value;
                }
            }
            if (self.onStreamDataReceived != nil) {
                self.onStreamDataReceived(sid, stream, time, data);
            }
        } else if (warning != nil) {
            // warning message
            NSLog(@"Cortex warning %@", data);
            int warningCode = [[warning objectForKey:@"code"] intValue];
            if(warningCode == WARNING_CODE_HEADSET_CONNECTED)
            {
                NSDictionary *message = [warning objectForKey:@"message"];
                NSString *headset = [message objectForKey:@"headsetId"];
                if(self.onHeadsetConnected != nil)
                {
                    self.onHeadsetConnected(headset);
                }
            }
        }
    }
}

-(void) handleResponse: (NSString *)  method result: (id) result {
    if ([method isEqual: @"queryHeadsets"]) {
        if ([result isKindOfClass:[NSArray class]]) {
        NSMutableArray *headsets = [[NSMutableArray alloc] init];
            NSArray *arr = (NSArray *) result;
            for(NSDictionary *val in arr) {
                Headset *headset = [[Headset alloc] initJson:val];
                [headsets addObject:headset];
            }
            if (self.onQueryHeadsetsOk != nil) {
                self.onQueryHeadsetsOk(headsets);
            }
        }
    } else if ([method isEqual:@"getUserLogin"]) {
        NSString *emotivId = @"";
        if ([result isKindOfClass:[NSArray class]]) {
            NSArray *arr = (NSArray *) result;
            if (arr.count != 0) {
                NSDictionary *obj = [arr objectAtIndex:0];
                NSString *currentOSUId = obj[@"currentOSUId"];
                NSString *loggedInOSUId = obj[@"loggedInOSUId"];
                if ([currentOSUId isEqualToString:loggedInOSUId]) {
                    emotivId = obj[@"username"];
                }
            }
            if (self.onGetUserLoginOk != nil) {
                self.onGetUserLoginOk(emotivId);
            }
        }
    } else if ([method isEqual: @"authorize"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *token = res[@"cortexToken"];
            if (self.onAuthorizeOk != nil) {
                self.onAuthorizeOk(token);
            }
        }
    } else if ([method isEqual: @"createSession"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *sessionId = res[@"id"];
            if (self.onCreateSessionOk != nil) {
                self.onCreateSessionOk(sessionId);
            }
        }
    } else if ([method isEqual:@"updateSession"]) {
        if (self.onCloseSessionOk != nil) {
            self.onCloseSessionOk();
        }
    } else if ([method isEqual: @"subscribe"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSArray *streams = [self parseSubscriptionResult:method result:res];
            if (streams.count != 0) {
                if (self.onSubscribeOk != nil) {
                    self.onSubscribeOk(streams);
                }
            }
        }
    } else if ([method isEqual: @"unsubscribe"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSArray *streams = [self parseSubscriptionResult:method result:res];
            if (streams.count != 0) {
                if (self.onUnsubscribeOk != nil) {
                    self.onUnsubscribeOk(streams);
                }
            }
        }
    } else if ([method isEqual: @"queryProfile"]) {
        NSMutableArray *profiles = [[NSMutableArray alloc] init];
        if ([result isKindOfClass:[NSArray class]]) {
            NSArray *arr = (NSArray *) result;
            for(NSDictionary *obj in arr) {
                [profiles addObject:obj[@"name"]];
            }
            if (self.onQueryProfileOk != nil) {
                self.onQueryProfileOk(profiles);
            }
        }
    } else if ([method isEqual: @"setupProfile"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *action = res[@"action"];
            NSString *name = res[@"name"];
            if ([action  isEqual: @"create"]) {
                if (self.onCreateProfileOk != nil) {
                    self.onCreateProfileOk(name);
                }
            } else if ([action  isEqual: @"load"]) {
                if (self.onLoadProfileOk != nil) {
                    self.onLoadProfileOk(name);
                }
            } else if ([action  isEqual: @"save"]) {
                if (self.onSaveProfileOk != nil) {
                    self.onSaveProfileOk(name);
                }
            }
        }
    } else if ([method isEqual:@"getDetectionInfo"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            [self handleGetDetectionInfo:(NSDictionary *) result];
        }
    } else if ([method isEqual:@"training"]) {
        if ([result isKindOfClass:[NSString class]]) {
            if (self.onTrainingOk != nil) {
                self.onTrainingOk((NSString *) result);
            }
        }
    } else if ([method isEqual:@"createRecord"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSDictionary *record = res[@"record"];
            if (self.onCreateRecordOk != nil) {
                self.onCreateRecordOk((NSString *) record[@"uuid"]);
            }
        }
    } else if ([method isEqual:@"stopRecord"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSDictionary *record = res[@"record"];
            if (self.onStopRecordOk != nil) {
                self.onStopRecordOk((NSString *) record[@"uuid"]);
            }
        }
    } else if ([method isEqual:@"getRecordInfos"]) {
        if ([result isKindOfClass:[NSArray class]]) {
            NSArray *arr = (NSArray *) result;
            NSDictionary *record = arr[0];
            if (self.onGetRecordInfosOk != nil) {
                self.onGetRecordInfosOk(record);
            }
        }
    } else if ([method isEqual:@"injectMarker"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSDictionary *marker = res[@"marker"];
            NSString *markerId = marker[@"uuid"];
            if (self.onInjectMarkerOk != nil) {
                self.onInjectMarkerOk(markerId);
            }
        }
    } else if ([method isEqual:@"updateMarker"]) {
        if (self.onUpdateMarkerOk != nil) {
            self.onUpdateMarkerOk();
        }
    } else if ([method isEqual:@"loginWithAuthenticationCode"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *username = res[@"username"];
            if (self.onLoggedinOk != nil) {
                self.onLoggedinOk(username);
            }
        }
    } else if ([method isEqual:@"logout"]) {
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *username = res[@"username"];
            if (self.onLoggedoutOk != nil) {
                self.onLoggedoutOk(username);
            }
        }
    } else if ([method isEqual:@"controlDevice"]){
        if ([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *command = res[@"command"];
            if ([command isEqual:@"disconnect"] && self.onHeadsetDisconnected != nil) {
                self.onHeadsetDisconnected();
            }
        }
    } else if ([method isEqual:@"createVirtualHeadset"])
    {
        if([result isKindOfClass:[NSDictionary class]]) {
            NSDictionary *res = (NSDictionary *) result;
            NSString *virtualHeadsetId = res[@"uuid"];
            if(self.onCreateVirtualHeadsetOk != nil){
                self.onCreateVirtualHeadsetOk(virtualHeadsetId);
            }
        }
    } else if ([method isEqual:@"queryVirtualHeadsets"])
    {
        if([result isKindOfClass:[NSArray class]]) {
            NSArray *res = (NSArray *) result;
            if(self.onQueryVirtualHeadsetOk != nil){
                self.onQueryVirtualHeadsetOk(res);
            }
        }
    } else if ([method isEqual:@"deleteVirtualHeadset"])
    {
        if(self.onDeleteVirtualHeadsetOk != nil)
            self.onDeleteVirtualHeadsetOk();
    }
    else {
        // Unhandled reply method
        NSLog(@"Result from an unhandled API method: %@ %@", method, result);
    }
}

-(void) emitError: (NSString *) method error: (NSDictionary *) error {
    int code = [[error objectForKey:@"code"] intValue];
    NSString *message = error[@"message"];
    NSLog(@"The Cortex service returned an error:");
    NSLog(@"method %@", method);
    NSLog(@"code %d", code);
    NSLog(@"message %@", message);
    if (self.onErrorReceived != nil) {
        self.onErrorReceived(method, code, message);
    }
}

-(void) handleGetDetectionInfo: (NSDictionary *) result {
    NSArray *jactions = result[@"actions"];
    NSArray *jcontrols = result[@"controls"];
    NSArray *jevents = result[@"events"];
    if (self.onGetDetectionInfoOk != nil) {
        self.onGetDetectionInfoOk(jactions, jcontrols, jevents);
    }
}

-(NSArray *) parseSubscriptionResult: (NSString *) method result: (NSDictionary *) result {
    NSArray *success = result[@"success"];
    NSArray *failure = result[@"failure"];
    NSMutableArray *streams = [[NSMutableArray alloc] init];
    if (failure.count != 0) {
        for(NSDictionary *obj in failure) {
            [self emitError:method error:obj];
        }
        return streams;
    }
    for(NSDictionary *obj in success) {
        NSString *stream = obj[@"streamName"];
        NSArray *columns = obj[@"cols"];
        if (columns.count != 0) {
            NSLog(@"stream uses these columns: %@", columns);
        }
        [streams addObject:stream];
    }
    return streams;
}

#pragma mark CortexClientDelegate

- (void)processResponse:(NSString *)responseMessage
{
    [self handleResponse:responseMessage];
}

- (void)authenticationFinished:(NSString *)authenticationCode withError:(NSError *)error {
    if(!error)
    {
        NSLog(@"authentication code: %@",authenticationCode);
        if(self.onAuthenticateOk)
            self.onAuthenticateOk(authenticationCode);
    }
    else
        NSLog(@"error: %@", error.description);
}

@end
