package system;

import akka.actor.UntypedAbstractActor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class WordCounter extends UntypedAbstractActor {

    static class Count {
        private final Boolean wordCount;
        private final Boolean extractSentences;
        private final String url;
        private final List<String> keywords;

        public Count(String url, List<String> keywords, Boolean wordCount, Boolean extractSentences) {
            this.wordCount = wordCount;
            this.keywords = keywords;
            this.extractSentences = extractSentences;
            this.url = url;
        }

        @Override
        public String toString() {
            return "Count{" +
                    "wordCount=" + wordCount +
                    ", extractSentences=" + extractSentences +
                    ", url='" + url + '\'' +
                    ", keywords=" + keywords +
                    '}';
        }
    }

    private Map<String, String> results = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Count) {
            getContext().system().log().info("WordCounter get Count message {}", message);
        } else {
            unhandled(message);
        }
    }
}
