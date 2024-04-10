
package com.example.leader;

public class LeaderAnnoucementMessage implements Message {
    int leaderId;

    public LeaderAnnoucementMessage(int leaderId) {
        this.leaderId = leaderId;
    }

    public String toString() {
        return "LeaderAnnoucementMessage is leader " + leaderId;
    }
}