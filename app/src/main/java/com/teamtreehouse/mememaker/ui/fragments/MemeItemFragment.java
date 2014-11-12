package com.teamtreehouse.mememaker.ui.fragments;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.teamtreehouse.mememaker.R;
import com.teamtreehouse.mememaker.adapters.MemeItemListAdapter;
import com.teamtreehouse.mememaker.database.MemeDataSource;
import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;
import com.teamtreehouse.mememaker.ui.activities.CreateMemeActivity;
import com.teamtreehouse.mememaker.ui.activities.MemeSettingsActivity;
import com.teamtreehouse.mememaker.utils.FileUtilities;

import java.util.ArrayList;


public class MemeItemFragment extends ListFragment {

    private Menu mMenu;
    private int mSelectedItem;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setSelected(true);
                mSelectedItem = i;
                mMenu.findItem(R.id.share_action).setVisible(true);
                mMenu.findItem(R.id.edit_action).setVisible(true);
            }
        });
        this.getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                Meme meme = (Meme) getListAdapter().getItem(i);
                final int memeId = meme.getId();
                AlertDialog.Builder builder = new AlertDialog.Builder(MemeItemFragment.this.getActivity())
                        .setMessage(getString(R.string.dialog_message_delete_meme))
                        .setTitle(getString(R.string.dialog_confirm))
                        .setNegativeButton(getString(android.R.string.no), null)
                        .setPositiveButton(getString(android.R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Toast.makeText(MemeItemFragment.this.getActivity(), "Should delete", Toast.LENGTH_LONG).show();
                                        MemeDataSource dataSource = new MemeDataSource(MemeItemFragment.this.getActivity());
                                        dataSource.delete(memeId);
                                        refreshMemes();
                                        mMenu.findItem(R.id.share_action).setVisible(true);
                                        mMenu.findItem(R.id.edit_action).setVisible(true);

                                    }
                                });

                builder.create().show();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        refreshMemes();
    }

    private void refreshMemes() {
        MemeDataSource dataSource = new MemeDataSource(this.getActivity());
        ArrayList<Meme> memes = dataSource.read();
        setListAdapter(new MemeItemListAdapter(getActivity(), memes));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.setHasOptionsMenu(true);

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.meme_list, menu);
        mMenu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.settings_action) {
            Intent intent = new Intent(this.getActivity(), MemeSettingsActivity.class);
            startActivity(intent);
        } else if(item.getItemId() == R.id.share_action) {
            //need to build the image here.
            Meme meme = (Meme) getListAdapter().getItem(mSelectedItem);
            Bitmap bitmap = createMeme(meme);
            Uri uriForShare = FileUtilities.saveImageForSharing(this.getActivity(), bitmap, "new_meme.jpg");

            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_STREAM, uriForShare);
            shareIntent.setType("image/jpeg");
            startActivity(Intent.createChooser(shareIntent, getResources().getText(R.string.send_to)));
        } else if(item.getItemId() == R.id.edit_action) {
            Toast.makeText(this.getActivity(), "Into edit image now", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this.getActivity(), CreateMemeActivity.class);
            Meme meme = (Meme) getListAdapter().getItem(mSelectedItem);
            intent.putExtra(CreateMemeActivity.EXTRA_MEME_OBJECT, meme);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private Bitmap createMeme(Meme meme) {
        Bitmap bitmap = BitmapFactory.decodeFile(meme.getAssetLocation());
        Bitmap workingBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        float scale = getActivity().getResources().getDisplayMetrics().density;
        Canvas canvas = new Canvas(workingBitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        for(MemeAnnotation annotation : meme.getAnnotations()) {
            paint.setColor(Color.parseColor(annotation.getColor()));
            paint.setTextSize(12 * scale);

            Rect bounds = new Rect();
            String text = annotation.getTitle();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int x = annotation.getLocationX();
            int y = annotation.getLocationY();

            canvas.drawText(text, x, y, paint);
        }

        return workingBitmap;
    }
}
