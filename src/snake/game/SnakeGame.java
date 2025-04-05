package snake.game;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import snake.model.InputMap;
import snake.utils.AgentAction;
import snake.utils.ColorSnake;
import snake.utils.FeaturesItem;
import snake.utils.FeaturesSnake;
import snake.utils.ItemType;
import snake.utils.Position;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class SnakeGame extends Game implements KeyListener {

    public ArrayList<Snake> snake_liste;
    public ArrayList<Item> item_liste;
    public InputMap carte;
    private double pitem;// proba apparition item
    public boolean gameContinue;

    public SnakeGame() {
    }

    public SnakeGame(int maxturn, InputMap carte) {
        super(maxturn);
        this.carte = carte;
        this.snake_liste = new ArrayList<>();
        this.item_liste = new ArrayList<>();
        this.pitem = 0.3;
        this.gameContinue = true;
    }

    @Override
    public void gameOver() {
        System.out.println("jeu perdu");
        gameContinue = false;
        isRunning = false;
    }

    public ArrayList<Snake> getSnake_liste() {
        return snake_liste;
    }

    public void setSnake_liste(ArrayList<Snake> snake_liste) {
        this.snake_liste = snake_liste;
    }

    public ArrayList<Item> getItem_liste() {
        return item_liste;
    }

    public void setItem_liste(ArrayList<Item> item_liste) {
        this.item_liste = item_liste;
    }

    public InputMap getCarte() {
        return carte;
    }

    public void setCarte(InputMap carte) {
        this.carte = carte;
    }

    public double getPitem() {
        return pitem;
    }

    public void setPitem(double pitem) {
        this.pitem = pitem;
    }

    public boolean isGameContinue() {
        return gameContinue;
    }

    public void setGameContinue(boolean gameContinue) {
        this.gameContinue = gameContinue;
    }

    @Override
    public void initializeGame() {
        snake_liste.clear();
        item_liste.clear();
        gameContinue = true;
        SnakeFactory snakeFactory = new SnakeFactory();
        // CARTE INITIALE
        for (FeaturesSnake s : carte.getStart_snakes()) {
            /*
             * if (s.getColorSnake() == ColorSnake.Red)
             * snake_liste.add(snakeFactory.creerAgentIA(s.getPositions().get(0).getX(),
             * s.getPositions().get(0).getY(), s.getColorSnake()));
             * else
             * snake_liste.add(snakeFactory.creerAgentHumain(s.getPositions().get(0).getX(),
             * s.getPositions().get(0).getY(), s.getColorSnake()));
             */
            //snake_liste.add(snakeFactory.creerAgentHumain(s.getPositions().get(0).getX(),s.getPositions().get(0).getY(), s.getColorSnake()));
            snake_liste.add(snakeFactory.creerAgentIA(s.getPositions().get(0).getX(),s.getPositions().get(0).getY(), s.getColorSnake()));
        }

        for (FeaturesItem i : carte.getStart_items()) {
            item_liste.add(new Item(i.getX(), i.getY(), i.getItemType()));
        }
    }

    @Override
    public boolean gameContinue() {
        return gameContinue;
    }

    @Override
    public void takeTurn() {
        for (Snake s : snake_liste) {

            if (s.is_sick == false) {
                for (Item item : new ArrayList<>(item_liste)) {// utilisation d'une copie pour éviter les problèmes de
                                                               // suppression
                    // quand serpent mange pomme
                    if (item.itemType == ItemType.APPLE &&
                            item.x == s.position.get(0).getX() &&
                            item.y == s.position.get(0).getY()) {

                        s.augmenter_taille();
                        s.incrScore(1);
                        item_liste.remove(item);

                        Position pommePosition = genererPositionLibre();
                        if (pommePosition != null) {
                            Item bonus = new Item(pommePosition.getX(), pommePosition.getY(), ItemType.APPLE);
                            item_liste.add(bonus);
                        }

                        // apparition bonus
                        if (Math.random() < pitem) {
                            Position bonusPosition = genererPositionLibre();
                            if (bonusPosition != null) {
                                ItemType[] typesBonus = { ItemType.BOX, ItemType.SICK_BALL,
                                        ItemType.INVINCIBILITY_BALL };
                                ItemType choix = typesBonus[(int) (Math.random() * typesBonus.length)];

                                Item bonus = new Item(bonusPosition.getX(), bonusPosition.getY(), choix);
                                item_liste.add(bonus);
                            }
                        }
                    }
                    // quand serpent mange box
                    if (item.itemType == ItemType.BOX &&
                            item.x == s.position.get(0).getX() &&
                            item.y == s.position.get(0).getY()) {
                        // transforme type de l'item soit en SICK_BALL ou INVINCIBILITY_BALL
                        if (Math.random() < 0.5) {
                            item.itemType = ItemType.SICK_BALL;
                            s.incrScore(-3);
                        } else {
                            item.itemType = ItemType.INVINCIBILITY_BALL;
                            s.incrScore(5);
                        }
                    }
                    // quand serpent mange SICK_BALL
                    if (item.itemType == ItemType.SICK_BALL &&
                            item.x == s.position.get(0).getX() &&
                            item.y == s.position.get(0).getY()) {
                        s.is_sick = true;
                        s.tour_malade_restant = 20;
                        item_liste.remove(item);
                        s.incrScore(-2);
                    }
                    // quand serpent mange INVINCIBILITY_BALL
                    if (item.itemType == ItemType.INVINCIBILITY_BALL &&
                            item.x == s.position.get(0).getX() &&
                            item.y == s.position.get(0).getY()) {
                        s.is_invincible = true;
                        s.tour_invincible_restant = 20;
                        item_liste.remove(item);
                        s.incrScore(4);
                    }
                }
            }
            if (s.is_sick == true && s.tour_malade_restant == 0)
                s.is_sick = false;
            if (s.tour_malade_restant > 0)
                s.tour_malade_restant--;

            if (s.is_invincible == true && s.tour_invincible_restant == 0)
                s.is_invincible = false;
            if (s.tour_invincible_restant > 0)
                s.tour_invincible_restant--;

            s.moveAgent(s.getStrategie().getCurrentAction(s, this), carte.getSize_x(), carte.getSize_y());

        }
        agent_mort();

        serializeGameState();

    }

    // Méthode pour sérialiser l'état du jeu
    private void serializeGameState() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT); // Format JSON lisible

        try {
            mapper.writeValue(new File("gameState.json"), this);
            //System.out.println("État du jeu sauvegardé dans gameState.json");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void agent_mort() {

        boolean[][] walls = carte.getWalls();
        ArrayList<Snake> serpent_suppression = new ArrayList<>();

        for (Snake s : snake_liste) {
            Position tete = s.position.get(0);
            int teteX = tete.getX();
            int teteY = tete.getY();

            // collision avec les murs
            if (walls[teteX][teteY] && s.is_invincible == false) {
                serpent_suppression.add(s);
                continue;
            }

            // collision avec son propre corps
            for (int k = 1; k < s.position.size(); k++) {// on ignore la tête k = 0
                Position bodyPart = s.position.get(k);
                if (teteX == bodyPart.getX() && teteY == bodyPart.getY()) {
                    serpent_suppression.add(s);
                    break;
                }
            }

            // collision avec les autres serpents
            for (Snake otherSnake : snake_liste) {
                if (otherSnake == s)
                    continue;// ignore le serpent lui-même

                for (Position pos : otherSnake.position) {
                    if (teteX == pos.getX() && teteY == pos.getY()) {
                        if (s.position.size() > otherSnake.position.size()) {
                            if (otherSnake.is_invincible == false)
                                serpent_suppression.add(otherSnake);
                        } else if (s.position.size() < otherSnake.position.size()) {
                            if (s.is_invincible == false)
                                serpent_suppression.add(s);
                        } else {
                            if (s.is_invincible == false)
                                serpent_suppression.add(s);
                            if (otherSnake.is_invincible == false)
                                serpent_suppression.add(otherSnake);
                        }
                        break;
                    }
                }
            }
        }

        snake_liste.removeAll(serpent_suppression);

        if (liste_snake_vide())
            gameOver();
    }

    public boolean liste_snake_vide() {
        if (snake_liste.size() == 0) {
            return true;
        } else
            return false;

    }

    public ArrayList<FeaturesSnake> getFeaturesSnakes() {
        ArrayList<FeaturesSnake> featuresSnakes = new ArrayList<>();
        for (Snake snake : snake_liste) {
            FeaturesSnake featuresSnake = new FeaturesSnake(
                    snake.position,
                    snake.agentAction,
                    snake.couleur,
                    snake.is_invincible,
                    snake.is_sick);
            featuresSnakes.add(featuresSnake);
        }
        return featuresSnakes;
    }

    public ArrayList<FeaturesItem> getFeaturesItems() {
        ArrayList<FeaturesItem> featuresItems = new ArrayList<>();
        for (Item item : item_liste) {
            FeaturesItem featuresItem = new FeaturesItem(item.x, item.y, item.itemType);
            featuresItems.add(featuresItem);
        }
        return featuresItems;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        for (Snake s : snake_liste) {
            if (s.strategie instanceof StrategieClavier) {

                switch (e.getKeyCode()) {
                    case KeyEvent.VK_NUMPAD8:
                        if (s.isLegalMove(AgentAction.MOVE_UP))
                            s.agentAction = AgentAction.MOVE_UP;
                        break;
                    case KeyEvent.VK_NUMPAD5:
                        if (s.isLegalMove(AgentAction.MOVE_DOWN))
                            s.agentAction = AgentAction.MOVE_DOWN;
                        break;
                    case KeyEvent.VK_NUMPAD4:
                        if (s.isLegalMove(AgentAction.MOVE_LEFT))
                            s.agentAction = AgentAction.MOVE_LEFT;
                        break;
                    case KeyEvent.VK_NUMPAD6:
                        if (s.isLegalMove(AgentAction.MOVE_RIGHT))
                            s.agentAction = AgentAction.MOVE_RIGHT;
                        break;
                    default:
                        break;
                }
            }
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    private Position genererPositionLibre() {
        ArrayList<Position> positionsLibres = new ArrayList<>();

        for (int x = 0; x < carte.getSize_x(); x++) {
            for (int y = 0; y < carte.getSize_y(); y++) {
                boolean estOccupee = false;

                // si occupée par un item
                for (Item item : item_liste) {
                    if (item.x == x && item.y == y) {
                        estOccupee = true;
                        break;
                    }
                }

                // si occupée par un mur
                boolean[][] walls = carte.getWalls();
                if (walls[x][y]) {
                    estOccupee = true;
                }

                // si occupée par un serpent
                for (Snake snake : snake_liste) {
                    for (Position pos : snake.position) {
                        if (pos.getX() == x && pos.getY() == y) {
                            estOccupee = true;
                            break;
                        }
                    }
                }

                if (!estOccupee) {
                    positionsLibres.add(new Position(x, y));
                }
            }
        }

        if (!positionsLibres.isEmpty()) {
            int index = (int) (Math.random() * positionsLibres.size());
            return positionsLibres.get(index);
        } else {
            return null;
        }
    }

    public void updateGame() {
        if (!gameContinue)
            return; // Arrêter si le jeu est terminé

        // Appliquer les actions et déplacer les serpents
        for (Snake s : snake_liste) {
            if (s.is_sick && s.tour_malade_restant > 0) {
                s.tour_malade_restant--;
                if (s.tour_malade_restant == 0)
                    s.is_sick = false;
            }
            if (s.is_invincible && s.tour_invincible_restant > 0) {
                s.tour_invincible_restant--;
                if (s.tour_invincible_restant == 0)
                    s.is_invincible = false;
            }

            // Déplacer le serpent selon sa stratégie
            s.moveAgent(s.getStrategie().getCurrentAction(s, this), carte.getSize_x(), carte.getSize_y());
        }

        // Gérer les collisions et interactions avec les objets
        for (Snake s : new ArrayList<>(snake_liste)) {
            Position tete = s.position.get(0);

            // Vérifier collision avec les murs
            if (carte.getWalls()[tete.getX()][tete.getY()] && !s.is_invincible) {
                snake_liste.remove(s);
                continue;
            }

            // Vérifier collision avec les items
            for (Item item : new ArrayList<>(item_liste)) {
                if (item.x == tete.getX() && item.y == tete.getY()) {
                    switch (item.itemType) {
                        case APPLE:
                            s.augmenter_taille();
                            s.incrScore(1);
                            item_liste.remove(item);
                            break;
                        case BOX:
                            if (Math.random() < 0.5) {
                                item.itemType = ItemType.SICK_BALL;
                                s.incrScore(-3);
                            } else {
                                item.itemType = ItemType.INVINCIBILITY_BALL;
                                s.incrScore(5);
                            }
                            break;
                        case SICK_BALL:
                            s.is_sick = true;
                            s.tour_malade_restant = 20;
                            item_liste.remove(item);
                            s.incrScore(-2);
                            break;
                        case INVINCIBILITY_BALL:
                            s.is_invincible = true;
                            s.tour_invincible_restant = 20;
                            item_liste.remove(item);
                            s.incrScore(4);
                            break;
                    }
                }
            }
        }

        // Vérifier si des serpents sont morts
        agent_mort();

        // Ajouter aléatoirement de nouveaux items
        if (Math.random() < pitem) {
            Position pos = genererPositionLibre();
            if (pos != null) {
                item_liste.add(new Item(pos.getX(), pos.getY(), ItemType.APPLE));
            }
        }

        // Vérifier si le jeu doit continuer
        if (liste_snake_vide()) {
            gameOver();
        }

        // Sérialiser et envoyer l'état du jeu
        serializeGameState();
    }

    public void updateGameFromGameState(GameState gs) {
        System.out.println("DEBUT");
        for(Snake s : snake_liste){
            System.out.println("real snake " + s.toString());
        }
        for(Item i : item_liste){
            System.out.println("real item " + i.toString());
        }
        //GET NEW SNAKES
        Snake newS1 = new Snake();
        Snake newS2 = new Snake();
        List<Snake> gsSnakeList = gs.getSnake_liste();
        //s1
        newS1.setTaille(gsSnakeList.get(0).getTaille());
        newS1.setCouleur(gsSnakeList.get(0).getCouleur());
        newS1.setAgentAction(gsSnakeList.get(0).getAgentAction());
        newS1.setIs_invincible(gsSnakeList.get(0).isIs_invincible());
        newS1.setIs_sick(gsSnakeList.get(0).isIs_sick());
        newS1.setTour_invincible_restant(gsSnakeList.get(0).getTour_invincible_restant());
        newS1.setTour_malade_restant(gsSnakeList.get(0).getTour_malade_restant());
        newS1.setScore(gsSnakeList.get(0).getScore());
        newS1.setPosition(gsSnakeList.get(0).getPosition());
        newS1.setStrategie(gsSnakeList.get(0).getStrategie());
        //newS1.getStrategie().setSnake(newS1);
        //s2
        newS2.setTaille(gsSnakeList.get(1).getTaille());
        newS2.setCouleur(gsSnakeList.get(1).getCouleur());
        newS2.setAgentAction(gsSnakeList.get(1).getAgentAction());
        newS2.setIs_invincible(gsSnakeList.get(1).isIs_invincible());
        newS2.setIs_sick(gsSnakeList.get(1).isIs_sick());
        newS2.setTour_invincible_restant(gsSnakeList.get(1).getTour_invincible_restant());
        newS2.setTour_malade_restant(gsSnakeList.get(1).getTour_malade_restant());
        newS2.setScore(gsSnakeList.get(1).getScore());
        newS2.setPosition(gsSnakeList.get(1).getPosition());
        newS2.setStrategie(gsSnakeList.get(1).getStrategie());
        //newS2.getStrategie().setSnake(newS2);

        ArrayList<Snake> newrealSnakes = new ArrayList<>();
        newrealSnakes.add(newS1);
        newrealSnakes.add(newS2);
        setSnake_liste(newrealSnakes);

        for(Snake s : snake_liste){
            System.out.println("new snake " + s.toString());
        }

        //GET NEW ITEMS
        List<Item> gsItemList = gs.getItem_liste();
        ArrayList<Item> newrealItems = new ArrayList<>();
        for(Item i : gsItemList){
            Item newI = new Item();
            newI.setItemType(i.getItemType());
            newI.setX(i.getX());
            newI.setY(i.getY());
            newrealItems.add(newI);
        }
        setItem_liste(newrealItems);

        for(Item i : item_liste){
            System.out.println("new item " + i.toString());
        }
        


        if (!gameContinue)
        return; // Arrêter si le jeu est terminé

    // Appliquer les actions et déplacer les serpents
    for (Snake s : snake_liste) {
        if (s.is_sick && s.tour_malade_restant > 0) {
            s.tour_malade_restant--;
            if (s.tour_malade_restant == 0)
                s.is_sick = false;
        }
        if (s.is_invincible && s.tour_invincible_restant > 0) {
            s.tour_invincible_restant--;
            if (s.tour_invincible_restant == 0)
                s.is_invincible = false;
        }

        // Déplacer le serpent selon sa stratégie
        s.moveAgent(s.getStrategie().getCurrentAction(s, this), carte.getSize_x(), carte.getSize_y());
    }

    // Gérer les collisions et interactions avec les objets
    for (Snake s : new ArrayList<>(snake_liste)) {
        Position tete = s.position.get(0);

        // Vérifier collision avec les murs
        if (carte.getWalls()[tete.getX()][tete.getY()] && !s.is_invincible) {
            snake_liste.remove(s);
            continue;
        }

        // Vérifier collision avec les items
        for (Item item : new ArrayList<>(item_liste)) {
            if (item.x == tete.getX() && item.y == tete.getY()) {
                switch (item.itemType) {
                    case APPLE:
                        s.augmenter_taille();
                        s.incrScore(1);
                        item_liste.remove(item);
                        break;
                    case BOX:
                        if (Math.random() < 0.5) {
                            item.itemType = ItemType.SICK_BALL;
                            s.incrScore(-3);
                        } else {
                            item.itemType = ItemType.INVINCIBILITY_BALL;
                            s.incrScore(5);
                        }
                        break;
                    case SICK_BALL:
                        s.is_sick = true;
                        s.tour_malade_restant = 20;
                        item_liste.remove(item);
                        s.incrScore(-2);
                        break;
                    case INVINCIBILITY_BALL:
                        s.is_invincible = true;
                        s.tour_invincible_restant = 20;
                        item_liste.remove(item);
                        s.incrScore(4);
                        break;
                }
            }
        }
        }

        // Vérifier si des serpents sont morts
        agent_mort();

        // Ajouter aléatoirement de nouveaux items
        if (Math.random() < pitem) {
            Position pos = genererPositionLibre();
            if (pos != null) {
                item_liste.add(new Item(pos.getX(), pos.getY(), ItemType.APPLE));
            }
        }

        // Vérifier si le jeu doit continuer
        if (liste_snake_vide()) {
            gameOver();
        }

        for(Snake s : snake_liste){
            System.out.println("real snake end " + s.toString());
        }
        for(Item i : item_liste){
            System.out.println("real item end " + i.toString());
        }
        for(Snake s : gsSnakeList){
            System.out.println("gs snake end " + s.toString());
        }

        // Sérialiser et envoyer l'état du jeu
        serializeGameState();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // Informations générales sur le jeu
        sb.append("État du Jeu :\n");
        sb.append("Le jeu continue : ").append(gameContinue).append("\n");
        sb.append("Probabilité apparition item : ").append(pitem).append("\n");

        // Informations sur la carte (dimensions)
        sb.append("Dimensions de la carte : ").append(carte.getSize_x()).append(" x ").append(carte.getSize_y())
                .append("\n");

        // Liste des serpents
        sb.append("Serpents :\n");
        for (Snake snake : snake_liste) {
            sb.append("  Serpent ").append(snake.getCouleur()).append(" (").append(snake.couleur).append(") :\n");
            sb.append("    Taille : ").append(snake.position.size()).append("\n");
            sb.append("    Score : ").append(snake.score).append("\n");
            sb.append("    Invincible : ").append(snake.is_invincible).append("\n");
            sb.append("    Malade : ").append(snake.is_sick).append("\n");
            sb.append("    Position : ").append(snake.position.get(0).getX()).append(", ")
                    .append(snake.position.get(0).getY()).append("\n");
        }

        // Liste des items
        sb.append("Items :\n");
        for (Item item : item_liste) {
            sb.append("  Item (").append(item.itemType).append(") : ");
            sb.append("Position : ").append(item.x).append(", ").append(item.y).append("\n");
        }

        // Informations sur l'état de la partie (gameOver)
        sb.append("État du jeu : ");
        if (gameContinue) {
            sb.append("En cours\n");
        } else {
            sb.append("Terminé\n");
        }

        return sb.toString();
    }

    public AgentAction direction_choisie(Snake snake) {
        int teteX = snake.position.get(0).getX();
        int teteY = snake.position.get(0).getY();

        int pommeX = 0;
        int pommeY = 0;

        for(Item item : this.item_liste){
            if (item.itemType == ItemType.APPLE){
                pommeX = item.x;
                pommeY = item.y;
            }
        }

        //actions possibles et leur distance de la pomme
        AgentAction[] actions = {AgentAction.MOVE_UP, AgentAction.MOVE_DOWN, AgentAction.MOVE_LEFT, AgentAction.MOVE_RIGHT};
        int minDistance = this.carte.getSize_x()*this.carte.getSize_y();
        AgentAction meilleure_action = snake.agentAction;//direction actuelle par défaut

        for (AgentAction action : actions) {
            int suivantX = teteX;
            int suivantY = teteY;

            //prochaine position
            if(!mur_dans_la_carte()){
                switch (action) {
                    case MOVE_UP:
                        suivantY -= 1;
                        if (suivantY < 0) suivantY = this.carte.getSize_y() - 1;
                        break;
                    case MOVE_DOWN:
                        suivantY += 1;
                        if (suivantY >= this.carte.getSize_y()) suivantY = 0;
                        break;
                    case MOVE_LEFT:
                        suivantX -= 1;
                        if (suivantX < 0) suivantX = this.carte.getSize_x() - 1;
                        break;
                    case MOVE_RIGHT:
                        suivantX += 1;
                        if (suivantX >= this.carte.getSize_x()) suivantX = 0;
                        break;
                }
            }
            else {
                switch (action) {
                    case MOVE_UP:
                        suivantY -= 1;
                        break;
                    case MOVE_DOWN:
                        suivantY += 1;
                        break;
                    case MOVE_LEFT:
                        suivantX -= 1;
                        break;
                    case MOVE_RIGHT:
                        suivantX += 1;
                        break;
                }
            }

            if(!snake.isLegalMove(action)) continue;

            if (this.carte.getWalls()[suivantX][suivantY]) {
                continue;
            }

            //collision avec autre snake plus long
            boolean autre_snake_Collision = false;
            for(Snake s : this.snake_liste){
                if((s.taille >= snake.taille)&&(s.couleur != snake.couleur)&&(!s.is_invincible)){
                    for (int i = 0; i < s.position.size()-1; i++) {//commence à 1 pour ignorer la tête
                        Position bodyPart = s.position.get(i);
                        if (bodyPart.getX() == suivantX && bodyPart.getY() == suivantY) {
                            autre_snake_Collision = true;
                            break;
                        }
                    }
                }
            }
            if (autre_snake_Collision) {
                continue;
            }



            //collision avec le corps
            boolean isBodyCollision = false;
            for (int i = 1; i < snake.position.size(); i++) {//commence à 1 pour ignorer la tête
                Position bodyPart = snake.position.get(i);
                if (bodyPart.getX() == suivantX && bodyPart.getY() == suivantY) {
                    isBodyCollision = true;
                    break;
                }
            }
            if (isBodyCollision) {
                continue;
            }

            int distance = this.carte.getSize_x()*this.carte.getSize_y();
            if(mur_dans_la_carte())
                distance = calculateDistance_avec_mur(suivantX, suivantY, pommeX, pommeY);
            else
                distance = calculateDistance_sans_mur(suivantX, suivantY, pommeX, pommeY);

            if ((distance <= minDistance) && 
                (!snake.is_sick || (distance > snake.tour_malade_restant || minDistance == this.carte.getSize_x()*this.carte.getSize_y()))) {
                boolean prochain_boule_jaune = false;
                for(Item item : this.item_liste){
                    if(item.itemType == ItemType.SICK_BALL){
                        if((suivantX == item.x)&&(suivantY == item.y)){
                            prochain_boule_jaune = true;
                        }
                    }
                }

                if(((!snake.is_sick)&&(20 > distance)&& prochain_boule_jaune)&&( minDistance != this.carte.getSize_x()*this.carte.getSize_y())){
                    //System.out.println("boule jaune ne pas aller");
                }
                else{
                    minDistance = distance;
                    meilleure_action = action;
                }
            }


        }


        snake.agentAction = meilleure_action;
        return meilleure_action;
    }


    public int calculateDistance_sans_mur(int x1, int y1, int x2, int y2) {
        int dx = Math.min(Math.abs(x2 - x1), this.carte.getSize_x() - Math.abs(x2 - x1));
        int dy = Math.min(Math.abs(y2 - y1), this.carte.getSize_y() - Math.abs(y2 - y1));
        return dx + dy;
    }

    public int calculateDistance_avec_mur(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }


    public boolean mur_dans_la_carte(){
        boolean[][] walls = this.carte.getWalls();
    
        if (walls == null) {
            return false;
        }

        for (int i = 0; i < walls.length; i++) {
            for (int j = 0; j < walls[i].length; j++) {
                if (walls[i][j]) {
                    return true;
                }
            }
        }

        return false;
    }

}
