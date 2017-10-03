package com.martiproduction.drinkownia.CustomViews;

import android.content.Context;
import android.database.Cursor;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.martiproduction.drinkownia.R;

import java.io.IOException;


public class PickingImageAdapter extends RecyclerView.Adapter<PickingImageAdapter.PickingImageHolder> {

    public interface OnCLickListener{
        void getClickedPicture(Uri uri);
    }

    private Cursor mCursor;
    private Context mContext;
    private  RecyclerView mRecyclerView;
    private OnCLickListener mListener;

    class PickingImageHolder extends RecyclerView.ViewHolder {
        ImageView image;

         PickingImageHolder(View itemView) {
            super(itemView);
            image = (ImageView) itemView.findViewById(R.id.adapter_pickingImage_image);
        }
    }

    public PickingImageAdapter(OnCLickListener listener,RecyclerView recyclerView) {
       mListener = listener;
       mRecyclerView = recyclerView;
    }


    @Override
    public PickingImageHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_picking_image,parent,false);

        if(mListener != null && mContext != null) {
            view.setOnClickListener(l -> {
                mCursor.moveToPosition(mRecyclerView.getChildAdapterPosition(l));
                mListener.getClickedPicture(mCursor.getString(0));
            });
        }

        return new PickingImageHolder(view);
    }

    @Override
    public void onBindViewHolder(PickingImageHolder holder, int position) {
        RequestOptions options = new RequestOptions();

        mCursor.moveToPosition(position);

        options.override(512,384);
        options.centerCrop();


        try {
            ExifInterface exif = new ExifInterface(mCursor.getString(0));
            int imageWidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH,-1);
            int imageHeight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH,-1);

            if(imageWidth > imageHeight)
                options.override(384,512);

        } catch (IOException e) {
            e.printStackTrace();
        }


        Glide.with(mContext)
                .load(mCursor.getString(0))
                .apply(options)
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        if(mCursor != null)
            return mCursor.getCount();
        return 0;
    }

    public void changeCursor(Cursor cursor){
        Cursor oldCursor = swapCursor(cursor);

        if(oldCursor != null)
            oldCursor.close();
    }

    private Cursor swapCursor(Cursor cursor){
        if(cursor == mCursor)
            return null;
        Cursor oldCursor = mCursor;
        mCursor = cursor;

        if(cursor != null)
            this.notifyDataSetChanged();

        return oldCursor;
    }
}
