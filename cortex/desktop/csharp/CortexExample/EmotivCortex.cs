using System;
using Newtonsoft.Json.Linq;

namespace CortexExample;

public class CortexReponseHandler : ResponseHandlerCpp
{
    public CortexReponseHandler() : base()
    {}

    public override void processResponse(string responseMessage)
    {
        if(OnCortexResponse != null)
            OnCortexResponse(this, responseMessage);
    }

    public event EventHandler<string>? OnCortexResponse;
}
public class EmotivCortex
{
    private CortexClient _cortexClient;
    private CortexReponseHandler _responseHandler;

    private string _clientId = "your-client-id";
    private string _clientSecret = "your-client-secret";
    private string _licenseId = "your-license-id";

    private string _dataType = "eeg";
    private string DiscoveredHeadsetId {get; set;}
    private string ConnectedHeadsetId {get; set;}
    private string AuthToken {get; set;}
    private string Session {get; set;}
    private string CurrentUserId {get; set;}

    enum RequestId
    {
        GetUserLoginRequestId,
        LoginRequestId,
        LogoutRequestId,
        AuthorizeRequestId,
        QueryHeadsetRequestId,
        ConnectHeadsetRequestId,
        CreateSessionRequestId,
        SubscribeDataRequestId,
        UnsubscribeDataRequestId
    }

    enum CortexWarning
    {
        HeadsetConnected = 104
    }

    public EmotivCortex()
    {
        _cortexClient = new CortexClient();
        _responseHandler = new CortexReponseHandler();
        _responseHandler.OnCortexResponse += OnCortexResponse;
        _cortexClient.registerResponseHandler(_responseHandler);
        DiscoveredHeadsetId = string.Empty;
        ConnectedHeadsetId = string.Empty;
        AuthToken = string.Empty;
        Session = string.Empty;
        CurrentUserId = string.Empty;
    }

    private void OnCortexResponse(object? sender, string message)
    {
        JObject response = JObject.Parse(message);
        var idToken = response["id"];
        var warningMessage = response["warning"];
        var sid = response["sid"];
        if (warningMessage != null)
        {
            Console.WriteLine("Received warning message: " + message);
            var warningCodeToken = warningMessage["code"];
            if (warningCodeToken != null)
            {
                HandleCortexWarning((CortexWarning)(int)warningCodeToken, (JObject)warningMessage);
            }
        }
        if (idToken != null)
        {
            Console.WriteLine("Received response message: " + message);
            HandleCortexApiResult(response, idToken.Value<int>());
        }
        if  (sid != null)
        {
            HandleDataStream(response);
        }
    }

    private void HandleCortexApiResult(JObject json, int id)
    {
       switch (id)
       {
        case (int)RequestId.AuthorizeRequestId:
            AuthToken = json["result"]?["cortexToken"]?.ToString() ?? string.Empty;
            break;
        case (int)RequestId.QueryHeadsetRequestId:
            var headsets = json["result"];
            if (headsets != null)
            {
                foreach (var headset in headsets)
                {
                    if (headset["status"]?.ToString() == "discovered")
                    {
                        DiscoveredHeadsetId = headset["id"]?.ToString() ?? string.Empty;
                        break;
                    }
                }
            }
            break;
        case (int)RequestId.CreateSessionRequestId:
            Session = json["result"]?["id"]?.ToString() ?? string.Empty;
            break;
        case (int)RequestId.GetUserLoginRequestId:
            var resultArray = json["result"] as JArray;
            if (resultArray != null && resultArray.Count > 0)
            {
                CurrentUserId = resultArray[0]?["username"]?.ToString() ?? string.Empty;
            }
            else
            {
                CurrentUserId = string.Empty;
            }
            break;
        case (int)RequestId.LogoutRequestId:
            AuthToken = string.Empty;
            CurrentUserId = string.Empty;
            break;                
        default:
            break;
       }
    }
    private void HandleCortexWarning(CortexWarning code, JObject json)
    {
        switch (code)
        {
            case CortexWarning.HeadsetConnected:
                ConnectedHeadsetId = json["message"]?["headsetId"]?.ToString() ?? string.Empty;
                break;
            default:
                break;
        }
    }

    private static void HandleDataStream(JObject json)
    {
        Console.WriteLine("Data stream received: " + json.ToString());
    }
    private void SendMessage(JObject? param, string method, RequestId id)
    {
        JObject obj = new()
        {
            ["method"] = method,
            ["jsonrpc"] = "2.0",
            ["id"] = (int)id
        }; 
        if(param != null)
        {
            obj["params"] = param;
        }
        Console.WriteLine("Send request: " + obj.ToString());
        _cortexClient.sendRequest(obj.ToString());
    }
    public async void Login()
    {
        var authorize = new EmotivAuthentication(_clientId);
        var code = await authorize.Authorize();
        Console.WriteLine("Authorize: " + code);
        JObject param = new()
        {
            ["clientId"] = _clientId,
            ["clientSecret"] = _clientSecret,
            ["code"] = code
        };
        SendMessage(param, "loginWithAuthenticationCode", RequestId.LoginRequestId);
    }

    public void GetUserLogin()
    {
        SendMessage(null, "getUserLogin", RequestId.GetUserLoginRequestId);
    }

    public void GetUserInformation()
    {
        JObject param = new()
        {
            ["cortexToken"] = AuthToken,
        };
        SendMessage(param, "getUserInformation", RequestId.GetUserLoginRequestId);
    }

    public void GetLicenseInformation()
    {
        JObject param = new()
        {
            ["cortexToken"] = AuthToken,
        };
        SendMessage(param, "getLicenseInfo", RequestId.GetUserLoginRequestId);
    }

    public void Logout()
    {
        JObject param = new()
        {
            ["username"] = CurrentUserId,
        };
        SendMessage(param, "logout", RequestId.LogoutRequestId);
    }

    public void Authorize()
    {
        JObject param = new()
        {
            ["clientId"] = _clientId,
            ["clientSecret"] = _clientSecret
        };
        if(!string.IsNullOrEmpty(_licenseId))
        {
            param["license"] = _licenseId;
        }
        param["debit"] = 100;
        SendMessage(param, "authorize", RequestId.AuthorizeRequestId);
    }

    public void QueryHeadset()
    {
        SendMessage(null, "queryHeadsets", RequestId.QueryHeadsetRequestId);
    }

    public void ConnectHeadset()
    {
        JObject param = new()
        {
            ["headset"] = DiscoveredHeadsetId,
            ["command"] = "connect"
        };
        SendMessage(param, "controlDevice", RequestId.ConnectHeadsetRequestId);
    }

    public void CreateSession()
    {
        JObject param = new()
        {
            ["status"] = "active",
            ["headset"] = ConnectedHeadsetId,
            ["cortexToken"] = AuthToken
        };
        SendMessage(param, "createSession", RequestId.CreateSessionRequestId);
    }

    public void SubscribeData()
    {
        JObject param = new()
        {
            ["streams"] = new JArray(_dataType),
            ["cortexToken"] = AuthToken,
            ["session"] = Session
        };
        SendMessage(param, "subscribe", RequestId.SubscribeDataRequestId);
    }

    public void UnsubscribeData()
    {
        JObject param = new()
        {
            ["streams"] = new JArray(_dataType),
            ["cortexToken"] = AuthToken,
            ["session"] = Session
        };
        SendMessage(param, "unsubscribe", RequestId.UnsubscribeDataRequestId);
    }

    public void Close()
    {
        _cortexClient.close();
    }
}
