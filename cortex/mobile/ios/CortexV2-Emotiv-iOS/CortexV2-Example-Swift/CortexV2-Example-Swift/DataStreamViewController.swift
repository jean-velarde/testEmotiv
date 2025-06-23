//
//  DataStreamViewController.swift
//  CortexV2-Example-Swift
//
//  Created by Emotiv Inc on 2/21/20.
//  Copyright © 2020 Emotiv. All rights reserved.
//

import UIKit

class DataStreamViewController: UIViewController, UITableViewDelegate, UITableViewDataSource, ASWebAuthenticationPresentationContextProviding {
    @available(iOS 12.0, *)
    func presentationAnchor(for session: ASWebAuthenticationSession) -> ASPresentationAnchor {
        return self.view.window ?? ASPresentationAnchor()
    }
    

    @IBOutlet weak var authorizeView: UIView!
    @IBOutlet weak var dataView: UIView!
    @IBOutlet weak var trainingView: UIView!
    @IBOutlet weak var headsetTableView: UITableView!
    @IBOutlet weak var markerView: UIView!
    @IBOutlet weak var streamView: UIView!
    @IBOutlet weak var virtualHeadsetView: UIView!
    
    @IBOutlet weak var subscribeBtn: UIButton!
    @IBOutlet weak var unsubscribeBtn: UIButton!
    @IBOutlet weak var createSession: UIButton!
    @IBOutlet weak var getLicenseInfo: UIButton!
    @IBOutlet weak var getUserInfo: UIButton!
    @IBOutlet weak var training1: UIButton!
    @IBOutlet weak var training2: UIButton!
    @IBOutlet weak var training: UIButton!
    @IBOutlet weak var loadProfile: UIButton!
    @IBOutlet weak var subscribeTraining: UIButton!
    @IBOutlet weak var createProfile: UIButton!
    @IBOutlet weak var injectMarker: UIButton!
    @IBOutlet weak var stopRecord: UIButton!
    @IBOutlet weak var createRecord: UIButton!
    @IBOutlet weak var disconnectHeadset: UIButton!
    @IBOutlet weak var createVirtualHeadset: UIButton!
    
    var client : CortexConnection = CortexConnection()
    var connectedHeadsetId: String = ""
    var createdVirtualHeadsetId: String = ""
    var currentUser: String = ""
    var headsetList: [Headset] = [Headset]()
    var activateSession: Bool = false
    var license: String = ""

    var token: String = ""
    var sessionId: String = ""
    var stream: String = ""
    
    var action: String = ""
    var recordId: String = ""
    var markerId: String = ""
    
    let cellReuseIdentifier = "HeadsetTableViewCell"
    let ERROR_CLOUDTOKEN_REFRESH = -32130
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let headsetTableViewCell = UINib(nibName: cellReuseIdentifier, bundle: nil)
        self.headsetTableView.register(headsetTableViewCell, forCellReuseIdentifier: cellReuseIdentifier)
        headsetTableView.delegate = self
        headsetTableView.dataSource = self
    }
    
    func onCortexStarted() {
        client = CortexConnection.shareInstance() as!CortexConnection
        if #available(iOS 13.0, *) {
            client.setDisplayContex(self)
        } else {
            // Fallback on earlier versions
        }
        handleCortexEvent()
        DispatchQueue.main.async { [self] in
            handleUI()
        }
    }

    @IBAction func login(_ sender: Any){
        client.startAuthentication(ClientId)
    }
    
    @IBAction func logout(_ sender: Any){
        if currentUser.isEmpty
        {
            NSLog("You did not login any Emotiv id.")
        }
        else
        {
            client.logout(currentUser)
        }
    }
    
    @IBAction func getUserLogin(_ sender: Any) {
        client.getUserLogin()
    }
    
    @IBAction func authorize(_ sender: Any) {
        client.authorize(ClientId, secret: ClientSecret, license: license, debit: activateSession ? 1 : 0)
    }
    
    @IBAction func getUserInformation(_ sender: Any) {
        client.getUserInformation(token)
    }
    
    @IBAction func getLicenseInfo(_ sender: Any) {
        client.getLicenseInfos(token)
    }
    
    @IBAction func queryHeadsets(_ sender: Any) {
        //finder.findHeadsets()
        client.refreshHeadsetScan()
        client.queryHeadset("")
    }
    
    @IBAction func createSession(_ sender: Any) {
        client.createSession(token, headsetId: connectedHeadsetId, activate: activateSession)
    }
    
    @IBAction func subcribe(_ sender: Any) {
        client.subscribe(token, sessionId: sessionId, stream: stream)
    }
    
    @IBAction func unsubscribe(_ sender: Any) {
        client.unsubscribe(token, sessionId: sessionId, stream: stream)
    }
    
    @IBAction func subcribeTraining(_ sender: Any) {
        client.subscribe(token, sessionId: sessionId, stream: "sys")
    }
    
    @IBAction func createProfile(_ sender: Any) {
        client.createProfile(token, profileName: TrainingProfileName, headsetId: connectedHeadsetId)
    }
    
    @IBAction func loadProfile(_ sender: Any) {
        client.loadProfile(token, headsetId: connectedHeadsetId, profileName: TrainingProfileName)
    }
    
    @IBAction func trainNeutral(_ sender: Any) {
        self.action = "neutral"
        client.training(token, sessionId: sessionId, detection: stream , action: self.action, control: "start")
        self.enableButton(enable: false)
        NSLog("waiting training result from Cortex")
    }
    
    @IBAction func trainPush(_ sender: Any) {
        self.action = stream == "mentalCommand" ? "push" : "smile"
        client.training(token, sessionId: sessionId, detection: stream , action: self.action, control: "start")
        self.enableButton(enable: false)
        NSLog("waiting training result from Cortex")
    }
    
    @IBAction func createRecord(_ sender: Any) {
        client.createRecord(token, sessionId: sessionId, title: "Cortex Examples Swift")
    }
    
    @IBAction func injectMarker(_ sender: Any) {
        client.injectMarker(token, sessionId: sessionId, label: "test1", value: 41, time: Int64(Date().timeIntervalSince1970 * 1000));
    }
    
    @IBAction func stopRecord(_ sender: Any) {
        client.stopRecord(token, sessionId: sessionId)
    }
    
    @IBAction func training(_ sender: Any) {
        if sessionId == "" {
            NSLog("Please create session")
        } else {
            if self.stream == "session" {
                client.closeSession(self.token, sessionId: self.sessionId)
            } else {
                self.dataView.isHidden = true
                self.trainingView.isHidden = false
            }
        }
    }
    
    @IBAction func disconnect(_ sender: Any) {
        if connectedHeadsetId.isEmpty
        {
            NSLog("You did not connect any Emotiv headset.")
        }
        else
        {
            client.disconectHeadset(connectedHeadsetId)
        }
    }
    
    @IBAction func createVirtualHeadset(_ sender:Any) {
        if createdVirtualHeadsetId.isEmpty
        {
            client.createVirtualHeadset(token)
        }
        else
        {
            NSLog("You already created a virtual headset.")
        }
    }
    
    @IBAction func queryVirtualHeadset(_ sender: Any) {
        // query virtual headset will get virtual headset info from database
        // it does not replace for queryHeadset api
        // virtual headset also appears in queryHeadset when it set power on
        client.queryVirtualHeadset(token)
    }
    
    @IBAction func deleteVirtualHeadset(_ sender: Any) {
        client.deleteVirtualHeadset(token, uuid: createdVirtualHeadsetId)
    }
    
    func handleUI() {
        var nameStream = ""
        if stream == "mot" {
            nameStream = "motion"
        } else if stream == "eeg" {
            nameStream = "eeg"
        } else if stream == "pow" {
            nameStream = "Band Power"
        } else if stream == "com" {
            nameStream = "MC"
        } else if stream == "fac" {
            nameStream = "FE"
        } else if stream == "met" {
            nameStream = "PM"
        } else if stream == "facialExpression" {
            training.isHidden = false
            subscribeBtn.isHidden = true
            unsubscribeBtn.isHidden = true
            training1.setTitle("train FE Neutral", for: UIControl.State.normal)
            training2.setTitle("train FE Smile", for: UIControl.State.normal)
        } else if stream == "mentalCommand" {
            training.isHidden = false
            subscribeBtn.isHidden = true
            unsubscribeBtn.isHidden = true
            training1.setTitle("train MC Neutral", for: UIControl.State.normal)
            training2.setTitle("train MC Push", for: UIControl.State.normal)
        } else if stream == "marker" {
            streamView.isHidden = true
            markerView.isHidden = false
        }
        
        if stream != "authorize" {
            client.getUserLogin()
        }

        if stream == "virtualheadset" {
            virtualHeadsetView.isHidden = false
        } else if stream != "authorize" {
            authorizeView.isHidden = true
            dataView.isHidden = false
        }
        
        if stream == "session" {
            training.isHidden = false
            disconnectHeadset.isHidden = false
            subscribeBtn.isHidden = true
            unsubscribeBtn.isHidden = true
            self.training.setTitle("updateSession(Close)", for: UIControl.State.normal)
        }
        
        self.subscribeBtn.setTitle("subcribe " + nameStream, for: UIControl.State.normal)
        self.unsubscribeBtn.setTitle("unsubcribe " + nameStream, for: UIControl.State.normal)
    }
    
    func handleCortexEvent() {
        
        client.onErrorReceived = { [weak self](method, code, message) in
            guard let weakSelf = self else { return }
            // call authorize again if get error cloud token refresh
            if(code == weakSelf.ERROR_CLOUDTOKEN_REFRESH && method == "authorize")
            {
                weakSelf.client.authorize(ClientId, secret: ClientSecret, license: weakSelf.license, debit: weakSelf.activateSession ? 1 : 0)
            }
        }
        
        client.onAuthenticateOk = {[weak self](code) in
            guard let weakSelf = self else { return }
            weakSelf.client.login(withAuthenticationCode: code, clientId: ClientId, clientSecret: ClientSecret)
        }
        
        client.onLoggedinOk = {[weak self](emotivId) in
            guard let weakSelf = self else { return }
            NSLog("Logged in user \(emotivId)")
            weakSelf.currentUser = emotivId
            if(weakSelf.token.isEmpty && weakSelf.stream != "authorize")
            {
                weakSelf.client.authorize(ClientId, secret: ClientSecret, license: weakSelf.license, debit: weakSelf.activateSession ? 1 : 0)
            }
        }
        
        client.onLoggedoutOk = {[weak self](emotivId) in
            guard let weakSelf = self else { return }
            NSLog("Logged out user \(emotivId)")
            weakSelf.currentUser = ""
        }
        
        client.onGetUserLoginOk = { [weak self](emotivId) in
            guard let weakSelf = self else { return }
            weakSelf.currentUser = emotivId
            if emotivId == ""
            {
                NSLog("you did not login any emotiv Id")
                if(weakSelf.stream != "authorize")
                {
                    DispatchQueue.main.async {
                        weakSelf.client.startAuthentication(ClientId)
                    }
                }
            }
            else
            {
                NSLog("You are logged in with the EmotivId \(emotivId)")
                if(weakSelf.stream != "authorize")
                {
                    weakSelf.client.authorize(ClientId, secret: ClientSecret, license: weakSelf.license, debit: weakSelf.activateSession ? 1 : 0)
                }
            }
        }
        
        client.onUnsubscribeOk = { (streams) in
            NSLog("Subscription cancelled for data streams \(streams)")
        }
        
        client.onCloseSessionOk = {
            NSLog("Session closed.")
        }
        
        client.onQueryHeadsetsOk = {[weak self] (headsets) in
            NSLog("Query headset done.")
            guard let weakSelf = self else { return }
            let value = headsets as? [Headset]
            weakSelf.headsetList = value ?? [Headset]()
            DispatchQueue.main.async {
                weakSelf.headsetTableView.reloadData()
            }
        }
        
        client.onHeadsetConnected = {[weak self] (headsetId) in
            NSLog("Get connected headset: \(headsetId)")
            guard let weakSelf = self else { return }
            weakSelf.connectedHeadsetId = headsetId
            DispatchQueue.main.async {
                Timer.scheduledTimer(withTimeInterval: 1, repeats: false) { [weak self] (timer) in
                    guard let weakSelf = self else { return }
                    weakSelf.client.queryHeadset("")
                }
            }

        }
        
        client.onHeadsetDisconnected = { [weak self]() in
            guard let weakSelf = self else { return }
            NSLog("Get disconnected headset: \(weakSelf.connectedHeadsetId)")
            weakSelf.client.queryHeadset("")
        }
        
        client.onCreateVirtualHeadsetOk = {[weak self](headsetId) in
            guard let weakSelf = self else { return }
            NSLog("create virtual headset successful")
            weakSelf.createdVirtualHeadsetId = headsetId
        }
        
        client.onQueryVirtualHeadsetOk = {[weak self](array) in
            guard let weakSelf = self else { return }
            let value = array as Array
            if value.count == 0
            {
                DispatchQueue.main.async {
                    weakSelf.createVirtualHeadset.isEnabled = true
                }
            }
            else
            {
                let headsetId = array.first as! Dictionary<String, Any>
                weakSelf.createdVirtualHeadsetId = headsetId["uuid"] as! String
            }
            NSLog("query virtual headset done")
            NSLog("Data: \(array as Array)");
        }
        
        client.onDeleteVirtualHeadsetOk = {[weak self]() in
            guard let weakSelf = self else { return }
            NSLog("delete virtual headset successful")
            weakSelf.createdVirtualHeadsetId = ""
        }
        
        client.onAuthorizeOk = { [weak self] (token) in
            guard let weakSelf = self else { return }
            weakSelf.token = token
            NSLog("Authorize successful, token \(token)")
            if weakSelf.stream != "authorize"
            {
                weakSelf.client.queryVirtualHeadset(token)
            }
        }
        
        client.onSubscribeOk = { [weak self] (streams) in
            guard let weakSelf = self else { return }
            NSLog("Subscription to data stream successful \(streams)")
            DispatchQueue.main.async {
                weakSelf.training1.isEnabled = true
                weakSelf.training2.isEnabled = true
            }
        }
        
        client.onSaveProfileOk = { (profileName) in
            NSLog("Training profile saved \(profileName)")
        }
        
        client.onLoadProfileOk = { [weak self] (profileName) in
            guard let weakSelf = self else { return }
            NSLog("Training profile loaded \(profileName)")
            DispatchQueue.main.async {
                weakSelf.training1.isEnabled = true
                weakSelf.training2.isEnabled = true
            }
        }
        
        client.onCreateProfileOk = { (profileName) in
            NSLog("Training profile created \(profileName)")
        }
        
        
        client.onStreamDataReceived = { [weak self] (sessionId, stream, time, data) in
            guard let weakSelf = self else { return }
            if weakSelf.stream == "facialExpression" || weakSelf.stream == "mentalCommand" {
                if weakSelf.isEvent(data: data as NSArray, event: "Started") {
                    NSLog("")
                    NSLog("Please, focus on the action for a few seconds")
                } else if weakSelf.isEvent(data: data as NSArray, event: "Succeeded") {
                    weakSelf.client.training(weakSelf.token, sessionId: weakSelf.sessionId, detection: weakSelf.stream, action: weakSelf.action, control: "accept")
                } else if weakSelf.isEvent(data: data as NSArray, event: "Failed") {
                    DispatchQueue.main.async {
                        weakSelf.enableButton(enable: true)
                    }
                } else if weakSelf.isEvent(data: data as NSArray, event: "Completed") {
                    NSLog("Well done! You successfully trained")
                    DispatchQueue.main.async {
                        weakSelf.enableButton(enable: true)
                    }
                }
            }
            NSLog("\(stream) \(data)")
        }
        
        client.onTrainingOk = { (msg) in
            // this signal is not important
            // instead we need to watch the events from the sys stream
        }
        
        client.onCreateRecordOk = { [weak self] (recordId) in
            guard let weakSelf = self else { return }
            weakSelf.recordId = recordId
            DispatchQueue.main.async {
                weakSelf.stopRecord.isEnabled = true
                weakSelf.injectMarker.isEnabled = true
            }
        }
        
        client.onInjectMarkerOk = { [weak self] (markerId) in
            guard let weakSelf = self else { return }
            NSLog("Inject marker OK, marker id \(markerId)")
            weakSelf.markerId = markerId
        }
        
        client.onStopRecordOk = { [weak self] (recordId) in
            guard let weakSelf = self else { return }
            DispatchQueue.main.async {
                weakSelf.stopRecord.isEnabled = false
                weakSelf.injectMarker.isEnabled = false
            }
            weakSelf.client.getRecordInfos(weakSelf.token, recordId: recordId)
        }
        
        client.onCreateSessionOk = {[weak self] (sessionId) in
            guard let weakSelf = self else { return }
            weakSelf.sessionId = sessionId
            NSLog("Create Session Successful")
        }
        
        client.onCloseSessionOk = {
            NSLog("Close session successful")
        }
    }
    
    private func enableButton(enable: Bool) {
        self.training1.isEnabled = enable
        self.training2.isEnabled = enable
        self.createProfile.isEnabled = enable
        self.loadProfile.isEnabled = enable
        self.subscribeTraining.isEnabled = enable
    }
    
    private func isEvent(data: NSArray, event: String) -> Bool {
        for val in data {
            if val is String && (val as! String).contains(event) {
                return true
            }
        }
        return false
    }
    
    // number of rows in table view
    func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return headsetList.count
    }

    // create a cell for each table view row
    func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let hscell = tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as? HeadsetTableViewCell
        let headset = headsetList[indexPath.row]
        hscell?.status.text = headset.getStatus()
        hscell?.headsetName.text = headset.getId()
        hscell?.connectedBy.text = headset.getConnectedBy()
        if(headset.isVirtualHeadset())
        {
            hscell?.isVirtual.text = "true"
        }
        else
        {
            hscell?.isVirtual.text = "false"
        }
        return hscell ?? UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, viewForHeaderInSection section: Int) -> UIView? {
        let headerCell = tableView.dequeueReusableCell(withIdentifier: cellReuseIdentifier) as? HeadsetTableViewCell
        return headerCell ?? UITableViewCell()
    }
    
    func tableView(_ tableView: UITableView, heightForHeaderInSection section: Int) -> CGFloat {
        return 50
    }
    
    func tableView(_ tableView: UITableView, heightForRowAt indexPath: IndexPath) -> CGFloat {
        return 50
    }
    
    func tableView(_ tableView: UITableView, didSelectRowAt indexPath: IndexPath) {
        let headset = headsetList[indexPath.row]
        if(!(headset.getStatus() == "connected"))
        {
            NSLog("Selec headset \(headset.getId()) to connect")
            client.connectHeadset(headset.getId())
        }
    }
}

