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

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.bmstu.iu6.producthuntsimpleclient.MyApplication;
import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.api.ProductHunterApiEndpointInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class PickCategoryActivity extends AppCompatActivity {

    @Inject Retrofit retrofit;
    ProductHunterApiEndpointInterface api;
    CompositeSubscription subscriptions;

    @Inject SharedPreferences sharedPreferences;

    private CategoriesAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView textHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_category);

        ((MyApplication) getApplication()).getHelperComponent().inject(this);
        api = retrofit.create(ProductHunterApiEndpointInterface.class);

        adapter = new CategoriesAdapter();
        adapter.setListener(category -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(CategoryActivity.PREF_CATEGORY, category.getSlug());
            editor.putString(CategoryActivity.PREF_CATEGORY_NAME, category.getName());
            editor.apply();
            finish();
        });

        recyclerView = (RecyclerView) findViewById(R.id.recycler_categories);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textHelper = (TextView) findViewById(R.id.text_helper);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();

        subscriptions.add(
          api.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        categories -> {
                            adapter.setCategories(categories.getCategories());
                            progressBar.setVisibility(View.GONE);
                            recyclerView.setVisibility(View.VISIBLE);
                        },
                        error -> {
                            Log.e(PickCategoryActivity.class.getSimpleName(), error.toString());
                            progressBar.setVisibility(View.GONE);
                            textHelper.setVisibility(View.VISIBLE);
                            textHelper.setText(R.string.somethings_wrong);
                        }
                )
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }
}
