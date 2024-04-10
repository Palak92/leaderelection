package com.example.leader;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {
    private Node right;
    private Node left;
    private int uuid;
    private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<>();
    private WorkerThread workerThread;
    private int messagesReceivedBack = 0;
    private int currentPhase;

    public Node(int uuid) {
        this.uuid = uuid;
        workerThread = new WorkerThread(this);
    }

    public WorkerThread getWorkerThread() {
        return workerThread;
    }

    public void setRight(Node right) {
        this.right = right;
    }

    public void setLeft(Node left) {
        this.left = left;
    }

    public void receiveMessage(Message message) {
        System.out.println("Message is recieved by node" + uuid + ": " + message);
        messageQueue.add(message);
    }

    // Inside your Node class
    public void initiateLeaderElection() {
        currentPhase = 0; // Reset the phase
        messagesReceivedBack = 0; // Reset messages received counter

        // Create initial messages with the node's UID and the starting hop count
        Message rightMessage = new Message(this.uuid, Direction.Right, (int) Math.pow(2, currentPhase));
        Message leftMessage = new Message(this.uuid, Direction.Left, (int) Math.pow(2, currentPhase));

        // Send messages to the neighbors
        this.right.receiveMessage(rightMessage);
        this.left.receiveMessage(leftMessage);
    }

    private void sendMessages() {
        currentPhase++;
        messagesReceivedBack = 0;
        Message rightMessage = new Message(uuid, Direction.Right, (int) Math.pow(2, currentPhase));
        Message leftMessage = new Message(uuid, Direction.Left, (int) Math.pow(2, currentPhase));
        System.out.println("New Message is set by node" + uuid + ": " + rightMessage);
        right.receiveMessage(rightMessage);
        left.receiveMessage(leftMessage);
    }

    private class WorkerThread extends Thread {
        private Node node;

        public WorkerThread(Node node) {
            this.node = node;
        }

        @Override
        public void run() {
            while (true) {
                Message message = node.messageQueue.poll();
                if (message != null) {
                    processMessage(message);
                }
            }
        }

        private void processMessage(Message message) {
            if (message.uuid > node.uuid) {
                // formward the message
                if (message.hops > 0) {
                    Message nextMessage = new Message(message.uuid, message.direction, message.hops - 1);
                    if (nextMessage.direction.equals(Direction.Left)) {
                        node.left.receiveMessage(nextMessage);
                    } else {
                        node.right.receiveMessage(nextMessage);
                    }
                } else if (message.hops == 0) {
                    Message nextMessage = new Message(message.uuid, message.direction, message.hops);
                    if (nextMessage.direction.equals(Direction.Left)) {
                        node.right.receiveMessage(nextMessage);
                    } else {
                        node.left.receiveMessage(nextMessage);
                    }
                }
            } else if (message.uuid == node.uuid) {
                // increase hops
                if (message.hops == 0) {
                    node.messagesReceivedBack++;
                    if (node.messagesReceivedBack == 2) {
                        node.sendMessages();
                    }
                } else {
                    // i am maximum
                    System.out.println("node " + uuid + "is the leader");
                }
            }
        }
    }
}
