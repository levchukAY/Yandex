package com.artioml.yandex;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import static android.content.ContentValues.TAG;

class DesktopAdapter extends RecyclerView.Adapter<DesktopAdapter.ViewHolder> {

    //private static int MOD;

    //private ArrayList<Integer> popular, newIcons, popularKeys, popularVals, deleted, icons;
    //private ClickList clicked;
    private List<ResolveInfo> list;
    private Context context;
    private int cols, iconHeight;

    private final PackageManager pm;

    DesktopAdapter(Context context, int cols, int iconHeight) {
        this.context = context;
        this.cols = cols;
        this.iconHeight = iconHeight;
        /*this.popularKeys = popularKeys;
        this.popularVals = popularVals;
        this.deleted = deleted;
        this.icons = icons;*/
        //MOD = icons.size();
        setHasStableIds(true); // for animation


        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        list = context.getPackageManager().queryIntentActivities(launcher, 0);

        pm = context.getPackageManager();

        Collections.sort(list, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo info1, ResolveInfo info2) {
                return info1.loadLabel(pm).toString().compareTo(info2.loadLabel(pm).toString());
            }
        });

        ApplicationInfo ai = pm.getInstalledApplications(PackageManager.GET_META_DATA).get(2);


        /*list = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Iterator<ApplicationInfo> it = list.iterator();
        while (it.hasNext()) {
            ApplicationInfo info= it.next();
            if ((info.flags & ApplicationInfo.FLAG_SYSTEM) != 0)
                it.remove();
        }

        Collections.sort(list, new Comparator<ApplicationInfo>() {
            @Override
            public int compare(ApplicationInfo info1, ApplicationInfo info2) {
                return info1.loadLabel(pm).toString().compareTo(info2.loadLabel(pm).toString());
            }
        });

        for (ApplicationInfo packageInfo : list) {
            Log.d(TAG, "Name :" + packageInfo.loadLabel(pm));
            Log.d(TAG, "Installed package :" + packageInfo.packageName);
            Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
            Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
        }*/

        /*clicked = new ClickList(popularKeys, popularVals);
        popular = getPopular();

        newIcons = new ArrayList<>();
        for (int i = 0; i < cols; i++)
            newIcons.add(getPosition(i));*/
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

                        if (position > 2 * cols + 1) {
                            ResolveInfo info = list.get(position - 2 * cols - 2);
                            context.startActivity(
                                    pm.getLaunchIntentForPackage(info.activityInfo.packageName));
                        }

                        //clicked.add(Integer.parseInt(title, 16) - 1);
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

                                if (position > 2 * cols + 1) {
                                    ResolveInfo info = list.get(position - 2 * cols - 2);
                                }

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
                                    ResolveInfo info = list.get(position - 2 * cols - 2);
                                    Intent intent = new Intent(Intent.ACTION_DELETE);
                                    intent.setData(Uri.parse("package:" + info.activityInfo.packageName));
                                    context.startActivity(intent);
                                    list.remove(info);
                                    notifyDataSetChanged();
                                    //Toast.makeText(context, info.activityInfo.packageName, Toast.LENGTH_SHORT).show();

                                    /*String title = titleTextView.getText().toString();
                                    int n = Integer.parseInt(title, 16) - 1;
                                    clicked.rem(n);
                                    deleted.add(n);
                                    Collections.sort(deleted);
                                    DesktopAdapter.this.notifyDataSetChanged();*/
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
            /*holder.titleTextView.setText(context.getResources().getString(
                    R.string.app_title, popular.get(position - 1) + 1));
            holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().
                    getIdentifier("ic_" + icons.get(popular.get(position - 1) % MOD), "drawable", context.getPackageName())));*/
        } else if (position < 2 * cols + 2) {
            /*position -= cols + 2;
            holder.titleTextView.setText(context.getResources().getString(
                    R.string.app_title, newIcons.get(position) + 1));
            holder.iconImageView.setImageDrawable(ContextCompat.getDrawable(context, context.getResources().
                    getIdentifier("ic_" + icons.get(newIcons.get(position) % MOD), "drawable", context.getPackageName())));*/
        } else {
            position -= 2 * cols + 2;
            ResolveInfo info = list.get(position);
            //ApplicationInfo info = list.get(position);
            //holder.titleTextView.setText(info.loadLabel(pm));
            //holder.iconImageView.setImageDrawable(info.loadIcon(pm));
            /*Context context = null;

                context = this.context.createPackageContext(info.packageName, 0);
                holder.iconImageView.setImageDrawable(context.getResources().getDrawable(info.getIconResource()));
                holder.titleTextView.setText(info.resolvePackageName);*/
            holder.iconImageView.setImageDrawable(info.loadIcon(pm));
            holder.titleTextView.setText(info.loadLabel(pm));
        }
    }

    @Override
    public int getItemCount() {
        //return Integer.MAX_VALUE;
        return list.size() + 2 * cols + 2;
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
             //position = getPosition(position);
             return position;
         }
         return 0;
    }

    /*private ArrayList<Integer> getPopular() {
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
    }*/

    /*private int getPosition(int position) {
        for (int i : deleted)
            if (position >= i)
                position++;
            else return position;
        return position;
    }*/

}