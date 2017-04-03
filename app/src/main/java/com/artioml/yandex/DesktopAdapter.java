package com.artioml.yandex;

import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

class DesktopAdapter extends RecyclerView.Adapter<DesktopAdapter.ViewHolder> {

    private static int MOD;

    private ArrayList<Integer> popular, newIcons, popularKeys, popularVals, deleted, icons;
    private ClickList clicked;
    private Context context;
    private int cols, iconHeight;

    DesktopAdapter(Context context, ArrayList<Integer> popularKeys, ArrayList<Integer> popularVals,
                   ArrayList<Integer> deleted, ArrayList<Integer> icons, int cols, int iconHeight) {
        this.context = context;
        this.cols = cols;
        this.iconHeight = iconHeight;
        this.popularKeys = popularKeys;
        this.popularVals = popularVals;
        this.deleted = deleted;
        this.icons = icons;
        MOD = icons.size();
        setHasStableIds(true); // for animation

        clicked = new ClickList(popularKeys, popularVals);
        popular = getPopular();

        newIcons = new ArrayList<>();
        for (int i = 0; i < cols; i++)
            newIcons.add(getPosition(i));
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView iconImageView;
        int position;

        ViewHolder(View itemView, int viewType) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);

            if (viewType == 0) {
                iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
                if (Build.VERSION.SDK_INT >= 21)
                    iconImageView.setClipToOutline(true);
                iconImageView.setMinimumHeight(iconHeight);

                iconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String title = titleTextView.getText().toString();
                        Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
                        toast.show();
                        clicked.add(Integer.parseInt(title, 16) - 1);
                    }
                });

                iconImageView.setOnCreateContextMenuListener(menuListener);
            }
        }

        final View.OnCreateContextMenuListener menuListener = new View.OnCreateContextMenuListener() {
            @Override
            public void onCreateContextMenu(ContextMenu contextMenu, View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(context.getResources().getString(
                        R.string.info)).setOnMenuItemClickListener(
                        new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                String title = titleTextView.getText().toString();
                                Toast toast = Toast.makeText(context, title, Toast.LENGTH_SHORT);
                                toast.show();
                                return true;
                            }
                        }
                );
                if (position > 2 * cols + 1)
                    contextMenu.add(context.getResources().getString(
                            R.string.delete)).setOnMenuItemClickListener(
                            new MenuItem.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    String title = titleTextView.getText().toString();
                                    int n = Integer.parseInt(title, 16) - 1;
                                    clicked.rem(n);
                                    deleted.add(n);
                                    Collections.sort(deleted);
                                    DesktopAdapter.this.notifyDataSetChanged();
                                    return true;
                                }
                            }
                    );
            }
        };

        private void setPosition(int position) {
            this.position = position;
        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).
                    inflate(R.layout.desktop_item, parent, false);
            view.getWidth();
        }
        else view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.desktop_fill_item, parent, false);
        return new ViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.setPosition(position);

        if (position == 0)
            holder.titleTextView.setText(context.getResources().getString(R.string.popular));
        else if (position == cols + 1)
            holder.titleTextView.setText(context.getResources().getString(R.string.new_apps));
        else if (position <= cols) {
            holder.titleTextView.setText(context.getResources().getString(
                    R.string.app_title, popular.get(position - 1) + 1));
            holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().
                    getIdentifier("ic_" + icons.get(popular.get(position - 1) % MOD), "drawable", context.getPackageName())));
        } else if (position < 2 * cols + 2) {
            position -= cols + 2;
            holder.titleTextView.setText(context.getResources().getString(
                    R.string.app_title, newIcons.get(position) + 1));
            holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().
                    getIdentifier("ic_" + icons.get(newIcons.get(position) % MOD), "drawable", context.getPackageName())));
        } else {
            position -= 2 * cols + 2;
            position = getPosition(position);
            holder.titleTextView.setText(context.getResources().getString(
                    R.string.app_title, position + 1));
            holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().
                    getIdentifier("ic_" + icons.get(position % MOD), "drawable", context.getPackageName())));
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == cols + 1)
            return 1;
        return 0;
    }

     @Override
    public long getItemId(int position) { // for animation
         if (position > 2 * cols + 1) {
             position -= 2 * cols + 2;
             position = getPosition(position);
             return position;
         }
         return 0;
    }

    private ArrayList<Integer> getPopular() {
        ArrayList<Integer> res = new ArrayList<>();
        for (int i = 0; res.size() != cols && i < clicked.size(); i++)
            res.add(clicked.get(i).id);
        int n;
        for (int i = 0; res.size() != cols; i++) {
            n = getPosition(i);
            if (!res.contains(n))
                res.add(n);
        }
        return res;
    }

    void savePopular() {
        popularKeys.clear();
        popularVals.clear();
        for (ClickList.Icon i : clicked) {
            popularKeys.add(i.id);
            popularVals.add(i.count);
        }
    }

    private int getPosition(int position) {
        for (int i : deleted)
            if (position >= i)
                position++;
            else return position;
        return position;
    }

}

// локализация

// темная тема
// добавить delete во все случаи (не надо)
// убрать обновление для new
//+ бесконечный скроллинг? (integer maxsize)
//+ можно ли назад передвигаться между фрагментами и активити? (только по кнопке) (finish in intent)
//+ языки - нет цифр? (фарси)
//+ можно ли с нуля нумеровать (только с 1)
//+ как отличить сворачивание от поворота? (считаем, что одно и то же)
//+ цвета в темах? (установить textAppearance)


// Как сделать некриво?
// закругленные иконки
//+ как запоминать популярные? (Shared)
// числа в 16сс в персидском
// как определить RTL для версии ниже 17?
// + сменить перелистывания фрагментов для RTL?
//+ как сделать квадратные ячейки? (custom view)