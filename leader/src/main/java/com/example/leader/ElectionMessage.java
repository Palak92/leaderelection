package com.example.leader;

public class ElectionMessage implements Message {
    int hops;
    int uuid;
    Direction direction;

    public ElectionMessage(int uuid, Direction direction, int hops) {
        this.uuid = uuid;
        this.direction = direction;
        this.hops = hops;
    }

    public String toString() {
        return "message by uuid:" + uuid + " direction:" + direction.toString() + " hops:" + hops;
    }
}
