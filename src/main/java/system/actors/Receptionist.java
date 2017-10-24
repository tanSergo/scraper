package system.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;
import system.Arguments;

import java.util.*;

public class Receptionist extends UntypedAbstractActor {

    static public class CheckLinks {

        private Arguments arguments;

        public CheckLinks(Arguments args){
            this.arguments = args;
        }

        @Override
        public String toString() {
            return "CheckLinks {" + arguments.toString() + "}";
        }
    }

    static public class TakeResults {

        private final Map<String, Map<String, String>> results;

        public TakeResults(Map<String, Map<String, String>> results)
        {
            this.results = results;
        }

        public Map<String, Map<String, String>> getResults()
        {
            return results;
        }

        @Override
        public String toString() {
            return "TakeResults{" +
                    "results=" + results +
                    '}';
        }
    }

    static class ControllerDone {
        private final Map<String, Map<String, String>> controllerResults;
        private final String url;

        public ControllerDone(String url, Map<String, Map<String, String>> controllerResults) {
            this.controllerResults = controllerResults;
            this.url = url;
        }

        @Override
        public String toString() {
            return "ControllerDone{" +
                    "controllerResults=" + controllerResults +
                    '}';
        }
    }


    private Set<ActorRef> controllers = new HashSet<>();
    private Arguments arguments;
    private Map<String, Map<String, String>> passingResults = new HashMap<>();
    private Map<ActorRef, ActorRef> controllerToRequestMap = new HashMap<>();


    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof CheckLinks) {
            getContext().system().log().info("Receptionist get CheckLinks message {}", message);
            this.arguments = ((CheckLinks) message).arguments;
            if (!arguments.getCharactersCount() && !arguments.getWordsCount() && !arguments.getExtractSentences()) {
                System.out.println("There are no data processing commands in command line!");
                ActorRef controller = getSender();
                controller.tell(new TakeResults(passingResults), getSelf());

            }
            startParsing((CheckLinks)message);
        }else if (message instanceof ControllerDone) {
            getContext().system().log().info("Receptionist get ControllerDone message {}" , message);
            controllers.remove(getSender());
            ControllerDone doneMessage = (ControllerDone) message;

            if (passingResults.containsKey(doneMessage.url)) {
                passingResults.get(doneMessage.url).putAll(doneMessage.controllerResults.get(doneMessage.url));
            }
            else {
                passingResults.putAll(doneMessage.controllerResults);
            }

            if (controllers.isEmpty()) {
                if (!arguments.getVerbose()) {
                    passingResults.forEach((s, stringStringMap) -> stringStringMap.remove("Processing time in nanoseconds"));
                }
                addTotalResults();
                ActorRef controller = getSender();
                Optional<ActorRef> requestSender = Optional.ofNullable(controllerToRequestMap.get(controller));
                requestSender.ifPresent(actor -> actor.tell(new TakeResults(passingResults), getSelf()));
                controllerToRequestMap.remove(controller);
                getContext().stop(getSelf());
            }
        }
    }

    private void addTotalResults() {
        if (arguments.getCharactersCount()) {
            List<String> tempResults = new ArrayList<>();
            passingResults.forEach((s, stringStringMap) -> tempResults.add(stringStringMap.getOrDefault("Number of characters","0")));
            Long totalCharactersNumber = 0L;
            for (String tempResult : tempResults) {
                totalCharactersNumber += Long.valueOf(tempResult);
            }
            Map<String, String> map = new HashMap<>();
            map.put("Number of characters", totalCharactersNumber.toString());
            if (passingResults.containsKey("Total")) passingResults.get("Total").putAll(map);
            else passingResults.put("Total", map);
        }
        if (arguments.getWordsCount()) {
            Long totalKeywordsNumber = 0L;
            List<String> tempResults = new ArrayList<>();
            passingResults.forEach((s, stringStringMap) -> tempResults.add(stringStringMap.getOrDefault("Number of keywords", "0")));

            for (String tempResult : tempResults) {
                totalKeywordsNumber += Long.valueOf(tempResult);
            }

            Map<String, String> map = new HashMap<>();
            map.put("Number of keywords", totalKeywordsNumber.toString());
            if (passingResults.containsKey("Total")) passingResults.get("Total").putAll(map);
            else passingResults.put("Total", map);
        }
    }


    private void startParsing(CheckLinks message) {
        for (int i = 0; i < message.arguments.getUrls().size(); i++) {
            ActorRef controller = getContext().actorOf(Props.create(Controller.class));
            controller.tell(
                    new Controller.Check(
                            message.arguments.getUrls().get(i),message.arguments.getKeywords(),
                            message.arguments.getWordsCount(),
                            message.arguments.getExtractSentences(), message.arguments.getCharactersCount()
                    )
                    , getSelf()
            );
            controllerToRequestMap.put(controller, getSender());
            controllers.add(controller);
        }
    }
}
