package system.actors;

import akka.actor.UntypedAbstractActor;
import system.parser.FullTextParser;
import system.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
            getContext().parent().tell(new Controller.Done(countMessage.url, results), getSelf());
            getContext().stop(getSelf());

        } else {
            unhandled(message);
        }
    }

    private void parseUrl(Count countMessage) throws IOException {
        URL url = new URL(countMessage.url);
        BufferedReader in = new BufferedReader(
                new InputStreamReader(url.openStream()));
        Parser parser = new FullTextParser();

        List<String> sentences = parser.parseUrl(in, parser, countMessage.keywords);

        in.close();

        if (countMessage.wordCount) {
            if (results.containsKey(countMessage.url)) {
                results.get(countMessage.url).put("Number of keywords", String.valueOf(sentences.size()));
            } else {
                Map<String, String> numberOfKeywords = new HashMap<>();
                numberOfKeywords.put("Number of keywords", String.valueOf(sentences.size()));
                results.put(countMessage.url, numberOfKeywords);
            }
        }
        if (countMessage.extractSentences) {
            String text = "";
            for (String sentence : sentences) {
                text = text.concat(sentence);
            }
            if (results.containsKey(countMessage.url)) {
                results.get(countMessage.url).put("Sentences with keywords", text);
            } else {
                Map<String, String> sentencesMap = new HashMap<>();
                sentencesMap.put("Sentences with keywords", text);
                results.put(countMessage.url, sentencesMap);
            }
        }
    }
}
