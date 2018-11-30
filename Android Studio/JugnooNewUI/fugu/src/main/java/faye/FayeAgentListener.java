package faye;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */


public interface FayeAgentListener {

    public void onConnectedServer(FayeClient fc);
    public void onDisconnectedServer(FayeClient fc);
    public void onReceivedMessage(FayeClient fc, String msg, String channel);
    public void onPongReceived();
    public void onWebSocketError();

}
