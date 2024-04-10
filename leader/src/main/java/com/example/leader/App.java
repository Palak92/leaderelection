package com.example.leader;

import java.util.ArrayList;
import java.util.List;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        Node node1 = new Node(1);
        Node node2 = new Node(8);
        Node node3 = new Node(10);
        Node node4 = new Node(4);
        Node node5 = new Node(5);

        node1.setRight(node2);
        node1.setLeft(node5);

        node2.setRight(node3);
        node2.setLeft(node1);

        node3.setRight(node4);
        node3.setLeft(node2);

        node4.setRight(node5);
        node4.setLeft(node3);

        node5.setRight(node1);
        node5.setLeft(node4);

        List<Node> nodes = new ArrayList<>();
        nodes.add(node1);
        nodes.add(node2);
        nodes.add(node3);
        nodes.add(node4);
        nodes.add(node5);

        for (Node node : nodes) {
            Thread nodeThread = new Thread(node.getWorkerThread());
            nodeThread.start();
        }

        for (Node node : nodes) {
            node.initiateLeaderElection();
        }
    }
}
