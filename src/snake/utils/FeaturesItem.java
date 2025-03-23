package snake.utils;

import com.fasterxml.jackson.annotation.JsonValue;

public class FeaturesItem {

	private int x;
	private int y;
	private ItemType itemType;
	
	public FeaturesItem(){}

	public FeaturesItem(int x, int y, ItemType itemType) {
		
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
    public String toString() {
        return itemType + "(" + x + "," + y + ")";
    }

    public String getItemTypeString() {
        return itemType.name(); 
    }
	
	
}
