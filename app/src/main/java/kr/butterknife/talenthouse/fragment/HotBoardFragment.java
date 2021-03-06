package kr.butterknife.talenthouse.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.butterknife.talenthouse.LoadingDialog;
import kr.butterknife.talenthouse.activity.MainActivity;
import kr.butterknife.talenthouse.adapter.MainRVAdapter;
import kr.butterknife.talenthouse.OnItemClickListener;
import kr.butterknife.talenthouse.PostItem;
import kr.butterknife.talenthouse.R;
import kr.butterknife.talenthouse.network.ButterKnifeApi;
import kr.butterknife.talenthouse.network.response.PostRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HotBoardFragment extends Fragment implements DatePickerDialog.OnDateSetListener {

    public static int year, month, day;


    private RecyclerView rv;
    private MainRVAdapter rvAdapter;
    private ArrayList<PostItem> posts;
    private String startDate;
    private String endDate;
    private TextView startDateTv;
    private TextView endDateTv;
    private Button submitBtn;
    private LinearLayoutManager linearLayoutManager;
    private int startEndFlag = 0;   // 0 : start, 1 : end

    public HotBoardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_hot_board, container, false);

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog startDatePickerDialog = new DatePickerDialog(
                getContext(), HotBoardFragment.this, year, month, day);

        DatePickerDialog endDatePickerDialog = new DatePickerDialog(
                getContext(), HotBoardFragment.this, year, month, day);
        startDateTv = (TextView) view.findViewById(R.id.hot_startDate_tv);
        endDateTv = (TextView) view.findViewById(R.id.hot_endDate_tv);
        submitBtn = (Button) view.findViewById(R.id.hot_submit_btn);

        startDateTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startEndFlag = 0;
                startDatePickerDialog.show();
            }
        });

        endDateTv.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startEndFlag = 1;
                endDatePickerDialog.show();
            }
        });

        rv = view.findViewById(R.id.hot_board_rv);
        posts = new ArrayList<>();

        rvAdapter = new MainRVAdapter(getContext(), posts);
        rvAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                ((MainActivity) getActivity()).replaceFragment(
                        new ContentFragment(posts.get(pos)),
                        "Content"
                );
            }
        });
        rv.setAdapter(rvAdapter);
        linearLayoutManager = new LinearLayoutManager(getContext());
        rv.setLayoutManager(linearLayoutManager);

        calendar.setTime(new Date());
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd");
        endDate = df.format(calendar.getTime());
        calendar.add(Calendar.DATE, -7);
        startDate = df.format(calendar.getTime());
        getHotPosts();

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDate = (String) startDateTv.getText();
                endDate = (String) endDateTv.getText();
                rvAdapter.setPage(0);
                posts.clear();
                rv.setAdapter(null);
                rv.setLayoutManager(null);
                rv.setLayoutManager(linearLayoutManager);
                rv.setAdapter(rvAdapter);
                getHotPosts();
                rvAdapter.notifyDataSetChanged();
            }
        });

        return view;
    }

    public void getHotPosts(){
        new Runnable(){
            @Override
            public void run(){
                try{
                    LoadingDialog.INSTANCE.onLoadingDialog(getActivity());
                    ButterKnifeApi.INSTANCE.getRetrofitService().getPostHotBoard(startDate,endDate).enqueue(new Callback<PostRes>() {
                        @Override
                        public void onResponse(Call<PostRes> call, Response<PostRes> response) {
                            if(response.body() != null){
                                try{
                                    int i = 0;
                                    List<PostItem> postList = response.body().getData();
                                    String prevCategory = postList.get(0).getCategory();
                                    for(PostItem p : postList){
                                        if(i == 3 || !prevCategory.equals(p.getCategory())) {
                                            i = 1;
                                            prevCategory = p.getCategory();
                                        }
                                        else
                                            i++;
                                        if(p.getVideoUrl() != null)
                                            posts.add(new PostItem(p.get_id(), p.getTitle(), p.getWriterNickname(), p.getWriterId(), p.getUpdateTime(), p.getDescription(), p.getVideoUrl(), p.getLikeCnt(), p.getLikeIDs(), p.getCategory(), p.getComments(), p.getProfile(), i));
                                        else if(p.getImageUrl().size() != 0)
                                            posts.add(new PostItem(p.get_id(), p.getTitle(), p.getWriterNickname(), p.getWriterId(), p.getUpdateTime(), p.getDescription(), p.getImageUrl(), p.getLikeCnt(), p.getLikeIDs(), p.getCategory(), p.getComments(), p.getProfile(), i));
                                        else
                                            posts.add(new PostItem(p.get_id(), p.getTitle(), p.getWriterNickname(), p.getWriterId(), p.getUpdateTime(), p.getDescription(), p.getLikeCnt(), p.getLikeIDs(), p.getCategory(), p.getComments(), p.getProfile()));
                                    }
                                    rvAdapter.notifyDataSetChanged();
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                            }
                            LoadingDialog.INSTANCE.offLoadingDialog();
                        }
                        @Override
                        public void onFailure(Call<PostRes> call, Throwable t) {
                            // ?????? ????????? ???????????? ????????? ?????? ??????
                            Log.d("err", "SERVER CONNECTION ERROR");
                            LoadingDialog.INSTANCE.offLoadingDialog();
                        }
                    });
                }catch (Exception e){
                    e.printStackTrace();
                    LoadingDialog.INSTANCE.offLoadingDialog();
                }
            }
        }.run();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if(startEndFlag == 0) {     // start
            startDateTv.setText(String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(dayOfMonth));
        }else if(startEndFlag == 1){     // end
            endDateTv.setText(String.valueOf(year) + "/" + String.valueOf(month+1) + "/" + String.valueOf(dayOfMonth));
        }
    }
}