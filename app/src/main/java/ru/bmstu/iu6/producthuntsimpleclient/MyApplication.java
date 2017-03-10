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

package ru.bmstu.iu6.producthuntsimpleclient;

import android.app.Application;

import ru.bmstu.iu6.producthuntsimpleclient.helpers.HelperComponent;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.DaggerHelperComponent;
import ru.bmstu.iu6.producthuntsimpleclient.helpers.HelperModule;

/**
 * Created by Михаил on 08.03.2017.
 */

public class MyApplication extends Application {
    private HelperComponent helperComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        helperComponent = DaggerHelperComponent.builder()
                .helperModule(new HelperModule())
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public HelperComponent getHelperComponent() {
        return helperComponent;
    }
}
