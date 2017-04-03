package com.artioml.yandex;

import android.support.annotation.NonNull;

import java.util.ArrayList;

class ClickList extends ArrayList<ClickList.Icon> {

    ClickList (ArrayList<Integer> keys, ArrayList<Integer> values) {
        for (int i = 0; i < keys.size(); i++)
            add(new Icon(keys.get(i), values.get(i)));
    }

    private void add(int id, int value) {
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
                get(i + 1).set(get(i).id, get(i).count);
                get(i).set(id_, count);
            }
        }
    }

    void rem (int id) {
        for (int i = 0; i < size(); i++)
            if (get(i).id == id) {
                remove(i);
                return;
            }
    }

    class Icon implements Comparable<Icon> {

        int id, count;

        Icon (int id, int count) {
            this.id = id;
            this.count = count;
        }

        void set(int id, int count) {
            this.id = id;
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