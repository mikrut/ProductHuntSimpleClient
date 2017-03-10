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
import android.content.SharedPreferences;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.parceler.Parcels;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.bmstu.iu6.producthuntsimpleclient.MyApplication;
import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.api.ProductHunterApiEndpointInterface;
import ru.bmstu.iu6.producthuntsimpleclient.models.Post;
import ru.bmstu.iu6.producthuntsimpleclient.scheduler.BootReceiver;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class CategoryActivity extends AppCompatActivity {
    public static final String PREF_CATEGORY = "PREF_CATEGORY";
    public static final String PREF_CATEGORY_NAME = "PREF_CATEGORY_NAME";
    public static final String PREF_NEWEST = "PREF_NEWEST";

    private static final String SAVE_CATEGORY = "SAVE_CATEGORY";
    private static final String SAVE_POSTS = "SAVE_POSTS";

    @Inject Retrofit retrofit;
    ProductHunterApiEndpointInterface api;
    private CompositeSubscription subscriptions;

    @Inject SharedPreferences sharedPreferences;

    private ProductsAdapter adapter;
    private SwipeRefreshLayout swipe;
    private ActionBar actionBar;
    private TextView textHelper;

    private String category;
    private String savedCategory;
    private String categoryName;
    private List<Post> postList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        ((MyApplication) getApplication()).getHelperComponent().inject(this);
        api =  retrofit.create(ProductHunterApiEndpointInterface.class);

        adapter = new ProductsAdapter();
        RecyclerView productsRecycler = (RecyclerView) findViewById(R.id.recycler_products);
        productsRecycler.setAdapter(adapter);
        productsRecycler.setLayoutManager(new LinearLayoutManager(this));

        swipe = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipe.setOnRefreshListener(this::refresh);
        textHelper = (TextView) findViewById(R.id.text_helper);

        BootReceiver.setAlarm(getApplicationContext());

        if (savedInstanceState != null) {
            savedCategory = savedInstanceState.getString(SAVE_CATEGORY, null);
            postList = Parcels.unwrap(savedInstanceState.getParcelable(SAVE_POSTS));
        }

        actionBar = getSupportActionBar();
        if (actionBar == null) {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.category_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        final int id = item.getItemId();
        switch (id) {
            case R.id.action_pick_category:
                Intent intent = new Intent(this, PickCategoryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();

        category = sharedPreferences.getString(PREF_CATEGORY, "tech");
        categoryName = sharedPreferences.getString(PREF_CATEGORY_NAME, "Tech");
        actionBar.setTitle(categoryName);
        textHelper.setVisibility(View.GONE);

        if (savedCategory == null || !savedCategory.equals(category) || postList == null) {
            refresh();
        } else {
            dispatchNewPosts();
        }
    }

    private void dispatchNewPosts() {
        adapter.setPosts(postList);
        if (postList.size() > 0) {
            textHelper.setVisibility(View.GONE);
        } else {
            textHelper.setVisibility(View.VISIBLE);
            textHelper.setText(R.string.no_posts_today);
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (postList.size() > 0) {
            Post maxIdPost =
                    Collections.max(postList, (pf, ps) -> pf.getId().compareTo(ps.getId()));
            editor.putInt(PREF_NEWEST, maxIdPost.getId());
        } else {
            editor.remove(PREF_NEWEST);
        }
        editor.apply();
    }

    private void refresh() {
        swipe.setRefreshing(true);

        subscriptions.add(
                api.getPosts(category)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(posts -> {
                            postList = posts.getPosts();
                            dispatchNewPosts();
                            swipe.setRefreshing(false);
                        }, error -> {
                            Log.e(CategoryActivity.class.getSimpleName(), error.toString());
                            swipe.setRefreshing(false);
                            textHelper.setVisibility(View.VISIBLE);
                            textHelper.setText(R.string.somethings_wrong);
                        })
        );
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (postList != null && category != null) {
            outState.putString(SAVE_CATEGORY, category);
            outState.putParcelable(SAVE_POSTS, Parcels.wrap(postList));
        }
    }
}
