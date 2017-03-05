package com.example.android.booksapi;

/*
* {@link BookAdapter} is an {@link ArrayAdapter} that can provide the layout for each list
* based on a data source, which is a list of {@link Book} objects.
* */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {

    private static final String LOG_TAG = BookAdapter.class.getSimpleName();

    /**
     * This is our own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists.
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books A List of Book objects to display in a list
     */
    public BookAdapter(Activity context, List<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for three TextViews, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position The position in the list of data that should be displayed in the
     *                 list item view.
     * @param convertView The recycled view to populate.
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        String bookAuthors = "";

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);

            // Set up the ViewHolderItem
            viewHolder = new ViewHolderItem();
            // Find the TextView in the list_item_xml layout with the ID title_text_view
            viewHolder.titleTextView = (TextView) listItemView.findViewById(R.id.title_text_view);
            // Find the TextView in the list_item.xml layout with the ID author_text_view
            viewHolder.authorTextView = (TextView) listItemView.findViewById(R.id.authors_text_view);

            // store the holder with the view.
            listItemView.setTag(viewHolder);
        } else {
            // Use the ViewHolderItem instead of calling findViewById every time
            viewHolder = (ViewHolderItem) listItemView.getTag();
        }

        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Assign values if the object is not null
        if(currentBook != null) {
            // get the TextView from the ViewHolder and then set the text (item name) and tag (item ID) values
            viewHolder.titleTextView.setText(currentBook.getBookTitle());
            viewHolder.authorTextView.setText(currentBook.getBookAuthor());
        }

        // Return the whole list item layout so that it can be shown in the ListView
        return listItemView;
    }

    // ViewHolder class for layout items
    static class ViewHolderItem {
        TextView titleTextView;
        TextView authorTextView;
    }
}
