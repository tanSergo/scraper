package system;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedAbstractActor;

import java.util.*;

public class Receptionist extends UntypedAbstractActor {

    static class CheckLinks {

        private Arguments arguments;

        public CheckLinks(Arguments args){
            this.arguments = args;
        }

        @Override
        public String toString() {
            return "CheckLinks {" + arguments.toString() + "}";
        }
    }

    static class TakeResults {

        private final Map<String, String> results;

        public TakeResults(Map<String, String> results)
        {
            this.results = results;
        }

        public Map<String, String> getResults()
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
        private final Map<String, String> controllerResults;

        public ControllerDone(Map<String, String> controllerResults) {
            this.controllerResults = controllerResults;
        }

        @Override
        public String toString() {
            return "ControllerDone{" +
                    "controllerResults=" + controllerResults +
                    '}';
        }
    }


    private Set<ActorRef> controllers = new HashSet<>();

    private Map<String, String> passingResults = new HashMap<>();
    private Map<ActorRef, ActorRef> controllerToRequestMap = new HashMap<>();


    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof CheckLinks) {
            getContext().system().log().info(" Receptionist get CheckLinks message {}", message);
            startParsing((CheckLinks)message);
        }else if (message instanceof ControllerDone) {
            getContext().system().log().info(" Receptionist get ControllerDone message {}" , message);
            controllers.remove(getSender());
            passingResults.putAll(((ControllerDone) message).controllerResults);
            if (controllers.isEmpty()) {
                ActorRef controller = getSender();
                Optional<ActorRef> requestSender = Optional.ofNullable(controllerToRequestMap.get(controller));
                requestSender.ifPresent(actor -> actor.tell(new TakeResults(passingResults), getSelf()));
                controllerToRequestMap.remove(controller);
                getContext().stop(getSelf());
            }
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
