package system;


import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.*;

public class Controller extends UntypedAbstractActor {

    static class Done
    {
        private final Map<String, String> resultOfCounter;

        public Done(Map<String, String> resultOfCounter) {
            this.resultOfCounter = resultOfCounter;
        }

        @Override
        public String toString() {
            return "Done{" +
                    "resultOfCounter=" + resultOfCounter +
                    '}';
        }
    }

    static class Check
    {
        private final String url;
        private final List<String> keywords;
        private final Boolean wordCount;
        private final Boolean extractSentences;
        private final Boolean charactersCount;

        public Check(String url, List<String> keywords, Boolean wordsCount, Boolean extractSentences, Boolean charactersCount) {
            this.url = url;
            this.keywords = keywords;
            this.wordCount = wordsCount;
            this.extractSentences = extractSentences;
            this.charactersCount = charactersCount;
        }

        @Override
        public String toString() {
            return "Check{" +
                    "url='" + url + '\'' +
                    ", wordCount=" + wordCount +
                    ", extractSentences=" + extractSentences +
                    ", charactersCount=" + charactersCount +
                    '}';
        }
    }

    private Set<ActorRef> children = new HashSet<>();
    private Map<String, String> results = new HashMap<>();

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof Done)
        {
            results.putAll(((Done) message).resultOfCounter);
            children.remove(getSender());
            if (children.isEmpty())
            {
                getContext().parent().tell(new Receptionist.ControllerDone(results), getSelf());
                getContext().stop(getSelf());
            }
        }
        else if (message instanceof Check)
        {
            getContext().system().log().info("Controller get Check message {}", message);
            Check checkMessage = (Check) message;
            runCounters(checkMessage);
        }
    }

    private void runCounters(Check checkMessage) {
        if (checkMessage.wordCount) {
            if (checkMessage.extractSentences) {
                ActorRef counter = getContext().actorOf(Props.create(WordCounter.class));
                counter.tell(new WordCounter.Count(
                        checkMessage.url, checkMessage.keywords, true, true), getSelf()
                );
                children.add(counter);
            }
            else {
                ActorRef counter = getContext().actorOf(Props.create(WordCounter.class));
                counter.tell(new WordCounter.Count(
                        checkMessage.url, checkMessage.keywords, true, false), getSelf()
                );
                children.add(counter);
            }
        }
        if (checkMessage.extractSentences){
            ActorRef counter = getContext().actorOf(Props.create(WordCounter.class));
            counter.tell(new WordCounter.Count(
                    checkMessage.url, checkMessage.keywords, false, true), getSelf()
            );
            children.add(counter);
        }
        if (checkMessage.charactersCount) {
            ActorRef counter = getContext().actorOf(Props.create(CharacterCounter.class));
            counter.tell(new CharacterCounter.Count(checkMessage.url), getSelf());
            children.add(counter);
        }
    }
}
