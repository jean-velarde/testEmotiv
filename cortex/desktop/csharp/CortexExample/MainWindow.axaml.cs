using System;
using Avalonia.Controls;
using Avalonia.Interactivity;

namespace CortexExample;

public class CortexStarted : CortexStartedEventHandler
{
    public CortexStarted() : base()
    {}

    public override void onCortexStarted()
    {
        Console.WriteLine("Cortex Started");
        OnCortexStarted?.Invoke(this, true);
    }

    public event EventHandler<bool>? OnCortexStarted;
}

public class CortexLog : CortexLogEventHandler
{
    public CortexLog() : base()
    {}

    public override void onLogMessage(string message)
    {
        Console.WriteLine(message);
    }
}

public partial class MainWindow : Window
{
    private EmotivCortex? _emotivCortex;
    public MainWindow()
    {
        InitializeComponent();
        Start();
        Closing += (sender, e) => { _emotivCortex?.Close(); };
    }

    private void Start()
    {
        try
        {
            // Enable this line if you want to see the cortex log messages.   
            //CortexLog logger = new();
            //CortexLib.setLogHandler(1, logger);
            CortexStarted startEvent = new();
            startEvent.OnCortexStarted += CortexStarted; 
            CortexLib.start(startEvent);
        }
        catch (Exception)
        {
            throw; // Todo: Handle exception.
        }
    }

    private void CortexStarted(object? sender, bool e)
    {
        _emotivCortex = new EmotivCortex();
    }

    public void OnLogout(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.Logout();
    }
    public void OnGetUserLogin(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.GetUserLogin();
    }

    public void OnLogin(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.Login();
    }

    public void OnGetUserInfo(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.GetUserInformation();
    }

    public void OnGetLicenseInfo(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.GetLicenseInformation();
    }

    public void OnAuthorize(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.Authorize();
    }

    public void OnQueryHeadset(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.QueryHeadset();
    }

    public void OnConnectHeadset(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.ConnectHeadset();
    }

    public void OnCreateSession(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.CreateSession();
    }

    public void OnSubscribeData(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.SubscribeData();
    }

    public void OnUnsubscribeData(object sender, RoutedEventArgs args)
    {
        _emotivCortex?.UnsubscribeData();
    }
}