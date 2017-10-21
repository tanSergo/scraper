package system;

import akka.actor.ActorRef;
import akka.actor.UntypedAbstractActor;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class CharacterCounter extends UntypedAbstractActor {

    static class Count {
        private final String url;

        public Count(String url) {
            this.url = url;
        }

        @Override
        public String toString() {
            return "Count{" +
                    "url='" + url + '\'' +
                    '}';
        }
    }

    private Map<String, String> results = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Count) {
            getContext().system().log().info("CharacterCounter get Count message {}", message);
            Count countMessage = (Count) message;
            getContext().system().log().info("CharacterCounter ready to count");
            Long characters = countCharacters(countMessage);
            getContext().system().log().info("CharacterCounter is done to count");
            results.put(countMessage.url + ". Number of characters ", String.valueOf(characters));
            getContext().system().log().info("CharacterCounter make own result {}", results);
            getContext().parent().tell(new Controller.Done(results), getSelf());
            getContext().stop(getSelf());
        } else {
            unhandled(message);
        }
    }

    private Long countCharacters(Count countMessage) throws IOException {
        Long retVal = 0L;
        URL url = new URL(countMessage.url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        int symbol = in.read();
        while (symbol != -1) {
            char c = (char) symbol;
            if (!Character.isWhitespace(c)) {
                retVal +=1;
            }
            symbol = in.read();
        }
        return retVal;
    }
}
