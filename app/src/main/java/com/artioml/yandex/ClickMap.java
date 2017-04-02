package com.artioml.yandex;

import java.util.ArrayList;
import java.util.HashMap;


class ClickMap extends HashMap<Integer, Integer> {

    ClickMap() { }

    ClickMap(ArrayList<Integer> keys, ArrayList<Integer> vals) {
        for (int i = 0; i < keys.size(); i++)
            put(keys.get(i), vals.get(i));
    }

    void add(int id) {
        if (!containsKey(id))
            put(id, 0);
        int v = get(id);
        put(id, v + 1);
    }

    void add(int id, int count) {
        put(id, count);
    }

}