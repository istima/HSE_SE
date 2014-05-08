package ru.hse.se.shugurov.gui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.HSEView;
import ru.hse.se.shugurov.screens.HSEViewRSS;
import ru.hse.se.shugurov.screens.HSEViewRSSWrapper;

/**
 * Used for demonstrating a list of RSS items via list adapter.
 * <p/>
 * See {@link ru.hse.se.shugurov.gui.RSSListAdapter}
 * <p/>
 * Created by Ivan Shugurov
 */
public class RSSFragment extends AbstractFragment
{
    /**
     * Default constructor used by Android for instantiating this class after it was destroyed.
     * Should not be used by developers.
     */
    public RSSFragment()
    {
    }

    /**
     * Constructs a fragment
     *
     * @param hseViewRSS object with a list of rss items. not null
     */
    public RSSFragment(HSEView hseViewRSS)
    {
        super(hseViewRSS);
    }

    private View showListOfRSSItems(final LayoutInflater inflater, final ViewGroup container)
    {
        HSEViewRSS[] rssItems = null;
        try
        {
            rssItems = ((HSEViewRSSWrapper) getHseView()).getRSS();
        } catch (JSONException e)
        {

        }
        ListView rssItemsView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
        if (rssItems == null)
        {
            Toast.makeText(getActivity(), "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
            return rssItemsView;
        }
        final RSSListAdapter rssListAdapter = new RSSListAdapter(getActivity(), rssItems);
        rssItemsView.setAdapter(rssListAdapter);
        rssItemsView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                HSEViewRSS selectedItem = rssListAdapter.getItem(position);
                switch (selectedItem.getType())
                {
                    case FULL_RSS:
                        ScreenFactory.instance().showFragment(selectedItem);
                        break;
                    case ONLY_TITLE:
                        openBrowser(selectedItem.getUrl());
                        break;
                }
            }
        });
        return rssItemsView;
    }

    /*Tries to open provided url in browser*/
    private void openBrowser(String url)
    {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        browserIntent.setData(uri);
        getActivity().startActivity(browserIntent);
    }

    /*inflates view for rss and sets titles when only 1 element has to be shown*/
    private View showEntireRSS(LayoutInflater inflater, ViewGroup container)
    {
        View rssLayout = inflater.inflate(R.layout.rss_layout, container, false);
        rssLayout.findViewById(R.id.rss_layout_content).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                openBrowser(getHseView().getUrl());
            }
        });
        if (getHseView() instanceof HSEViewRSS)
        {
            ((TextView) rssLayout.findViewById(R.id.rss_layout_title)).setText(((HSEViewRSS) getHseView()).getTitle());
            ((TextView) rssLayout.findViewById(R.id.rss_layout_text)).setText(((HSEViewRSS) getHseView()).getOmitted());
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.showEntireRSS.Inappropriate view type.");
        }
        return rssLayout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        if (getHseView() instanceof HSEViewRSSWrapper)
        {
            return showListOfRSSItems(inflater, container);
        } else if (getHseView() instanceof HSEViewRSS)
        {
            return showEntireRSS(inflater, container);
        } else
        {
            throw new IllegalStateException("Precondition violated in RssScreenAdapter.onCreateView().Inappropriate view type.");
        }
    }
}
