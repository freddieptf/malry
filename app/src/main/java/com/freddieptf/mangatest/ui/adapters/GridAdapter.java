package com.freddieptf.mangatest.ui.adapters;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.freddieptf.mangatest.R;

/**
 * Created by fred on 6/7/15.
 */
public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> implements View.OnClickListener {

    String[] picUris;
    OnItemClick onItemClick;

    public GridAdapter(String[] picUris, OnItemClick onItemClick){
        this.picUris = picUris;
        this.onItemClick = onItemClick;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_grid_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.itemHolder.setTag(position);
        holder.itemHolder.setOnClickListener(this);
        new LoadStuff(holder, picUris[position], position).execute();
    }

    @Override
    public int getItemCount() {
        return picUris.length;
    }

    @Override
    public void onClick(View view) {
        onItemClick.onGridItemClick((int) view.getTag());
    }

    public interface OnItemClick {
        void onGridItemClick(int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;
        TextView textView;
        FrameLayout itemHolder;
        public ViewHolder(View view){
            super(view);
            itemHolder = (FrameLayout) view.findViewById(R.id.list_grid_item_holder);
            imageView = (ImageView) view.findViewById(R.id.staggered_recyclerView_item);
            textView = (TextView) view.findViewById(R.id.staggered_recyclerView_item2);
        }
    }

    public class LoadStuff extends AsyncTask<Void, Void, Bitmap>{
        ViewHolder viewHolder;
        String uri;
        int pos;
        public LoadStuff(ViewHolder viewHolder, String uri, int pos){
            this.viewHolder = viewHolder;
            this.uri = uri;
            this.pos = pos;
        }

        @Override
        protected Bitmap doInBackground(Void... voids) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 4;
            return BitmapFactory.decodeFile(uri, options);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.textView.setText("" + pos);
            viewHolder.textView.setVisibility(View.VISIBLE);
        }
    }

}
