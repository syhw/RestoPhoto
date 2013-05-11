package com.example.restophoto;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import android.os.Bundle;
import android.provider.MediaStore;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

public class TakePic extends Activity {
    private static final int REQUEST_CODE = 1;
    private Bitmap bitmap;
    private ImageView imageView;

    
    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_take_pic);
      imageView = (ImageView) findViewById(R.id.previewPhoto);
    }

    public void pickImage(View View) {
      Intent intent = new Intent();
      intent.setType("image/*");
      intent.setAction(Intent.ACTION_GET_CONTENT);
      intent.addCategory(Intent.CATEGORY_OPENABLE);
      startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK)
        try {
          // We need to recyle unused bitmaps
          if (bitmap != null) {
            bitmap.recycle();
          }
          InputStream stream = getContentResolver().openInputStream(data.getData());
          bitmap = BitmapFactory.decodeStream(stream);
          stream.close();
          imageView.setImageBitmap(bitmap);
        } catch (FileNotFoundException e) {
          e.printStackTrace();
        } catch (IOException e) {
          e.printStackTrace();
        }
      super.onActivityResult(requestCode, resultCode, data);
    }
    
}
