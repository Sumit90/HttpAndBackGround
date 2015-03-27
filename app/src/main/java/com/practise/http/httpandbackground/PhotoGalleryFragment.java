package com.practise.http.httpandbackground;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by e00959 on 3/27/2015.
 */
public class PhotoGalleryFragment extends Fragment{

    private GridView mGridView;
    private static final String TAG = "PhotoGalleryFragment";
    ArrayList<GalleryItem> mItems;
    ProgressDialog mDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new FetchItemsTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View inflatedView=inflater.inflate(R.layout.activity_photo_gallery,container,false);
        mGridView=(GridView)inflatedView.findViewById(R.id.myGridView);

        return inflatedView;
    }

    private class FetchItemsTask extends AsyncTask<Void,Void,ArrayList<GalleryItem>>
    {
        @Override
        protected void onPreExecute() {
            mDialog=ProgressDialog.show(getActivity(),"Downloading","Please wait....");
            mDialog.setCancelable(false);
            mDialog.show();

        }

        @Override
        protected ArrayList<GalleryItem> doInBackground(Void... params) {

            try{

                return new FlickrFetchr().fetchItems();

            }catch(IOException ex)
            {
                Log.i(TAG,"IOException Occured");
                ex.printStackTrace();
            }
            catch (XmlPullParserException ex)
            {
                Log.i(TAG,"XmlPullParserException Occured");
                ex.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onPostExecute(ArrayList<GalleryItem> items) {
            mItems=items;
            if(mDialog!=null)
            {
                mDialog.dismiss();
            }
            setUpAdapter();

        }
    }

    void setUpAdapter()
    {
        if(getActivity()==null || mItems==null) return;

        if(mItems!=null)
        {
            mGridView.setAdapter(new ArrayAdapter<GalleryItem>(getActivity(),android.R.layout.simple_gallery_item,mItems));
        }
        else
        {
            mGridView.setAdapter(null);
        }
    }

}
