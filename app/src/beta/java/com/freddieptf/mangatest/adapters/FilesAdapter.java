package com.freddieptf.mangatest.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.DismissedItem;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemDismissedHelper;
import com.freddieptf.mangatest.recyclerviewdecor.swipestuff.ItemTouchHelperAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by fred on 4/30/15.
 */
public class FilesAdapter extends RecyclerView.Adapter<FilesAdapter.MyViewHolder>
        implements View.OnClickListener,
        ItemTouchHelperAdapter {

    List<File> fileList = new ArrayList<>();
    ArrayList<DismissedItem> dismissedItems = new ArrayList<>();
    boolean folderIcon;
    Context context;
    ClickListener clickListener;
    SwipeListener swipeToDelete;

    public FilesAdapter(Context context, File[] objects, Boolean folderIcon,
                        ClickListener clickListener, SwipeListener swipeToDelete) {
        fileList.addAll(Arrays.asList(objects));
        this.folderIcon = folderIcon;
        this.context = context;
        this.clickListener = clickListener;
        this.swipeToDelete = swipeToDelete;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_fromdir_item, parent, false);
        return new MyViewHolder(itemView);
    }


    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        File file = fileList.get(position);

        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(this);
        holder.title.setText(file.getName());

        if(folderIcon) holder.iconView.setImageResource(R.drawable.ic_folder_grey600_24dp);
        else holder.iconView.setImageResource(R.drawable.ic_collections_grey600_24dp);

        if(file.listFiles().length > 1) holder.contentSize.setText(file.listFiles().length + " items");
        else holder.contentSize.setText(file.listFiles().length + " item");

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    @Override
    public void onClick(View view) {
        clickListener.onClick((Integer) view.getTag());
    }

    @Override
    public void onItemDismised(final int pos) {
        final DismissedItem item = new DismissedItem();
        item.setFile(fileList.get(pos));
        item.setPos(pos);
        dismissedItems.add(item);

        swipeToDelete.onSwipeToDelete(fileList.get(pos), new ItemDismissedHelper() {
            @Override
            public void onUndoDismiss() {
                fileList.add(item.getPos(), item.getFile());
                notifyItemInserted(pos);
                Toast.makeText(context, "undo", Toast.LENGTH_SHORT).show();
            }
        });

        fileList.remove(pos);
        notifyItemRemoved(pos);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView title;
        TextView contentSize;
        ImageView iconView;
        LinearLayout itemView;

        public MyViewHolder(View view){
            super(view);
            itemView = (LinearLayout)view.findViewById(R.id.list_item);
            title = (TextView)view.findViewById(R.id.tv_title);
            contentSize = (TextView)view.findViewById(R.id.tv_contents);
            iconView = (ImageView)view.findViewById(R.id.iv_iconView);
        }
    }

    public interface ClickListener{
        void onClick(int index);
    }

    public interface SwipeListener {
        void onSwipeToDelete(File file, ItemDismissedHelper dismissedHelper);
    }


}
