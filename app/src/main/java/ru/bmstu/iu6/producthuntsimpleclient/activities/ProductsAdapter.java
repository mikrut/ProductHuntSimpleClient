/*
 * Copyright (c) 2017 Mikhail Krutyakov
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ru.bmstu.iu6.producthuntsimpleclient.activities;

import android.content.Intent;
import android.media.tv.TvView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.models.Post;

/**
 * Created by Михаил on 08.03.2017.
 */

public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }

    public void setPosts(List<Post> posts) {
        this.posts = posts;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View productView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_product_small, parent, false);
        return new ViewHolder(productView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (posts != null) {
            holder.initView(posts.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return (posts != null) ? posts.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView thumbnail;
        private TextView title;
        private TextView description;
        private TextView upvotes;

        private Integer id;

        private ViewHolder(View itemView) {
            super(itemView);

            thumbnail = (ImageView) itemView.findViewById(R.id.image_thumbnail);
            title = (TextView) itemView.findViewById(R.id.text_product_title);
            description = (TextView) itemView.findViewById(R.id.text_product_description);
            upvotes = (TextView) itemView.findViewById(R.id.text_product_upvotes);

            itemView.setOnClickListener(view -> {
                if (id != null) {
                    Intent intent = new Intent(view.getContext(), ProductActivity.class);
                    intent.putExtra(ProductActivity.EXTRA_ID, id);
                    view.getContext().startActivity(intent);
                }
            });
        }

        private void initView(Post post) {
            title.setText(post.getName());
            description.setText(post.getTagline());
            upvotes.setText(String.valueOf(post.getVotesCount()));
            id = post.getId();

            Glide.with(itemView.getContext())
                    .load(post.getThumbnail().getImageUrl())
                    .centerCrop()
                    .placeholder(R.drawable.ic_photo_black_24dp)
                    .into(thumbnail);
        }
    }
}
