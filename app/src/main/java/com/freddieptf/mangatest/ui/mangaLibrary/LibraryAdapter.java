package com.freddieptf.mangatest.ui.mangaLibrary;

import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.freddieptf.mangatest.R;
import com.freddieptf.mangatest.data.model.MangaDetails;
import com.freddieptf.mangatest.utils.Utilities;

import java.util.ArrayList;


/**
 * Created by fred on 2/15/15.
 */
public class LibraryAdapter extends RecyclerView.Adapter<LibraryAdapter.LibraryViewHolder>
        implements View.OnClickListener, View.OnLongClickListener {

    final String TAG = getClass().getSimpleName();
    private ClickCallback myClickCallback;
    private ArrayList<MangaDetails> mangaDetailsList;

    public LibraryAdapter(ClickCallback clickCallback) {
        myClickCallback = clickCallback;
    }

    public void swapData(ArrayList<MangaDetails> mangaDetailsList) {
        if (this.mangaDetailsList == null) {
            this.mangaDetailsList = mangaDetailsList;
            notifyDataSetChanged();
        } else {
            LibraryListCallback callback = new LibraryListCallback(this.mangaDetailsList, mangaDetailsList);
            this.mangaDetailsList = mangaDetailsList;
            DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
            result.dispatchUpdatesTo(this);
        }

    }

    @Override
    public void onClick(View view) {
        myClickCallback.onItemClick(mangaDetailsList.get((Integer) view.getTag()).getName());
    }

    @Override
    public boolean onLongClick(View view) {
        int pos = (Integer) view.getTag();
        myClickCallback.onItemLongClick(mangaDetailsList.get(pos).getName(), pos);
        return true;
    }

    @Override
    public LibraryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new LibraryViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_my_manga_item, parent, false));
    }

    @Override
    public void onBindViewHolder(LibraryViewHolder holder, int position) {
        holder.bind(mangaDetailsList.get(position));
        holder.card.setOnClickListener(this);
        holder.card.setOnLongClickListener(this);
        holder.card.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mangaDetailsList == null ? 0 : mangaDetailsList.size();
    }

    interface ClickCallback {
        void onItemClick(String mangaName);

        void onItemLongClick(String mangaName, int position);
    }

    public static class LibraryViewHolder extends RecyclerView.ViewHolder {
        View view;
        CardView card;
        TextView mangaName, mangaAuthor, mangaStatus, mangaInfo, updateNum;
        ImageView imageView;
        RelativeLayout imageViewLayout;
        TableRow infoRow;
        String imagePath = "";

        public LibraryViewHolder(View view) {
            super(view);
            this.view = view;
            card = (CardView) view.findViewById(R.id.card);
            mangaName = (TextView) view.findViewById(R.id.tv_MyManga_name);
            mangaAuthor = (TextView) view.findViewById(R.id.tv_MyManga_author);
            mangaStatus = (TextView) view.findViewById(R.id.tv_MyManga_status);
            mangaInfo = (TextView) view.findViewById(R.id.tv_MyManga_info);
            updateNum = (TextView) view.findViewById(R.id.tv_mangaUpdates);
            imageView = (ImageView) view.findViewById(R.id.iv_myMangaImageView);
            imageViewLayout = (RelativeLayout) view.findViewById(R.id.iv_myMangaImageView_Layout);
            infoRow = (TableRow) view.findViewById(R.id.tr_MyManga_InfoRow);
        }

        public void bind(MangaDetails mangaDetails) {
            mangaName.setText(mangaDetails.getName());
            mangaAuthor.setText(mangaDetails.getAuthor());
            mangaStatus.setText(mangaDetails.getStatus());
            mangaInfo.setText(mangaDetails.getInfo());
            Glide.with(imageView.getContext()).load(mangaDetails.getCover()).into(imageView);
            this.imagePath = mangaDetails.getCover();
            toggleCompact();
            showUpdates(mangaDetails.getId());
        }

        private void toggleCompact() {
            if (!Utilities.compactCards(view.getContext())) {
                imageViewLayout.setVisibility(View.VISIBLE);
                infoRow.setMinimumHeight(view.getContext().getResources().getDimensionPixelSize(R.dimen.myNavBarMargin));
                Glide.with(imageView.getContext()).load(imagePath).into(imageView);
            } else {
                imageViewLayout.setVisibility(View.GONE);
                view.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                view.setMinimumHeight(view.getContext().getResources().getDimensionPixelSize(R.dimen.noDp));
                infoRow.setMinimumHeight(view.getContext().getResources().getDimensionPixelSize(R.dimen.noDp));
            }
        }

        private void showUpdates(String mangaId) {
            int updateMargin = Utilities.readMangaPageFromPrefs(updateNum.getContext(), mangaId);
            if (updateMargin != 0) {
                updateNum.setVisibility(View.VISIBLE);
                updateNum.setText("" + updateMargin);
            }
        }

    }


}
