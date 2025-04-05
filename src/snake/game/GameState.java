package snake.game;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import snake.utils.FeaturesSnake;
import snake.model.InputMap;

@JsonIgnoreProperties(ignoreUnknown = true) // Ignore les propriétés non définies
public class GameState {
    private Snake snake1;
    private Snake snake2;
    private int turn;
    private int time;
    private List<Snake> snake_liste;
    private List<Item> item_liste;
    private InputMap carte;
    private double pitem;
    private boolean gameContinue;
    private List<FeaturesSnake> featuresSnakes;
    //private List<Item> items;


    public GameState() {}

    // Getters et Setters

    public int getTurn() { return turn; }
    public void setTurn(int turn) { this.turn = turn; }

    public int getTime() { return time; }
    public void setTime(int time) { this.time = time; }

    public List<Snake> getSnake_liste() { return snake_liste; }
    public void setSnake_liste(List<Snake> snake_liste) {
        this.snake_liste = snake_liste;
        if (snake_liste != null && snake_liste.size() >= 2) {
            this.snake1 = snake_liste.get(0);
            this.snake2 = snake_liste.get(1);
        }
    }
    

    public List<Item> getItem_liste() { return item_liste; }
    public void setItem_liste(List<Item> item_liste) { this.item_liste = item_liste; }

    public InputMap getCarte() { return carte; }
    public void setCarte(InputMap carte) { this.carte = carte; }

    public double getPitem() { return pitem; }
    public void setPitem(double pitem) { this.pitem = pitem; }

    public boolean isGameContinue() { return gameContinue; }
    public void setGameContinue(boolean gameContinue) { this.gameContinue = gameContinue; }

    public List<FeaturesSnake> getFeaturesSnakes() {
        return featuresSnakes;
    }

    public void setFeaturesSnakes(List<FeaturesSnake> featuresSnakes) {
        this.featuresSnakes = featuresSnakes;
    }

    public Snake getSnake1() { return snake1; }
    public void setSnake1(Snake snake1) { this.snake1 = snake1; }

    public Snake getSnake2() { return snake2; }
    public void setSnake2(Snake snake2) { this.snake2 = snake2; }

    /*public List<Item> getItems() { return items; }
    public void setItems(List<Item> items) { this.items = items; }*/
    
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
