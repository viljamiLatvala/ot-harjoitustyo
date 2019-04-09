/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csgotuc.domain;

import csgotuc.domain.Item;
import java.util.ArrayList;
import java.util.List;
import csgotuc.dao.ItemDao;

/**
 *
 * @author latvavil
 */
public class ItemService {
    private ItemDao itemDao;
    private List<Item> input;

    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
        this.input = new ArrayList<>();
    }

    public List<Item> getInput() {
        return input;
    }

    public void setInput(List<Item> input) {
        this.input = input;
    }
    
    public void addToInput(Item item) {
        if (this.input.size() >= 10) {
            throw new IllegalArgumentException("Maximum input size is 10!");
        } else if (this.input.size() > 0 && this.input.get(0).getGrade() != item.getGrade()) {
            throw new IllegalArgumentException("All items must be of same grade!");
        } else if (item.getGrade() > 4) {
            throw new IllegalArgumentException("Item grade must be below 6");
        } else {
            this.input.add(item);
        }
    }
    
    public List<Item> getAll() {
        return this.itemDao.getAll();
    }
    
    public List<Item> getByGrade(int grade) {
        return this.itemDao.getByGrade(grade);
    }
    
    public void setInputWithIds(int[] ids) {
        List<Item> newInput = new ArrayList<>();
        for (int id : ids) {
            newInput.add(itemDao.findById(id));
        }
        this.input = newInput;
    }
    
    public List<Item> calculateTradeUp() {
        List<Item> output = new ArrayList<>();
        for (Item item : this.input) {
            for (Item outputItem : itemDao.getChildren(item)) {
                output.add(outputItem);
            }
        }
        return output;
    }
}
