package com.artioml.yandex;

import java.util.ArrayList;

/**
 * Created by Artiom L on 31.03.2017.
 */

class ClickList extends ArrayList<ClickList.Icon> {

    ClickList() { }

    ClickList (ArrayList<Integer> keys, ArrayList<Integer> values) {
        for (int i = 0; i < keys.size(); i++)
            add(new Icon(keys.get(i), values.get(i)));
    }

    void add(int id, int value) {
        add(new Icon(id, value));
    }

    void add(int id) {
        int index = -1;
        for (int i = 0; i < size(); i++)
            if (get(i).id == id)
                index = i;
        if (index == -1)
            add(id, 1);
        else {
            get(index).inc();
            int id_, count;
            for (int i = index - 1; i >= 0 && get(i).count <= get(i + 1).count; i--) {
                id_ = get(i + 1).id;
                count = get(i + 1).count;
                get(i + 1).set(get(i));
                get(i).set(id_, count);
            }
        }
    }

    void rem(int id) {
        for (int i = 0; i < size(); i++)
            if (get(i).id == id) {
                remove(i);
                return;
            }
    }

    class Icon implements Comparable<Icon> {

        protected int id, count;

        Icon (int id, int count) {
            this.id = id;
            this.count = count;
        }

        void set (int id, int count) {
            this.id = id;
            this.count = count;
        }

        void inc() {
            count++;
        }

        void set (Icon icon) {
            this.id = icon.id;
            this.count = icon.count;
        }

        @Override
        public int compareTo(Icon icon) {
            return icon.count - count;
        }

    }
}