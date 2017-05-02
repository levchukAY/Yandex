package com.artioml.yandex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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
import java.util.HashMap;
import java.util.List;

class FavoriteAppsAdapter extends RecyclerView.Adapter<FavoriteAppsAdapter.ViewHolder> {

    private ArrayList<String> favoriteApps;
    private HashMap<String, ResolveInfo> appsMap;
    private Context context;
    private int iconHeight;

    private final PackageManager pm;
    private SQLiteDatabase db;

    FavoriteAppsAdapter(Context context, int cols) {
        this.context = context;
        setHasStableIds(true); // for animation

        pm = context.getPackageManager();
        db = new DatabaseHelper(context).getWritableDatabase();

        DisplayMetrics metrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metrics);
        iconHeight = (metrics.widthPixels - 8 * metrics.densityDpi / 160) / cols
                - 16 * metrics.densityDpi / 160;

        loadInstalledApps();
        loadFavoriteApps();
    }

    void loadFavoriteApps() {
        Cursor cursor = db.query(
                Favorites.TABLE_NAME,
                new String[] {Favorites.COLUMN_APP},
                null, null, null, null, null);

        favoriteApps = new ArrayList<>();
        String app;
        while (cursor.moveToNext()) {
            app = cursor.getString(cursor.getColumnIndex(Favorites.COLUMN_APP));
            if (appsMap.containsKey(app))
                favoriteApps.add(app);
            else
                db.delete(Favorites.TABLE_NAME, Favorites.COLUMN_APP + " = ? ", new String[] {app});
        }
        cursor.close();
        notifyDataSetChanged();
    }

    void loadInstalledApps() {
        Intent launcher = new Intent(Intent.ACTION_MAIN);
        launcher.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> installedApps = pm.queryIntentActivities(launcher, 0);
        appsMap = new HashMap<>();
        for (ResolveInfo info : installedApps) {
            appsMap.put(info.activityInfo.packageName, info);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView titleTextView;
        ImageView iconImageView;
        int position;

        ViewHolder(View itemView) {
            super(itemView);

            titleTextView = (TextView) itemView.findViewById(R.id.titleTextView);
            iconImageView = (ImageView) itemView.findViewById(R.id.iconImageView);
            iconImageView.setMinimumHeight(iconHeight);

            iconImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ResolveInfo info = appsMap.get(favoriteApps.get(position));
                    context.startActivity(
                            pm.getLaunchIntentForPackage(info.activityInfo.packageName));

                }
            });

            iconImageView.setOnCreateContextMenuListener(menuListener);

        }

        private void setPosition(int position) {
            this.position = position;
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
                                        ResolveInfo info = appsMap.get(favoriteApps.get(position));
                                        Intent intent = new Intent(android
                                                .provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        intent.setData(Uri.parse("package:" + info.activityInfo.packageName));
                                        context.startActivity(intent);
                                        return true;
                                    }
                                }
                        );

                /*.add(context.getResources().getString(R.string.delete))
                        .setOnMenuItemClickListener(
                                new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        ResolveInfo info = list.get(position - 2 * cols - 2);
                                        Intent intent = new Intent(Intent.ACTION_DELETE);
                                        intent.setData(Uri.parse("package:" + info.activityInfo.packageName));
                                        context.startActivity(intent);
                                        //list.remove(info);
                                        loadAppList();
                                        notifyDataSetChanged();
                                        return true;
                                    }
                                }
                        );*/

                contextMenu.add(context.getResources().getString(R.string.menu_delete_from_favorites))
                        .setOnMenuItemClickListener(
                                new MenuItem.OnMenuItemClickListener() {
                                    @Override
                                    public boolean onMenuItemClick(MenuItem menuItem) {
                                        ResolveInfo info = appsMap.get(favoriteApps.get(position));
                                        db.delete(Favorites.TABLE_NAME, Favorites.COLUMN_APP + " = ? ",
                                                new String[] {info.activityInfo.packageName});
                                        loadFavoriteApps();
                                        return true;
                                    }
                                }
                        );
            }
        };

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.desktop_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.setPosition(position);
        ResolveInfo info = appsMap.get(favoriteApps.get(position));
        holder.iconImageView.setImageDrawable(info.loadIcon(pm));
        holder.titleTextView.setText(info.loadLabel(pm));
    }

    @Override
    public int getItemCount() {
        return favoriteApps.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return favoriteApps.get(position).hashCode();
    }

}