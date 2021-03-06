package shine.tran.localtubeview.gui.businessobjects;

/**
 * Created by hai.tran on 6/14/2016.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import shine.tran.localtubeview.R;

public class ArrayAdapterSearchView extends SearchView {

    private SearchView.SearchAutoComplete mSearchAutoComplete;

    public ArrayAdapterSearchView(Context context) {
        super(context);
        initialize();
    }

    public ArrayAdapterSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialize();
    }

    public void initialize() {
        mSearchAutoComplete = (SearchAutoComplete) findViewById(android.support.v7.appcompat.R.id.search_src_text);
        mSearchAutoComplete.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSearchAutoComplete.setTextColor(Color.WHITE);
        this.setAdapter(null);
        this.setOnItemClickListener(null);
    }

    @Override
    public void setSuggestionsAdapter(CursorAdapter adapter) {
        // don't let anyone touch this
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener) {
        mSearchAutoComplete.setOnItemClickListener(listener);
    }

    public void setAdapter(ArrayAdapter<?> adapter) {
        mSearchAutoComplete.setAdapter(adapter);
        mSearchAutoComplete.setThreshold(0);
    }

    public void setText(String text) {
        mSearchAutoComplete.setText(text);
    }

}