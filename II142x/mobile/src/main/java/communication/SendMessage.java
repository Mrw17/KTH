package communication;

import android.app.Activity;
import android.content.Context;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Class that will create a thread and try to send a message with MessageClient
 */
public class SendMessage extends Thread{
    String path;
    String message;
    Context context;

    /**
     * Constructor for sending information to the Data Layer
     * @param path that it will be sent on
     * @param message data that will be sent
     * @param activity context
     */
    public SendMessage(String path, String message, Activity activity) {
        this.context = activity.getBaseContext();
        this.path = path;
        this.message = message;
    }

    /**
     * Running method that
     */
    public void run() {

        // Get all connected nodes
        Task<List<Node>> nodeListTask = Wearable.getNodeClient(context).getConnectedNodes();
        try {

            // Blocking task and get the result synchronously
            List<Node> nodes = Tasks.await(nodeListTask);

            // Getting all nodes
            for (Node node : nodes) {

                // Creating task for sending the message
                Task<Integer> sendMessageTask =
                        Wearable.getMessageClient(context).sendMessage(node.getId(), path, message.getBytes());

                try {
                    // Gets the result
                    Tasks.await(sendMessageTask);

                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}