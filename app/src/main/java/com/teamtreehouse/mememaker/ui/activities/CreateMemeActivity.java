package com.teamtreehouse.mememaker.ui.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.teamtreehouse.mememaker.R;
import com.teamtreehouse.mememaker.database.MemeDataSource;
import com.teamtreehouse.mememaker.models.Meme;
import com.teamtreehouse.mememaker.models.MemeAnnotation;
import com.teamtreehouse.mememaker.ui.views.MemeImageView;

import java.util.ArrayList;

public class CreateMemeActivity extends Activity {

    public static final String EXTRA_IMAGE_FILE_PATH = "EXTRA_IMAGE_FILE_PATH";
    public static final String EXTRA_MEME_OBJECT = "EXTRA_MEME_OBJECT";

    private MemeImageView mMemeBitmapHolder;
    private FrameLayout mMemeContainer;
    private ArrayList<EditText> mMemeTexts;
    private String mImageFilePath;
    private String mCurrentColor;
    private Meme mCurrentMeme;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_meme);
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        mMemeTexts = new ArrayList<EditText>();
        mMemeContainer = (FrameLayout) findViewById(R.id.meme_container);
        mMemeBitmapHolder = (MemeImageView) findViewById(R.id.meme_bitmap_container);

        if(this.getIntent().hasExtra(EXTRA_IMAGE_FILE_PATH)) {
            mImageFilePath = this.getIntent().getStringExtra(EXTRA_IMAGE_FILE_PATH);
            mCurrentMeme = new Meme(-1, mImageFilePath, "", null);
        } else {
            mCurrentMeme = (Meme)this.getIntent().getSerializableExtra(EXTRA_MEME_OBJECT);
            mImageFilePath = mCurrentMeme.getAssetLocation();

            for(MemeAnnotation annotation : mCurrentMeme.getAnnotations()) {
                addEditTextOverImage(
                        annotation.getTitle(),
                        annotation.getLocationX(),
                        annotation.getLocationY(),
                        annotation.getColor());
            }
        }

        mMemeBitmapHolder.setImageBitmap(mCurrentMeme.getBitmap());
        mMemeBitmapHolder.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (MotionEvent.ACTION_UP == motionEvent.getAction()) {
                    int touchX = (int) motionEvent.getX();
                    int touchY = (int) motionEvent.getY();

                    addAnnotation(touchX, touchY, mCurrentColor);
                    addEditTextOverImage("Title", touchX, touchY, mCurrentColor);
                    return false;
                } else {
                    return true;
                }
            }
        });


    }

    private void addAnnotation(int touchX, int touchY, String mCurrentColor) {
        MemeAnnotation annotation = new MemeAnnotation();
        annotation.setColor(mCurrentColor);
        annotation.setLocationX(touchX);
        annotation.setLocationY(touchY);

        if(mCurrentMeme.getAnnotations() == null) {
            mCurrentMeme.setAnnotations(new ArrayList<MemeAnnotation>());
        }

        mCurrentMeme.getAnnotations().add(annotation);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_meme, menu);
        Spinner colorOptions = (Spinner) menu.findItem(R.id.choose_font_action).getActionView();
        colorOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mCurrentColor = getResources().getStringArray(R.array.fontColorValues)[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.save_action) {
            final EditText input = new EditText(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle("Name of meme?")
                    .setMessage("Please give this meme a name.")
                    .setView(input)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String memeName = input.getText().toString();
                            mCurrentMeme.setName(memeName);
                            saveMeme();
                            finish();
                        }
                    });
            builder.show();
            return true;
        } else if(id == android.R.id.home) {
            finish();
            return true;
        } else if(id == R.id.choose_font_action) {

        }
        return super.onOptionsItemSelected(item);
    }

    private void addEditTextOverImage(String title, int x, int y, String color) {
        ContextThemeWrapper newContext = new ContextThemeWrapper(this, R.style.holoLightLess);
        EditText editText = new EditText(this);
        editText.setText(title);
        editText.setBackground(null);
        editText.setTextColor(Color.parseColor(color));

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(x, y, 0, 0);
        editText.setLayoutParams(layoutParams);
        mMemeContainer.addView(editText);
        editText.requestFocus();
        mMemeTexts.add(editText);
    }

    private void saveMeme() {
        for (int i = 0; i < mMemeTexts.size(); i++) {
            EditText editText = mMemeTexts.get(i);
            MemeAnnotation annotation = mCurrentMeme.getAnnotations().get(i);
            annotation.setTitle(editText.getText().toString());
        }

        MemeDataSource dataSource = new MemeDataSource(this);
        if(mCurrentMeme.getId() != -1) {
            dataSource.update(mCurrentMeme);
        } else {
            dataSource.create(mCurrentMeme);
        }

    }
}
