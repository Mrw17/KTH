package com.example.ii142x.communication;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class SendMessage {
    String path;
    String message;
    Context context;
//Constructor for sending information to the Data Layer//

    public SendMessage(String p, String m, Activity mainActivity) {
        context = mainActivity.getBaseContext();
        path = p;
        message = m;
    }

    public void run() throws ExecutionException, InterruptedException {

        // Get all connected nodes
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(context).getConnectedNodes();
        try {

            //Block on a task and get the result synchronously//
            List<Node> nodes = Tasks.await(nodeListTask);
            for (Node node : nodes) {

                // Task for sending the message
                Task<Integer> sendMessageTask =
                        Wearable.getMessageClient(context).sendMessage(node.getId(), path, message.getBytes());

                try {
                    // Gets the result
                    Integer result = Tasks.await(sendMessageTask);

                } catch (ExecutionException exception) {
                    throw new ExecutionException(exception);

                } catch (InterruptedException exception) {
                    throw new InterruptedException(exception.toString());
                }
            }
        } catch (ExecutionException exception) {
            throw new ExecutionException(exception);

        } catch (InterruptedException exception) {
            throw new InterruptedException(exception.toString());
        }
    }
}
