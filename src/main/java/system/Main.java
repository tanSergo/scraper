package system;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;
import system.actors.Receptionist;

import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) throws Exception {
        long startTime = System.nanoTime();
        Arguments arguments = new Arguments(args);

        Timeout timeout = new Timeout(Duration.create(10, "seconds"));
        ActorSystem system = ActorSystem.create("webScraper");
        ActorRef receptionist = system.actorOf(Props.create(Receptionist.class), "Receptionist");
        Future<Object> future = Patterns.ask(receptionist,
                new Receptionist.CheckLinks(arguments), 7000);
        Receptionist.TakeResults results = (Receptionist.TakeResults) Await.result(future, timeout.duration());
        long estimatedTime = System.nanoTime() - startTime;

        if (arguments.getVerbose()) {
            Map<String, String> timeProp = new HashMap<>();
            timeProp.put("Scraping time", String.valueOf(estimatedTime));
            results.getResults().put("Time spent in nanoseconds", timeProp);

        }

        system.log().info("Result {}", results.getResults());
        system.terminate();
    }
}
