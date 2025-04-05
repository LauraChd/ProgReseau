package snake.game;

import snake.utils.ItemType;

public class Item {
    int x;
	int y;
	ItemType itemType;

	public Item(){}

	public Item(int x, int y, ItemType itemType) {
		
		this.x = x;
		this.y = y;
		this.itemType = itemType;
	
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public ItemType getItemType() {
		return itemType;
	}

	public void setItemType(ItemType itemType) {
		this.itemType = itemType;
	}

	@Override
	public String toString(){
		return getItemType().toString() + " " + getX() + " " + getY();
	}

	
}
