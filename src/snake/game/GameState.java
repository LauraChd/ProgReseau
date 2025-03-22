package snake.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import snake.utils.FeaturesItem;
import java.util.List;

public class GameState {
    private Snake snake1;
    private Snake snake2;
    private List<FeaturesItem> items;  

    public GameState() {}

    public GameState(Snake snake1, Snake snake2, List<FeaturesItem> items) {
        this.snake1 = snake1;
        this.snake2 = snake2;
        this.items = items;
    }

    public Snake getSnake1() { return snake1; }
    public void setSnake1(Snake snake1) { this.snake1 = snake1; }

    public Snake getSnake2() { return snake2; }
    public void setSnake2(Snake snake2) { this.snake2 = snake2; }

    public List<FeaturesItem> getItems() { return items; }
    public void setItems(List<FeaturesItem> items) { this.items = items; }

    // Sérialisation JSON
    public String toJson() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(this);
    }

    // Désérialisation JSON
    public static GameState fromJson(String json) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(json, GameState.class);
    }
}
