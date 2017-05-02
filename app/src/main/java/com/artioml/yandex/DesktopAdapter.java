package com.artioml.yandex;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.artioml.yandex.data.DatabaseDescription.*;
import com.artioml.yandex.data.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

class DesktopAdapter extends RecyclerView.Adapter<DesktopAdapter.ViewHolder> {

    private ArrayList<ResolveInfo> list, newApps, popularApps;
    private HashMap<String, ResolveInfo> appsMap;
    private Context context;
    private int cols, iconHeight;

    private final PackageManager pm;
    private SQLiteDatabase db;

    DesktopAdapter(Context context, int cols, int iconHeight) {
        this.context = context;
        this.cols = cols;
        this.iconHeight = iconHeight;
        setHasStableIds(true); // for animation

        pm = context.getPackageManager();
        db = new DatabaseHelper(context).getWritableDatabase();

        loadAppList();
    }

    void loadAppList() {
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);
        list = new ArrayList<>(context.getPackageManager().queryIntentActivities(launcher, 0));

        appsMap = new HashMap<>();
        for (ResolveInfo info : list)
            appsMap.put(info.activityInfo.packageName, info);

        Collections.sort(list, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo info1, ResolveInfo info2) {
                return info1.loadLabel(pm).toString().compareTo(info2.loadLabel(pm).toString());
            }
        });

        newApps = new ArrayList<>(list);
        Collections.sort(newApps, new Comparator<ResolveInfo>() {
            @Override
            public int compare(ResolveInfo info1, ResolveInfo info2) {
                try {
                    long time1 = pm.getPackageInfo(info1.activityInfo.packageName, 0).lastUpdateTime;
                    long time2 = pm.getPackageInfo(info2.activityInfo.packageName, 0).lastUpdateTime;
                    if (time1 > time2)
                        return -1;
                    else if (time1 == time2)
                        return 0;
                    else return 1;
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        loadPopularApps();
    }

    void loadPopularApps() {
        Cursor cursor = db.query(
                PopularApps.TABLE_NAME,
                new String[] {PopularApps.COLUMN_APP},
                null, null, null, null, PopularApps.COLUMN_COUNT + " DESC");

        popularApps = new ArrayList<>();
        String app;
        while (cursor.moveToNext()) {
            app = cursor.getString(cursor.getColumnIndex(PopularApps.COLUMN_APP));
            if (appsMap.containsKey(app))
                popularApps.add(appsMap.get(app));
            else
                db.delete(PopularApps.TABLE_NAME, PopularApps.COLUMN_APP + " = ? ", new String[] {app});
        }
        cursor.close();
        notifyDataSetChanged();
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
                iconImageView.setMaxHeight(iconHeight);

                iconImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String app = getResolveInfo(position).activityInfo.packageName;
                        context.startActivity(
                                pm.getLaunchIntentForPackage(app));

                        Cursor cursor = db.query(
                                PopularApps.TABLE_NAME,
                                new String[] {PopularApps.COLUMN_APP, PopularApps.COLUMN_COUNT},
                                PopularApps.COLUMN_APP + " = ? ",
                                new String[] {app}, null, null, null);
                        int size = cursor.getCount();

                        if (size == 0) {
                            ContentValues cv = new ContentValues();
                            cv.put(PopularApps.COLUMN_APP, app);
                            cv.put(PopularApps.COLUMN_COUNT, 1);
                            db.insert(PopularApps.TABLE_NAME, null, cv);
                        } else {
                            while (cursor.moveToNext()) {
                                int count = cursor.getInt(cursor.getColumnIndex(PopularApps.COLUMN_COUNT));
                                ContentValues cv = new ContentValues();
                                cv.put(PopularApps.COLUMN_COUNT, count + 1);
                                db.update(PopularApps.TABLE_NAME, cv,
                                        PopularApps.COLUMN_APP + " = ? ", new String[]{app});
                            }
                        }

                        cursor.close();

                        loadPopularApps();
                    }
                });

                iconImageView.setOnCreateContextMenuListener(menuListener);
            }
        }

        final View.OnCreateContextMenuListener menuListener = new View.OnCreateContextMenuListener() {

            @Override
            public void onCreateContextMenu(final ContextMenu contextMenu, View view,
                                            ContextMenu.ContextMenuInfo contextMenuInfo) {
                contextMenu.add(context.getResources().getString(R.string.menu_info))
                        .setOnMenuItemClickListener(
                        new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                ResolveInfo info = getResolveInfo(position);
                                Intent intent = new Intent(android
                                        .provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setData(Uri.parse("package:" + info.activityInfo.packageName));
                                context.startActivity(intent);
                                return true;
                            }
                        }
                );

                contextMenu.add(context.getResources().getString(R.string.menu_delete))
                        .setOnMenuItemClickListener(
                        new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                ResolveInfo info = getResolveInfo(position);
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:" + info.activityInfo.packageName));
                                ((MainActivity )context).startActivityForResult(intent, 1);
                                loadAppList();
                                return true;
                            }
                        }
                    );

                contextMenu.add(context.getResources().getString(R.string.menu_add_to_favorites))
                        .setOnMenuItemClickListener(
                        new MenuItem.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                String value = getResolveInfo(position).activityInfo.packageName;

                                Cursor cursor = db.query(
                                        Favorites.TABLE_NAME,
                                        new String[] {Favorites.COLUMN_APP},
                                        Favorites.COLUMN_APP + " = ? ",
                                        new String[] {value}, null, null, null);
                                int size = cursor.getCount();
                                cursor.close();
                                if (size > 0)
                                    return true;

                                ContentValues cv = new ContentValues();
                                cv.put(Favorites.COLUMN_APP, value);
                                db.insert(Favorites.TABLE_NAME, null, cv);
                                ((MainActivity) context).updateFavoriteApps();
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
        else {
            ResolveInfo info = getResolveInfo(position);
            holder.iconImageView.setImageDrawable(info.loadIcon(pm));
            holder.titleTextView.setText(info.loadLabel(pm));
        }
    }

    @Override
    public int getItemCount() {
        return list.size() + 2 * cols + 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 || position == cols + 1)
            return 1;
        return 0;
    }

     @Override
    public long getItemId(int position) {
         if (position == 0 || position == cols + 1)
             return 1;
         return getResolveInfo(position).hashCode();
    }

    private ResolveInfo getResolveInfo(int position) {
        if (position < cols + 1) {
            if (popularApps.size() > position - 1)
                return popularApps.get(position - 1);
            else return newApps.get(position - popularApps.size() - 1);
        } else if (position < 2 * cols + 2)
            return newApps.get(position - cols - 2);
        else
            return list.get(position - 2 * cols - 2);
    }


}