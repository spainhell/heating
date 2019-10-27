package eu.spanhel.heating;

import java.io.Serializable;

class CRoomHeatingParams implements Serializable {
    String[] rooms;
    String selectedRoom;
    Double setTemperature;

    CRoomHeatingParams() {
        rooms = new String[5];
    }
}
