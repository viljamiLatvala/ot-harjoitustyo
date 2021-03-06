/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package csgotuc.domain;

import csgotuc.dao.ItemDao;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import java.util.Collections;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 *
 * ItemService forms Trade Up-contract calculations based on its input.
 */
public class ItemService {

    private ItemDao itemDao;
    private SortedMap<Integer, Item> inputMap;
    private List<Integer> freeSlots;

    /**
     *
     * @param itemDao   DAO-object to be used by the service to obtain Item objects.
     */
    public ItemService(ItemDao itemDao) {
        this.itemDao = itemDao;
        this.inputMap = new TreeMap<>();
        this.freeSlots = IntStream.rangeClosed(0, 9).boxed().collect(Collectors.toList());
    }
    
    /**
     * 
     * @return List of Item-objects stored as input by the ItemService-class
     */
    public List<Item> getInput() {
        return new ArrayList<>(this.inputMap.values());
    }

    /**
     * 
     * @return List of objects that can be used as input items for the Trade Up Contract.
     */
    public List<Item> getPossibleInputs() {
        return this.itemDao.getPossibleInputs();
    }

    /**
     * Method is used to match visualizations of input-slots in the UI to locations in the actual InputMap.
     * @param key   The slot location of the input item.
     * @return Item stored with the given key.
     */
    public Item getInputItem(int key) {
        return this.inputMap.get(key);
    }

    /**
     * Removes item from inputMap. Correspondingly frees the slot for future
     * inputs.
     *
     * @param slot Input slot number i.e. inputMap key.
     */
    public void removeFromInput(int slot) {
        this.inputMap.remove(slot);
        this.freeSlots.add(slot);
        Collections.sort(this.freeSlots);
    }

    /**
     * Adds item given as a parameter to input
     *
     * @param item  Item to add to input.
     */
    public void addToInput(Item item) {
        if (this.inputMap.size() >= 10) {
            throw new IllegalArgumentException("Maximum input size is 10!");
        } else {
            this.inputMap.put(this.freeSlots.get(0), item);
            this.freeSlots.remove(0);
            Collections.sort(this.freeSlots);
        }

    }

    /**
     * 
     * @param grade Desired grade of the items returned
     * @return A list of items with the provided grade
     */
    public List<Item> getByGrade(int grade) {
        return this.itemDao.getByGrade(grade);
    }

    /**
     * 
     * @param name  name of the item
     * @return Item object from the database with desired name.
     */
    public Item findByName(String name) {
        return (Item) this.itemDao.findByName(name);
    }

    /**
     * Method for calculating possible returns of a Trade Up-contract.
     *
     * @return list of possible returns on a Trade Up-contract with given input.
     * Number of occurrences of an Item corresponds to the chance that it would
     * be received as output from the Trade Up.
     */
    public List<Item> calculateTradeUp() {
        double floatSum = 0;
        List<Item> output = new ArrayList<>();

        for (Item inputItem : this.inputMap.values()) {
            floatSum += inputItem.getFloatValue();
            for (Item outputItem : (List<Item>) itemDao.getChildren(inputItem)) {
                output.add(outputItem);
            }
        }

        double floatAvg = floatSum / inputMap.values().size();

        output.forEach((outputItem) -> {
            double maxWear = outputItem.getMaxWear();
            double minWear = outputItem.getMinWear();
            double finalFloat = (maxWear - minWear) * floatAvg + minWear;
            outputItem.setFloatValue(finalFloat);
        });
        return output;
    }
}
