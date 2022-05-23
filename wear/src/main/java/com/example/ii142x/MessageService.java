package com.example.ii142x;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.example.ii142x.communication.MessagePath;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class MessageService extends WearableListenerService {
    public MessageService() {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {

//If the message’s path equals "/my_path"...//

        if (messageEvent.getPath().equals("/my_path")) {

//...retrieve the message//

            final String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);
            System.out.println("yey??");

//Broadcast the received Data Layer messages locally//

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else if (messageEvent.getPath().equals("/pressure")) {
            System.out.println("test??");
            final String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("pressure", message);

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }
        else if (messageEvent.getPath().equals(MessagePath.GPS)){
            System.out.println("test??");
            final String message = new String(messageEvent.getData());

            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("message", message);

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);

        }
        else if(messageEvent.getPath().equals(MessagePath.HEART_RATE)){
            final String message = new String(messageEvent.getData());
            Intent messageIntent = new Intent();
            messageIntent.setAction(Intent.ACTION_SEND);
            messageIntent.putExtra("heartRate", message);

            //Broadcast the received Data Layer messages locally//

            LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
        }
        else {
            System.out.println("nej??");
            System.out.println(messageEvent.toString());
            super.onMessageReceived(messageEvent);
        }
    }

}