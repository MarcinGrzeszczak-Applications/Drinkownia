package com.martiproduction.drinkownia.Fragments;

import android.app.Fragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.martiproduction.drinkownia.CustomViews.PickingImageAdapter;
import com.martiproduction.drinkownia.R;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PickingImage extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>,PickingImageAdapter.OnCLickListener{

    public interface Listener {
        void buttonClicked();
        void close(Bitmap picture);
    }

    private Unbinder bind;
    private boolean mReadPermission;
    private PickingImageAdapter mPickingImageAdapter;
    private Listener mListener;

    @BindView(R.id.pickingImage_recyclerView)
    RecyclerView mPickingRecycler;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_picking_image,container,false);
        bind = ButterKnife.bind(this,view);

        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3,StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);

        mPickingRecycler.setLayoutManager(layoutManager);

        if(mReadPermission) {
             mPickingImageAdapter = new PickingImageAdapter(this,mPickingRecycler);
             mPickingRecycler.setAdapter(mPickingImageAdapter);
             getLoaderManager().initLoader(0,null,this);
        }
        return view;
    }

    public void readPermissions(boolean premission){
        mReadPermission = premission;
    }
    public void setListener (Listener listener) {mListener = listener;}

    @Override
    public void onDestroy() {
        bind.unbind();
        super.onDestroy();
    }



    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = { MediaStore.Images.Media.DATA};
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        return new CursorLoader(getActivity(),uri,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        mPickingImageAdapter.changeCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPickingImageAdapter.changeCursor(null);
    }


    @Override
    public void getClickedPicture(Uri uri) {

        try {
            Bitmap bitmapPicture = MediaStore.Images.Media.getBitmap(PickingImage.this.getActivity().getContentResolver(),Uri.parse(uri));
            mListener.close(bitmapPicture);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
