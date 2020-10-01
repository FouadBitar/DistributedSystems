package Client;


import java.util.*;
import java.io.Serializable;

public class Message implements Serializable {
    
    private static final long serialVersionUID = 1L;
    public Vector<String> message;

    // CREATE ENUM OF MESSAGE ACTIONS
    // THEN CREATE A GENERAL METHOD THAT RETRIEVES THE VALUES FROM THE STRING MESSAGE,
    // SO IF WE HAVE ADDFLIGHT IT WOULD CHECK ITS THE RIGHT ENUM, THEN RETRIEVE THE ARGUMENTS POSSIBLE FROM 
    // AN ALREADY TOKENIZED VECTOR OF STRINGS FROM THE CLIENT.

    public Message(Vector<String> mes) {
        this.message = mes;
    }

    public void printMessage() {
        System.out.println("printing message");
        for(String s : message) {
            System.out.println(s);
        }
    }
}
