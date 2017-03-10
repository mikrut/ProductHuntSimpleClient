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

package ru.bmstu.iu6.producthuntsimpleclient.scheduler;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import javax.inject.Inject;

import retrofit2.Retrofit;
import ru.bmstu.iu6.producthuntsimpleclient.MyApplication;
import ru.bmstu.iu6.producthuntsimpleclient.R;
import ru.bmstu.iu6.producthuntsimpleclient.activities.CategoryActivity;
import ru.bmstu.iu6.producthuntsimpleclient.activities.ProductActivity;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.api.ProductHunterApiEndpointInterface;
import ru.bmstu.iu6.producthuntsimpleclient.models.Post;
import rx.android.schedulers.AndroidSchedulers;

public class CheckNewPostsService extends Service {
    @Inject
    SharedPreferences sharedPreferences;
    @Inject
    Retrofit retrofit;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ((MyApplication) getApplication()).getHelperComponent().inject(this);
        String slug = sharedPreferences.getString(CategoryActivity.PREF_CATEGORY, "tech");

        Integer newest = sharedPreferences.contains(CategoryActivity.PREF_NEWEST) ?
                sharedPreferences.getInt(CategoryActivity.PREF_NEWEST, 0) :
                null;

        if (newest != null) {
            ProductHunterApiEndpointInterface api = retrofit.create(ProductHunterApiEndpointInterface.class);
            final String mSlug = slug;
            api.getLatestPosts(slug, newest)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            posts -> {
                                String title =
                                        getResources().getString(R.string.notification_new_posts);
                                NotificationCompat.Builder builder =
                                        new NotificationCompat.Builder(this)
                                        .setSmallIcon(R.drawable.ic_fiber_new_white_24dp)
                                        .setContentTitle(title)
                                        .setAutoCancel(true);

                                int size = posts.getPosts().size();
                                if (size == 0) {
                                    String sNoNewPosts = getResources()
                                            .getString(R.string.notification_no_new_posts_on, mSlug);
                                    builder.setContentText(sNoNewPosts);

                                    Intent categoryIntent = new Intent(this, CategoryActivity.class);
                                    PendingIntent resultIntent = PendingIntent.getActivity(
                                            this,
                                            0,
                                            categoryIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                                    builder.setContentIntent(resultIntent);
                                } else if (size == 1) {
                                    Post post = posts.getPosts().get(0);
                                    builder.setContentTitle(post.getName());
                                    String sNewPost = getResources()
                                            .getString(R.string.notification_new_post, post.getTagline());
                                    builder.setContentText(sNewPost);

                                    Intent postIntent = new Intent(this, ProductActivity.class);
                                    postIntent.putExtra(ProductActivity.EXTRA_ID, post.getId());
                                    PendingIntent resultIntent = PendingIntent.getActivity(
                                            this,
                                            0,
                                            postIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                                    builder.setContentIntent(resultIntent);
                                } else {
                                    String sPostsCount = getResources()
                                            .getString(R.string.notification_new_posts_count, size);
                                    builder.setContentText(sPostsCount);

                                    Intent categoryIntent = new Intent(this, CategoryActivity.class);
                                    PendingIntent resultIntent = PendingIntent.getActivity(
                                            this,
                                            0,
                                            categoryIntent,
                                            PendingIntent.FLAG_UPDATE_CURRENT
                                    );
                                    builder.setContentIntent(resultIntent);
                                }

                                int mNotificationId = 001;
                                NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                manager.notify(mNotificationId, builder.build());
                            }, error -> {
                                // nothing
                            }, CheckNewPostsService.this::stopSelf
                    );
        }

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
