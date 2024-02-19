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
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

public class HW3
{
    private static class Tree {
        private Node root;

        public Tree() {
            root = null;
        }

        public Node insert(String data) {
            if(root == null) {
                root = new Node(data, null);
                return root;
            } else {
                return root.insertChild(data);
            }
        }

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

        public Node append(String data) {
            if(root == null) {
                root = new Node(data, null);
                return root;
            } else {
                return root.appendChild(data);
            }
        }

        public Node find(String data) {
            if(root == null) return null;
            return root.findNode(data);
        }
    }

    private static class Node {
        String data;
        Node parent;
        List<Node> children = new LinkedList<>();

        public Node(String data, Node parent) {
            this.data = data;
            this.parent = parent;
        }

        public String getData() {
            return data;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void setData(String data) {
            this.data = data;
        }

        public void sort() {
            children.sort((a, b) -> a.data.compareTo(b.data));
        }

        public Node insertChild(String data) {
            Node child = new Node(data, this);
            children.add(0, child);
            return child;
        }

        public Node appendChild(String data) {
            Node child = new Node(data, this);
            children.add(child);
            return child;
        }

        public Node findNode(String data) {
            if(this.data.equals(data)) return this;
            for(Node child : children) {
                Node found = child.findNode(data);
                if(found != null) return found;
            }
            return null;
        }
    }

    private final Scanner data;
    private final Scanner queries;

    private Tree tree = new Tree();

    public HW3(final Scanner data, final Scanner queries) {
        this.data = data;
        this.queries = queries;
    }

    private void handleQueryLine () {
        final String line = queries.nextLine();
        // Split by spaces...
        String[] parts = line.split(" " );

        // First part is the command...
        if(parts[0].equals("GetEventsBySport")) {

        } else if(parts[0].equals("GetWinnersAndCountriesBySportAndEvent")) {

        } else if(parts[0].equals("GetAthleteWithMostMedals")) {

        } else if(parts[0].equals("GetAthleteWithMostGoldMetals")) {

        } else if(parts[0].equals("GetCountryWithMostMedals")) {

        } else if(parts[0].equals("GetCountryWithMostGoldMedals")) {

        } else if(parts[0].equals("getSportAndEventByAthlete")) {

        } else {
            System.out.println("Invalid command: " + parts[0]);
        }
    }

    private void handleDataLine() {
        final String line = data.nextLine();
        // Split by spaces...
        String[] parts = line.split(" " );
        String target = parts[0];
        // set nodes to be the rest of parts.
        String[] nodes = new String[parts.length - 1];
        System.arraycopy(parts, 1, nodes, 0, nodes.length);

        Node t = tree.createOrInsert(target);
        for(String n : nodes) {
            t.appendChild(n);
        }
    }

    public void run() {
        // First gather up the data...
        while (queries.hasNextLine()) {
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
            File file = new File(args[0]);
            Scanner data = new Scanner(file, StandardCharsets.US_ASCII.name());
            File file2 = new File(args[1]);
            Scanner queries = new Scanner(file2, StandardCharsets.US_ASCII.name());
            HW3 program = new HW3(data, queries);
            program.run();
        } catch (FileNotFoundException e) {
            System.out.println("File not found: " + args[0]);
            return;
        }
    }

}
