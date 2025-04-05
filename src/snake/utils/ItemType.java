package snake.utils;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ItemType {
    APPLE, BOX, SICK_BALL, INVINCIBILITY_BALL;

    @JsonValue
    public String toValue() {
        return name();  
    }

    public String toString(){
        return name();
    }
}
