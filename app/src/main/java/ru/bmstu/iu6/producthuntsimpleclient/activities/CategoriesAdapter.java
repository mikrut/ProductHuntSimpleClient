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

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.DrawableUtils;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.models.Category;

/**
 * Created by Михаил on 08.03.2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<CategoriesAdapter.ViewHolder> {
    private List<Category> categories;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View categoryView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_category_small, parent, false);
        return new ViewHolder(categoryView);
    }

    @Override
    public void onBindViewHolder(CategoriesAdapter.ViewHolder holder, int position) {
        if (categories != null) {
            holder.initView(categories.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return categories != null ? categories.size() : 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView name;
        private final ImageView colorSquare;

        private Category category;

        private ViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.text_category_name);
            colorSquare = (ImageView) itemView.findViewById(R.id.view_category_square);

            itemView.setOnClickListener(view -> {
                if (category != null && listener != null) {
                    listener.onCategoryClick(category);
                }
            });
        }

        private void initView(Category category) {
            name.setText(category.getName());
            Drawable color = new ColorDrawable(Color.parseColor(category.getColor()));
            colorSquare.setImageDrawable(color);

            this.category = category;
        }
    }

    private OnCategoryClickListener listener;

    public OnCategoryClickListener getListener() {
        return listener;
    }

    public void setListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    public interface OnCategoryClickListener {
        void onCategoryClick(Category category);
    }
}
