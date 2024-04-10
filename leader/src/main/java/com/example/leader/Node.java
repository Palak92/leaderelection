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
    private boolean terminateElection;

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
        terminateElection = false;
        currentPhase = 0; // Reset the phase
        messagesReceivedBack = 0; // Reset messages received counter

        // Create initial messages with the node's UID and the starting hop count
        ElectionMessage rightMessage = new ElectionMessage(this.uuid, Direction.Right, (int) Math.pow(2, currentPhase));
        ElectionMessage leftMessage = new ElectionMessage(this.uuid, Direction.Left, (int) Math.pow(2, currentPhase));

        // Send messages to the neighbors
        this.right.receiveMessage(rightMessage);
        this.left.receiveMessage(leftMessage);
    }

    private void sendMessages() {
        currentPhase++;
        messagesReceivedBack = 0;
        ElectionMessage rightMessage = new ElectionMessage(uuid, Direction.Right, (int) Math.pow(2, currentPhase));
        ElectionMessage leftMessage = new ElectionMessage(uuid, Direction.Left, (int) Math.pow(2, currentPhase));
        System.out.println("New Message is set by node" + uuid + ": " + rightMessage);
        right.receiveMessage(rightMessage);
        left.receiveMessage(leftMessage);
    }

    private void sendAnnouncement() {
        terminateElection = true;
        LeaderAnnoucementMessage annoucementMessage = new LeaderAnnoucementMessage(uuid);
        right.receiveMessage(annoucementMessage);
        left.receiveMessage(annoucementMessage);
    }

    private void processLeaderAnnoucement(LeaderAnnoucementMessage message) {
        terminateElection = true;
        System.out.println("Leader Annoucement got by " + uuid + " and leader is " + message.leaderId);
        right.receiveMessage(message);
        left.receiveMessage(message);
    }

    private class WorkerThread extends Thread {
        private Node node;

        public WorkerThread(Node node) {
            this.node = node;
        }

        @Override
        public void run() {
            while (!terminateElection) {
                Message message = node.messageQueue.poll();
                if (message != null) {
                    if (message instanceof ElectionMessage) {
                        processMessage((ElectionMessage) message);
                    } else {
                        node.processLeaderAnnoucement((LeaderAnnoucementMessage) message);
                    }
                }
            }
        }

        private void processMessage(ElectionMessage message) {
            if (message.uuid > node.uuid) {
                // formward the message
                if (message.hops > 0) {
                    ElectionMessage nextMessage = new ElectionMessage(message.uuid, message.direction,
                            message.hops - 1);
                    if (nextMessage.direction.equals(Direction.Left)) {
                        node.left.receiveMessage(nextMessage);
                    } else {
                        node.right.receiveMessage(nextMessage);
                    }
                } else if (message.hops == 0) {
                    ElectionMessage nextMessage = new ElectionMessage(message.uuid, message.direction, message.hops);
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
                    node.sendAnnouncement();
                }
            }
        }
    }
}
