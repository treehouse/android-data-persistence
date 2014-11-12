package com.teamtreehouse.mememaker.ui.fragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.teamtreehouse.mememaker.R;
import com.teamtreehouse.mememaker.adapters.GridViewAdapter;
import com.teamtreehouse.mememaker.models.ImageGridItem;
import com.teamtreehouse.mememaker.ui.activities.CreateMemeActivity;
import com.teamtreehouse.mememaker.ui.activities.MemeSettingsActivity;
import com.teamtreehouse.mememaker.utils.FileUtilities;

import java.io.File;
import java.util.ArrayList;


public class ImageGridFragment extends Fragment {

    private GridView mGridView;
    private GridViewAdapter mGridAdapter;
    public static int RESULT_LOAD_IMAGE = 1000;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_image_grid, container, false);
        mGridView = (GridView) rootView.findViewById(R.id.gridView);
        mGridAdapter = new GridViewAdapter(this.getActivity(), R.layout.view_grid, extractFiles());
        mGridView.setAdapter(mGridAdapter);
        mGridView.setOnItemClickListener(mOnItemClickListener);
        mGridView.setOnItemLongClickListener(mOnItemLongClickListener);
        this.setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.image_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.import_action) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, RESULT_LOAD_IMAGE);
        } else {
            if (item.getItemId() == R.id.settings_action) {
                Intent intent = new Intent(this.getActivity(), MemeSettingsActivity.class);
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private ArrayList extractFiles() {
        final ArrayList imageItems = new ArrayList();
        File [] filteredFiles = FileUtilities.listFiles(this.getActivity());
        for(File filteredFile : filteredFiles) {
            Bitmap bitmap = BitmapFactory.decodeFile(filteredFile.getAbsolutePath());
            ImageGridItem item = new ImageGridItem(bitmap, filteredFile.getName(), filteredFile.getAbsolutePath());
            imageItems.add(item);
        }
        return imageItems;
    }

    private void resetGridAdapter() {
        mGridAdapter = new GridViewAdapter(this.getActivity(), R.layout.view_grid, extractFiles());
        mGridView.setAdapter(mGridAdapter);
    }

    protected AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            ImageGridItem imageGridItem = (ImageGridItem) adapterView.getAdapter().getItem(i);
            Intent intent = new Intent(ImageGridFragment.this.getActivity(), CreateMemeActivity.class);
            intent.putExtra(CreateMemeActivity.EXTRA_IMAGE_FILE_PATH, imageGridItem.getFullPath());
            Log.d("FILE:", imageGridItem.getFullPath());
            ImageGridFragment.this.getActivity().startActivity(intent);
        }
    };

    protected AdapterView.OnItemLongClickListener mOnItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
            final ImageGridItem imageGridItem = (ImageGridItem) adapterView.getAdapter().getItem(i);

            AlertDialog.Builder builder = new AlertDialog.Builder(ImageGridFragment.this.getActivity());
            builder.setTitle(R.string.dialog_confirm)
                    .setMessage(R.string.dialog_message_delete_file)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            File fileToDelete = new File(imageGridItem.getFullPath());
                            boolean deleted = fileToDelete.delete();
                            if (deleted) {
                                ImageGridFragment.this.resetGridAdapter();
                            }
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    });
            builder.create().show();
            return true;
        }
    };
}
