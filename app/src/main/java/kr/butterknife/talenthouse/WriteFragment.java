package kr.butterknife.talenthouse;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.Arrays;
import java.util.List;

public class WriteFragment extends Fragment implements View.OnClickListener{

    private String category;

    private Spinner spinner;
    private ImageView imageView;
    private VideoView videoView;
    private Button btnUploadImage;
    private Button btnUploadVideo;
    private Intent intent;
    private Uri uri;

    HorizontalScrollView horizontalScrollView;
    LinearLayout linearLayout;

    public WriteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_write, container, false);

        spinner = view.findViewById(R.id.fw_spinner);

        videoView = view.findViewById(R.id.fw_vv);
        btnUploadImage = view.findViewById(R.id.fw_btn_uploadImage);
        btnUploadVideo = view.findViewById(R.id.fw_btn_uploadVideo);


        btnUploadImage.setOnClickListener(this);
        btnUploadVideo.setOnClickListener(this);

        linearLayout = view.findViewById(R.id.fw_ll_image);
        horizontalScrollView = view.findViewById(R.id.fw_hsv);


        List<String> spinner_items = Arrays.asList(getResources().getStringArray(R.array.category_spinner));
        // 스피커와 리스트를 연결하기 위해 사용되는 어댑터
        ArrayAdapter<String> spinner_adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, spinner_items);
        spinner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // 스피너의 어댑터 지정
        spinner.setAdapter(spinner_adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = parent.getItemAtPosition(position).toString();
                if(str.equals("카테고리") == false){
                    category = str;
                    Log.d("setCater", category);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.fw_btn_uploadImage:
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, 10);

//                intent = new Intent(Intent.ACTION_PICK);
//                intent.setType("image/*");
//                startActivityForResult(intent, 10);
                break;
            case R.id.fw_btn_uploadVideo:
                intent = new Intent(Intent.ACTION_PICK);
                intent.setType("video/*");
                startActivityForResult(intent, 20);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case 10:
                    if(resultCode == -1){
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(100, 100);
                        layoutParams.rightMargin = 5;
                        layoutParams.gravity = Gravity.CENTER;

                        for(int i=0 ; i<data.getClipData().getItemCount() ; i++){
                            ImageView tempImage = new ImageView(getContext());
                            tempImage.setImageURI(data.getClipData().getItemAt(i).getUri());
                            tempImage.setLayoutParams(layoutParams);
                            tempImage.setScaleType(ImageView.ScaleType.FIT_XY);
                            linearLayout.addView(tempImage);
                        }
                }else{
                    Toast.makeText(getContext(), "이미지를 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
            case 20:
                if(resultCode == -1){
                    // 선택한 사진의 경로(Uri) 객체 얻어오기
                    uri = data.getData();
                    if(uri != null) {
                        MediaController mc = new MediaController(getContext());
                        videoView.setMediaController(mc);
                        videoView.setVideoURI(uri);
                        videoView.setBackground(null);
                        videoView.requestFocus();
                        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                videoView.start();
                                videoView.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        videoView.pause();
                                    }
                                }, 100);
                            }
                        });
                    }
                }else{
                    Toast.makeText(getContext(), "동영상을 선택하지 않았습니다.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}