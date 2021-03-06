package kr.butterknife.talenthouse.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import kr.butterknife.talenthouse.CommentItem;
import kr.butterknife.talenthouse.LoginInfo;
import kr.butterknife.talenthouse.R;
import kr.butterknife.talenthouse.Util;
import kr.butterknife.talenthouse.activity.MainActivity;
import kr.butterknife.talenthouse.network.ButterKnifeApi;
import kr.butterknife.talenthouse.network.request.UpdateCommentReq;
import kr.butterknife.talenthouse.network.response.CommonResponse;
import kr.butterknife.talenthouse.network.response.UpdateCommentRes;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommentRVAdapter extends RecyclerView.Adapter<CommentRVAdapter.CommentRVHolder> {
    private ArrayList<CommentItem> comments;
    Context context;

    public CommentRVAdapter(ArrayList<CommentItem> comments) {
        this.comments = comments;
    }

    static class CommentRVHolder extends RecyclerView.ViewHolder {
        TextView writer, date, comment, commentModify, modifyCancel;
        ImageButton btnMenu;
        EditText editComment;
        LinearLayout modifyLayout;
        String dateText;
        Context mainContext;
        ImageView profile;

        public CommentRVHolder(@NonNull View itemView, Context context) {
            super(itemView);
            writer = itemView.findViewById(R.id.rvcomment_tv_writer);
            date = itemView.findViewById(R.id.rvcomment_tv_date);
            comment = itemView.findViewById(R.id.rvcomment_tv_comment);
            btnMenu = itemView.findViewById(R.id.rvcomment_btn_menu);
            editComment = itemView.findViewById(R.id.rvcomment_et_comment);
            modifyLayout = itemView.findViewById(R.id.rvcomment_ll_modify);
            commentModify = itemView.findViewById(R.id.rvcomment_tv_modify);
            modifyCancel = itemView.findViewById(R.id.rvcomment_tv_cancel);
            profile = itemView.findViewById(R.id.rvcomment_iv_profile);
            this.mainContext = context;
        }
    }

    @NonNull
    @Override
    public CommentRVHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_comment, parent, false);
        context = parent.getContext();
        CommentRVHolder vh = new CommentRVHolder(view, context);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentRVHolder holder, int position) {
        holder.writer.setText(comments.get(position).getWriterNickname());
        holder.date.setText(Util.INSTANCE.unixTime2String(Long.parseLong(comments.get(position).getDate())));
        holder.comment.setText(comments.get(position).getComment());
        holder.dateText = comments.get(position).getDate();

        if(comments.get(position).getProfile().equals("") == false){
            Glide.with(context).load(comments.get(position).getProfile()).circleCrop().into(holder.profile);
        }

        holder.writer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) holder.mainContext;
                mainActivity.setMyPageID(comments.get(position).getWriterId());
                mainActivity.outsideMyPageClick();
            }
        });


        if (comments.get(position).getWriterId().equals(LoginInfo.INSTANCE.getLoginInfo(context)[0])) {
            holder.btnMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context, v);
                    popup.getMenuInflater().inflate(R.menu.comment, popup.getMenu());
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.comment_modify:
                                    holder.comment.setVisibility(View.INVISIBLE);
                                    holder.modifyLayout.setVisibility(View.VISIBLE);
                                    holder.btnMenu.setVisibility(View.INVISIBLE);
                                    holder.editComment.setText(holder.comment.getText().toString());

                                    holder.modifyCancel.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            holder.btnMenu.setVisibility(View.VISIBLE);
                                            holder.modifyLayout.setVisibility(View.INVISIBLE);
                                            holder.comment.setVisibility(View.VISIBLE);
                                        }
                                    });

                                    holder.commentModify.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        String postId = comments.get(position).getPostId();
                                                        String userId = comments.get(position).getWriterId();
                                                        String date = holder.dateText;
                                                        String newComment = holder.editComment.getText().toString();
                                                        UpdateCommentReq updateCommentReq = new UpdateCommentReq(userId, date, newComment);
                                                        if (newComment.equals("")) {
                                                            Toast.makeText(context, "???????????????", Toast.LENGTH_SHORT).show();
                                                            return;
                                                        } else {
                                                            ButterKnifeApi.INSTANCE.getRetrofitService().updateComment(postId, updateCommentReq).enqueue(new Callback<UpdateCommentRes>() {
                                                                @Override
                                                                public void onResponse(Call<UpdateCommentRes> call, Response<UpdateCommentRes> response) {
                                                                    if (response.body() != null) {
                                                                        if (response.body().getResult().equals("Success")) {
                                                                            CommentItem newComment = response.body().getData();
                                                                            holder.dateText = newComment.getDate();
                                                                            holder.date.setText(Util.INSTANCE.unixTime2String(Long.parseLong(holder.dateText)));
                                                                            holder.comment.setText(newComment.getComment());
                                                                            holder.modifyLayout.setVisibility(View.INVISIBLE);
                                                                            holder.btnMenu.setVisibility(View.VISIBLE);
                                                                            holder.comment.setVisibility(View.VISIBLE);
                                                                        } else {
                                                                            Toast.makeText(context, "???????????? ???????????????", Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }

                                                                }

                                                                @Override
                                                                public void onFailure(Call<UpdateCommentRes> call, Throwable t) {
                                                                    // ?????? ????????? ???????????? ????????? ?????? ??????
                                                                    Log.d("err", "SERVER CONNECTION ERROR");
                                                                }
                                                            });
                                                        }
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }.run();
                                        }
                                    });
                                    break;
                                case R.id.comment_delete:
                                    String postId = comments.get(position).getPostId();
                                    String userId = LoginInfo.INSTANCE.getLoginInfo(context)[0];
                                    String date = comments.get(position).getDate();
                                    deleteComment(postId, userId, date, position);
                                    break;
                            }
                            return true;
                        }
                    });
                    popup.show();
                }
            });
        } else {
            holder.btnMenu.setVisibility(View.GONE);
        }
    }

    private void deleteComment(String postId, String userId, String date, int position) {
        new Runnable() {
            @Override
            public void run() {
                try {
                    ButterKnifeApi.INSTANCE.getRetrofitService().deleteComment(postId, userId, date).enqueue(new Callback<CommonResponse>() {
                        @Override
                        public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                            if (response.body() != null) {
                                if (response.body().getResult().equals("Success")) {
                                    deleteComment(userId, date, position);
                                } else {
                                    Toast.makeText(context, "?????? ??????", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<CommonResponse> call, Throwable t) {
                            // ?????? ????????? ???????????? ????????? ?????? ??????
                            Log.d("err", "SERVER CONNECTION ERROR");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.run();
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public void addNewComment(CommentItem newItem) {
        comments.add(newItem);
        notifyItemInserted(comments.size() - 1);
    }

    public void deleteComment(String userId, String date, int position) {
        int index = -1;
        for (int i = 0; i < comments.size(); i++) {
            CommentItem tempComment = comments.get(i);
            if (userId.equals(tempComment.getWriterId()) && date.equals(tempComment.getDate())) {
                index = i;
                break;
            }
        }
        comments.remove(index);
        notifyItemRemoved(index);
        notifyItemChanged(index, comments.size());
    }
}
