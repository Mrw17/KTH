package com.example.ii142x;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import communication.MessageBundles;
import communication.MessagePath;

public class MessageService extends WearableListenerService {
    public MessageService() {}

    private String getMessage(MessageEvent messageEvent){
        return new String(messageEvent.getData());
    }

    private Intent createIntet(String path, String message){
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra(path, message);
        return messageIntent;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //If it was a heart rate message
        if(messageEvent.getPath().equals(MessagePath.HEART_RATE)){
            //Gets the message
            final String message = getMessage(messageEvent);

            //Fix settings
            Intent messageIntent = createIntet(MessageBundles.HEART_RATE, message);

            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else if (messageEvent.getPath().equals(MessagePath.GPS)){
            //Gets the message
            final String message = getMessage(messageEvent);

            //Fix settings
            Intent messageIntent = createIntet(MessageBundles.GPS, message);

            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }
    }

}