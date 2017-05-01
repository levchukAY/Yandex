package com.artioml.yandex;

import android.support.annotation.NonNull;

import java.util.ArrayList;

class ClickList extends ArrayList<ClickList.Icon> {

    /*ClickList (ArrayList<Integer> keys, ArrayList<Integer> values) {
        for (int i = 0; i < keys.size(); i++)
            add(new Icon(keys.get(i), values.get(i)));
    }*/

    private void add(String name, int value) {
        add(new Icon(name, value));
    }

    void add(String name) {
        int index = -1;
        for (int i = 0; i < size(); i++)
            if (get(i).name.equals(name))
                index = i;
        if (index == -1) {
            index = size();
            add(name, 0);
        }
        get(index).inc();

        String name_;
        int count;
        for (int i = index - 1; i >= 0 && get(i).count <= get(i + 1).count; i--) {
            name_ = get(i + 1).name;
            count = get(i + 1).count;
            get(i + 1).set(get(i).name, get(i).count);
            get(i).set(name_, count);
        }
    }

    void rem (String name) {
        for (int i = 0; i < size(); i++)
            if (get(i).name.equals(name)) {
                remove(i);
                return;
            }
    }

    class Icon implements Comparable<Icon> {

        String name;
        int count;

        Icon (String name, int count) {
            this.name = name;
            this.count = count;
        }

        void set(String name, int count) {
            this.name = name;
            this.count = count;
        }

        void inc() {
            count++;
        }

        @Override
        public int compareTo(@NonNull Icon icon) {
            return icon.count - count;
        }

    }
}