package snake.game;

import snake.utils.ItemType;

public class Item {
    int x;
	int y;
	ItemType itemType;

	public Item(int x, int y, ItemType itemType) {
		
		this.x = x;
		this.y = y;
		this.itemType = itemType;
	
	}
}
