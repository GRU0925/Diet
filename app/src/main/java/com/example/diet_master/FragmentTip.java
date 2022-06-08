package com.example.diet_master;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class FragmentTip extends Fragment {


    private static final String TAG = "FragmentTip";

    // rootView
    View rootView;

    // Storage
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();

    // View
    AddTipThumbnail addThumb;
    TextView tvItemTipName;
    ImageView ivItemTipThumb, ivTest;
    LinearLayout linearLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_tip, container, false);

        linearLayout = rootView.findViewById(R.id.Linear_root_thumbnail);
        //showTipThumb();

        //ivItemTipThumb = addThumb.findViewById(R.id.IV_thumbnail);

        /*
        ivItemTipThumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
         */
        return rootView;
    }

}

    /*
    // 썸네일 표시
    public void showTipThumb() {
        storageRef.child("Tip").listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                List<String> pr = new ArrayList<String>();
                for (StorageReference prefix : listResult.getPrefixes()) {
                    pr.add(String.valueOf(prefix));

                    storage.getReferenceFromUrl(storageRef + "Tip" + "/tip00" + pr.size() + "/tip00" + pr.size() + "_00.jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                        @Override
                        public void onSuccess(Uri uri) {
                            AddTipThumbnail item = new AddTipThumbnail(getContext());
                            ivItemTipThumb = item.findViewById(R.id.IV_thumbnail);
                            Glide.with(getContext()).load(uri).into(ivItemTipThumb);

                            linearLayout.addView(item);
                        }
                    });
                }

            }
        });
    }
}
*/
