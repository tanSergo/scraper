package system;

import akka.actor.UntypedAbstractActor;
import system.parser.MyRegexParser;
import system.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
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

    private Map<String, Map<String, String>> results = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Count) {
            Count countMessage = (Count) message;
            getContext().system().log().info("WordCounter get Count message {}", message);
            parseUrl(countMessage);

        } else {
            unhandled(message);
        }
    }

    private void parseUrl(Count countMessage) throws IOException {
        URL url = new URL(countMessage.url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        String inputLine;
        Parser parser = new MyRegexParser();
        while ((inputLine = in.readLine()) != null) {
            List<String> sequences = parser.parseString(inputLine, countMessage.keywords);
            for (String sequence: sequences) {
//                System.out.println(sequence);

            }
        }
        in.close();
    }
}
