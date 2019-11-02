package eu.spanhel.heating;

import java.io.Serializable;
import java.util.ArrayList;

class CRoomHeatingParams implements Serializable {
    ArrayList<String> rooms;
    String selectedRoom;
    Double setTemperature;

    CRoomHeatingParams() {
        rooms = new ArrayList<>();
    }
}
