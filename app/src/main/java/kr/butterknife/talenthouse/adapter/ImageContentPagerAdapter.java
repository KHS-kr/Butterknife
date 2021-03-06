package kr.butterknife.talenthouse.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;

import java.util.List;

import kr.butterknife.talenthouse.R;

public class ImageContentPagerAdapter extends PagerAdapter {
    private LayoutInflater inflater;
    private Context context;
    private List<String> imageUrl;


    public ImageContentPagerAdapter(Context context, List<String> imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
    }
    @Override
    public int getCount() {
        return imageUrl.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == ((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_content_image, container, false);
        ImageView imageView = view.findViewById(R.id.vp_iv_image);

        Glide.with(context)
                .load(imageUrl.get(position))
                .into(imageView);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.invalidate();
    }
}
