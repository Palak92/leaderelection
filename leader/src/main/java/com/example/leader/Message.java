package com.example.leader;

public class Message {
    int hops;
    int uuid;
    Direction direction;

    public Message(int uuid, Direction direction, int hops) {
        this.uuid = uuid;
        this.direction = direction;
        this.hops = hops;
    }

    public String toString() {
        return "message by uuid:" + uuid + " direction:" + direction.toString() + " hops:" + hops;
    }
}
