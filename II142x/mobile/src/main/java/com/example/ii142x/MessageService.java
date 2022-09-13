package com.example.ii142x;

import android.content.Intent;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;
import communication.MessageBundles;
import communication.MessagePath;

/**
 * Service that will broadcast out an Intent
 */
public class MessageService extends WearableListenerService {
    public MessageService() {}

    /**
     * Gets the message from an event
     * @param messageEvent to get the message from
     * @return the message from the event
     */
    private String getMessage(MessageEvent messageEvent){
        return new String(messageEvent.getData());
    }


    /**
     * Creates an Intent-object with a path and message
     * @param path that for message
     * @param message message that will be sent
     * @return object with given data
     */
    private Intent createIntent(String path, String message){
        Intent messageIntent = new Intent();
        messageIntent.setAction(Intent.ACTION_SEND);
        messageIntent.putExtra(path, message);
        return messageIntent;
    }


    /**
     * If a message is received
     * @param messageEvent that happened
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        //If it was a heart rate message
        if(messageEvent.getPath().equals(MessagePath.HEART_RATE)){

            //Gets the message
            final String message = getMessage(messageEvent);

            //Fix settings
            Intent messageIntent = createIntent(MessageBundles.HEART_RATE, message);

            //Broadcast the received Data Layer messages locally//
            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
    }
}