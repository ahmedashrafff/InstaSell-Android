package com.example.shade.instasell;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.support.constraint.Constraints.TAG;


public class SelectPhotoDialog extends DialogFragment {

    public interface OnPhotoSelectedListener{
        void getImagePath(Uri imagePath);
        void getImageBitmap(Bitmap bitmap);
    }

    OnPhotoSelectedListener mOnPhotoSelectedListener;


    @BindView(R.id.choosephotoDialog)
    Button choosePhoto;
    @BindView(R.id.opencameraDialog)
    Button openCamera;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.select_photo_dialog,container,false);
        ButterKnife.bind(this,view);

        choosePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1234);

            }
        });


        openCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 4321);
            }
        });


        return view;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1234 && resultCode== Activity.RESULT_OK)
        {
            Uri selectedImage = data.getData();
            mOnPhotoSelectedListener.getImagePath(selectedImage);
            getDialog().dismiss();

        }


        else if (requestCode==4321 && resultCode==Activity.RESULT_OK)
        {
            Bitmap bitmap;
            bitmap = (Bitmap) data.getExtras().get("data");
            Uri uri= data.getData();
            mOnPhotoSelectedListener.getImagePath(uri);
            mOnPhotoSelectedListener.getImageBitmap(bitmap);
            getDialog().dismiss();
        }

    }

    @Override
    public void onAttach(Context context) {
        try{
            mOnPhotoSelectedListener = (OnPhotoSelectedListener) getActivity();
        }catch (ClassCastException e){

        }
        super.onAttach(context);
    }

}
