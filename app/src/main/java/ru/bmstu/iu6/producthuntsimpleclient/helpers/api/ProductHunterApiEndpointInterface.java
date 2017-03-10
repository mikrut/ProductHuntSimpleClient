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

package ru.bmstu.iu6.producthuntsimpleclient.helpers.api;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.bmstu.iu6.producthuntsimpleclient.models.wrappers.Categories;
import ru.bmstu.iu6.producthuntsimpleclient.models.wrappers.PostWrapper;
import ru.bmstu.iu6.producthuntsimpleclient.models.wrappers.Posts;
import rx.Observable;

/**
 * Created by Михаил on 08.03.2017.
 */

public interface ProductHunterApiEndpointInterface {

    @GET("posts")
    Observable<Posts> getTechPosts();

    @GET("categories/{category}/posts")
    Observable<Posts> getPosts(@Path("category") String category);

    @GET("categories")
    Observable<Categories> getCategories();

    @GET("posts/{id}")
    Observable<PostWrapper> getPostDetails(@Path("id") int id);

    @GET("posts/all")
    Observable<Posts> getLatestPosts(@Query("search[category]") String slug, @Query("newer") int newer);

}
