package com.example.android.booksapi;

import android.text.TextUtils;

/**
 * {@Book} represents a Book item. It holds the details
 * of the book such as title and author.
 */
public class Book {

    // Title of the Book
    public final String mBookTitle;

    // Author of the Book
    public final String[] mBookAuthor;

    // Preview link of the Book
    public final String mBookLink;

    /**
     * Constructs a new {@link Book}.
     *
     * @param title is the title of the Book
     * @param author is the author(s) of the Book
     * @param link is the preview link of the Book (if available)
     */
    public Book(String title, String[] author, String link) {
        mBookTitle = title;
        mBookAuthor = author;
        mBookLink = link;
    }

    public String getBookAuthor() {
        return TextUtils.join(", ", mBookAuthor);
    }

    public String getBookTitle() {
        return mBookTitle;
    }

    public String getBookLink() {
        return mBookLink;
    }
}
