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
import android.net.Uri;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Iterator;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.bmstu.iu6.producthuntsimpleclient.MyApplication;
import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.models.Post;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.api.ProductHunterApiEndpointInterface;
import rx.android.schedulers.AndroidSchedulers;
import rx.subscriptions.CompositeSubscription;

public class ProductActivity extends AppCompatActivity {

    public static final String EXTRA_ID =
            ProductActivity.class.getCanonicalName() + ".EXTRA_ID";

    public static final String SAVE_POST =
            ProductActivity.class.getCanonicalName() + ".SAVE_POST";

    private int id;

    @Inject
    Retrofit retrofit;
    ProductHunterApiEndpointInterface api;
    private CompositeSubscription subscriptions;

    private TextView description;
    private TextView upvotes;
    private ImageView screenshot;
    private Button getItButton;
    private ActionBar actionBar;
    private ProgressBar progressBar;
    private TextView productInfo;

    private Post post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        ((MyApplication) getApplication()).getHelperComponent().inject(this);
        api =  retrofit.create(ProductHunterApiEndpointInterface.class);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_ID)) {
            id = intent.getIntExtra(EXTRA_ID, -1);
        } else {
            finish();
        }

        description = (TextView) findViewById(R.id.text_product_description);
        upvotes = (TextView) findViewById(R.id.text_product_upvotes);
        screenshot = (ImageView) findViewById(R.id.image_product);
        getItButton = (Button) findViewById(R.id.button_product_get_it);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        productInfo = (TextView) findViewById(R.id.text_product_info);

        actionBar = getSupportActionBar();
        if (actionBar == null) {
            finish();
            return;
        }

        actionBar.setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState != null && savedInstanceState.containsKey(SAVE_POST)) {
            post = Parcels.unwrap(savedInstanceState.getParcelable(SAVE_POST));
        }
    }

    private void setPostElementsVisibility(int visibility) {
        description.setVisibility(visibility);
        upvotes.setVisibility(visibility);
        getItButton.setVisibility(visibility);
        screenshot.setVisibility(visibility);
    }

    private void setAwaitElementsVisibility(int visibility) {
        progressBar.setVisibility(visibility);
    }

    private void setInfoElementsVisibility(int visibility) {
        productInfo.setVisibility(visibility);
    }

    @Override
    protected void onStart() {
        super.onStart();
        subscriptions = new CompositeSubscription();

        if (post == null) {
            actionBar.setTitle(R.string.loading_progress);
            subscriptions.add(
                    api.getPostDetails(id)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    postWrapper -> {
                                        post = postWrapper.post;
                                        setPostData(post);
                                    }, error -> {
                                        Log.e(ProductsAdapter.class.getSimpleName(), error.getMessage());
                                        error.printStackTrace();

                                        actionBar.setTitle(R.string.loading_error);
                                        productInfo.setText(R.string.somethings_wrong);
                                        setAwaitElementsVisibility(View.GONE);
                                        setInfoElementsVisibility(View.VISIBLE);
                                    }
                            )
            );
        } else {
            setPostData(post);
        }
    }

    private int parsePxInt(String pxInt) {
        int result = 0;
        char[] chars = pxInt.toCharArray();
        int i = 0;

        if (chars.length > 0) {
            while (Character.isDigit(chars[i])) {
                i++;
            }
        }

        if (i > 0) {
            result = Integer.parseInt(pxInt.substring(0, i));
        }

        return result;
    }

    private void setPostData(Post post) {
        description.setText(post.getTagline());
        upvotes.setText(String.valueOf(post.getVotesCount()));

        String screenshotKey = Collections.max(post.getScreenshotUrl().keySet(),
                (sf, ss) -> parsePxInt(sf) - parsePxInt(ss));
        actionBar.setTitle(post.getName());

        Glide.with(this)
                .load(post.getScreenshotUrl().get(screenshotKey))
                .fitCenter()
                .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .into(screenshot);

        final Uri postUri = Uri.parse(post.getDiscussionUrl());
        getItButton.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, postUri);
            startActivity(intent);
        });

        setAwaitElementsVisibility(View.GONE);
        setPostElementsVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        subscriptions.unsubscribe();
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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (post != null) {
            outState.putParcelable(SAVE_POST, Parcels.wrap(post));
        }
    }
}
