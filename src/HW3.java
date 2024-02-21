/*
 * Author:  Andrew Bastien
 * Email: abastien2021@my.fit.edu
 * Course:  CSE 2010
 * Section: 23
 * Term: Spring 2024
 * Project: HW3, Trees
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.function.Consumer;

public class HW3
{
    private static class Tree {
        private Node root;

        public Tree() {
            root = null;
        }

        // Used by the input handler to get-or-create target nodes for adding children to.
        public Node createOrInsert(String data) {
            if(root == null) {
                root = new Node(data, null);
                return root;
            } else {
                Node found = root.findNode(data);
                if(found == null) {
                    return root.insertChild(data);
                } else {
                    return found;
                }
            }
        }

        // Search the tree for a node with the given data.
        public Node find(String data) {
            if(root == null) return null;
            return root.findNode(data);
        }

        // Traverse tree using lambda as visitor to run arbitrary code.
        public void inOrderLambda(Consumer<Node> visitor) {
            if(root == null) return;
            root.inOrderLambda(visitor);
        }

    }

    // The node class for our tree.
    private static class Node {
        String data;
        Node parent;
        List<Node> children = new LinkedList<>();

        public Node(String data, Node parent) {
            this.data = data;
            this.parent = parent;
        }

        // traverses tree recursively and calls the visitor.accept on each node.
        public void inOrderLambda(Consumer<Node> visitor) {
            for(Node child : children) {
                child.inOrderLambda(visitor);
            }
            visitor.accept(this);
        }

        public String getData() {
            return data;
        }

        // used for searching the tree; athletes have special strings as names, this function returns the name part.
        public String getName() {
            if(data.contains(":")) {
                  return data.split(":")[0];
               } else {
                  return data;
            }
        }

        public List<Node> getChildren() {
            return children;
        }

        public void sort() {
            children.sort((a, b) -> a.data.compareTo(b.data));
        }

        // This ended up unused, because appendChild worked out more accurately for the given output cases.
        // All alphabetical sorting is done on specific kinds of output, anyways, so this has no purpose.
        public Node insertChild(String data) {
            Node child = new Node(data, this);
            children.add(0, child);
            sort();
            return child;
        }

        // Simple append operation to the children list.
        public Node appendChild(String data) {
            Node child = new Node(data, this);
            children.add(child);
            return child;
        }

        // used for searching. Finds a node with the given data as its name.
        public Node findNode(String data) {
            if(this.getName().equals(data)) return this;
            for(Node child : children) {
                Node found = child.findNode(data);
                if(found != null) return found;
            }
            return null;
        }

        // Athlete names always have a : in them as a country separator. We can use this
        // to identify them with searching.
        public boolean isAthlete() {
            return data.contains(":");
        }
    }

    private final Scanner data;
    private final Scanner queries;

    private final Tree tree = new Tree();

    public HW3(final Scanner data, final Scanner queries) {
        this.data = data;
        this.queries = queries;
    }

    private String handleGetEventsBySport(String sport) {
        // We just need to use tree.findNode(sport) and return the joined children.
         Node sportNode = tree.find(sport);
         if(sportNode == null) return "";
         LinkedList<String> events = new LinkedList<>();
         // insert the events into the list.
         for(Node event : sportNode.getChildren()) {
             events.add(event.getData());
         }

         events.addFirst(sport);
         return String.join(" ", events);
    }

    private Node getSportEvent(String sport, String event) {
        Node sportNode = tree.find(sport);
        if(sportNode == null) return null;
        return sportNode.findNode(event);
    }

    private String handleGetWinnersAndCountriesBySportAndEvent(String sport, String event) {
         // We just need to use tree.findNode(sport) and return the joined children.
         Node eventNode = getSportEvent(sport, event);
         if(eventNode == null) return "";

         LinkedList<String> winners = new LinkedList<>();
         // insert the winners into the list.
         for(Node winner : eventNode.getChildren()) {
             winners.add(winner.getData());
         }
         winners.addFirst(event);
         winners.addFirst(sport);
         return String.join(" ", winners);
    }

    private String handleGetGoldMedalistAndCountryBySportAndEvent(String sport, String event) {
        // We just need to use tree.findNode(sport) and return the joined children.
        Node eventNode = getSportEvent(sport, event);

        // even then, there might not be a winner.
         if(eventNode != null && !eventNode.getChildren().isEmpty()) {
               Node winner = eventNode.getChildren().get(0);
               return String.format("%s %s %s", sport, event, winner.getData());
         }
        return String.format("%s %s", sport, event);
    }

    // locates and formats the most medals and most gold medals queries.
    private String mostCounter(HashMap<String, Integer> counts) {
         // We need to display <count> [names] where names are the names of the athletes or countries with the most medals.
        // There could be multiple [names]. We need to find the highest Integer value...
         int max = 0;
         for(int count : counts.values()) {
             if(count > max) max = count;
         }
         // Now we need to find all the names that have that count.
        LinkedList<String> names = new LinkedList<>();
         for(String name : counts.keySet()) {
               if(counts.get(name) == max) names.add(name);
         }
         // sort names...
         names.sort(String::compareTo);
         names.addFirst(Integer.toString(max));
         // return a string joined by spaces.
         return String.join(" ", names);
    }

    // used to search between athlete names or country names for the most medals and most gold medals operations.
    private String mostMedalsHelper(int idx) {
        // let's just use a HashMap to count the occurrences.
        // My only other option is to do some kind of weird list of structs that accomplishes the same thing but
        // using worse performance. Yes, I could use a class that just has the name and win count and then sort it,
        // but screw it, why write that? This is exactly what an associative array if supposed to do.
        // The athlete's name is a key and the value is their win count.
        HashMap<String, Integer> counts = new HashMap<>();
        tree.inOrderLambda((node) -> {
            if (node.isAthlete()) {
                String[] parts = node.getData().split(":");
                String name = parts[idx];
                int count = counts.getOrDefault(name, 0);
                counts.put(name, count + 1);
            }
        });

        return mostCounter(counts);
    }

    // used to search between athlete names or country names for the most medals and most gold medals operations.
    private String mostGoldHelper(int idx) {
        // This is similar to mostMedalsHelper but instead we need to only count athletes who are the first children of their parent.
         // This is because the first child is the gold medal winner.
         HashMap<String, Integer> counts = new HashMap<>();
         tree.inOrderLambda((node) -> {
             if (node.isAthlete() && node.parent.getChildren().get(0) == node) {
                 String[] parts = node.getData().split(":");
                 String name = parts[idx];
                 int count = counts.getOrDefault(name, 0);
                 counts.put(name, count + 1);
             }
         });

         return mostCounter(counts);

    }

    // These are the handlers for the queries.
    private String handleGetAthleteWithMostMedals() {
        return mostMedalsHelper(0);
    }

    private String handleGetAthleteWithMostGoldMetals() {
        return mostGoldHelper(0);
    }

    private String handleGetCountryWithMostMedals() {
        return mostMedalsHelper(1);
    }

    private String handleGetCountryWithMostGoldMedals() {
        return mostGoldHelper(1);
    }

    private String handleGetSportAndEventByAthlete(String athlete) {
        // This one is easy. we just need to find every instance of the athlete and walk up two parents.
        // let's use that visitor pattern again.
         LinkedList<String> results = new LinkedList<>();
         tree.inOrderLambda((node) -> {
             if(node.isAthlete() && node.getName().equals(athlete)) {
                   Node event = node.parent;
                   Node sport = event.parent;
                   results.add(String.format("%s: %s", sport.getData(), event.getData()));
             }
         });
         results.sort(String::compareTo);
         results.addFirst(athlete);
         return String.join(" ", results);
    }

    private void handleQueryLine () {
        final String line = queries.nextLine();
        // Split by spaces...
        String[] parts = line.split(" " );

        LinkedList<String> results = new LinkedList<>();
        results.addFirst(parts[0]);
        String out = "";

        // First part is the command...
        // I would love to use switch-case here but the java IDE in use doesn't like it.
        if(parts[0].equals("GetEventsBySport")) {
            out = handleGetEventsBySport(parts[1]);
        } else if(parts[0].equals("GetWinnersAndCountriesBySportAndEvent")) {
            out = handleGetWinnersAndCountriesBySportAndEvent(parts[1], parts[2]);
        } else if(parts[0].equals("GetAthleteWithMostMedals")) {
            out = handleGetAthleteWithMostMedals();
        } else if(parts[0].equals("GetAthleteWithMostGoldMetals")) {
            out = handleGetAthleteWithMostGoldMetals();
        } else if(parts[0].equals("GetCountryWithMostMedals")) {
            out = handleGetCountryWithMostMedals();
        } else if(parts[0].equals("GetCountryWithMostGoldMedals")) {
            out = handleGetCountryWithMostGoldMedals();
        } else if(parts[0].equals("GetSportAndEventByAthlete")) {
            out = handleGetSportAndEventByAthlete(parts[1]);
        } else if(parts[0].equals("GetGoldMedalistAndCountryBySportAndEvent")) {
            out = handleGetGoldMedalistAndCountryBySportAndEvent(parts[1], parts[2]);
        } else {
            System.out.println("Invalid command: " + parts[0]);
            return;
        }

        results.addLast(out);

        System.out.println(String.join(" ", results));
    }

    // Parses a line of data input and adds it to the tree.
    private void handleDataLine() {
        final String line = data.nextLine();
        // Split by spaces...
        String[] parts = line.split(" " );
        String target = parts[0];
        // set nodes to be the rest of parts.
        String[] nodes = new String[parts.length - 1];
        System.arraycopy(parts, 1, nodes, 0, nodes.length);

        // get-or-create our basic node.
        Node t = tree.createOrInsert(target);
        for(String n : nodes) {
            // OLD LOGIC: if n contains a :, use append, else use insert.
            // NEW LOGIC: just use appendChild, because lexicographically sorting
            // does not match the example output.
            t.appendChild(n);
        }
    }

    public void run() {
        // First gather up the data...
        while (data.hasNextLine()) {
            handleDataLine();
        }

        // Execute queries.
        while (queries.hasNextLine()) {
            handleQueryLine();
        }

    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("No file path provided.");
            return;
        }

        // use java.util.Scanner because dang this is complicated.
        try {
            Scanner data = new Scanner(new File(args[0]), StandardCharsets.US_ASCII.name());
            Scanner queries = new Scanner(new File(args[1]), StandardCharsets.US_ASCII.name());
            HW3 program = new HW3(data, queries);
            program.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
        }
    }

}
