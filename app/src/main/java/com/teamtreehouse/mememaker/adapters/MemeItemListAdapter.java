package com.teamtreehouse.mememaker.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.teamtreehouse.mememaker.R;
import com.teamtreehouse.mememaker.models.Meme;

import java.util.List;

/**
 * Created by Evan Anger on 7/28/14.
 */
public class MemeItemListAdapter extends BaseAdapter {

    private List<Meme> imageItems;
    private Context context;
    public MemeItemListAdapter(Context context, List<Meme> imageItems) {
        this.context = context;
        this.imageItems = imageItems;
    }

    @Override
    public int getCount() {
        return imageItems.size();
    }

    @Override
    public Object getItem(int i) {
        return imageItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return imageItems.indexOf(getItem(i));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_meme_item, null);
        }

        convertView.setBackgroundResource(R.drawable.meme_list_selector);
        ImageView imageView = (ImageView)convertView.findViewById(R.id.meme_image);
        TextView textView = (TextView)convertView.findViewById(R.id.meme_text);

        Meme imageItem = this.imageItems.get(position);
        imageView.setImageBitmap(imageItem.getBitmap());
        textView.setText(imageItem.getName());

        return convertView;
    }
}
