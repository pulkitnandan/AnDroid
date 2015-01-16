package com.eudessess.webbrowser;

import android.app.Application;
import android.test.ApplicationTestCase;

import com.eudessess.webbrowser.database.InbuiltDatabase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
        testDatabase();
    }

    private void testDatabase() {
        InbuiltDatabase bookmarks = new InbuiltDatabase(this.getContext(), "BOOK_MARK", "DATA");
        bookmarks.insertRow("url", "newway");
        bookmarks.fetchData();
        bookmarks.deleteData("url");
    }
}