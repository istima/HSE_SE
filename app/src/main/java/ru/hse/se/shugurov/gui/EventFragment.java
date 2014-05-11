package ru.hse.se.shugurov.gui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONException;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.screens.EventScreen;

/**
 * This class is used to show list of events.
 * <p/>
 * For the required arguments see{@link ru.hse.se.shugurov.gui.AbstractFragment}
 *
 * @author Ivan Shugurov
 */
public class EventFragment extends AbstractFragment
{

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        ListView listView = (ListView) inflater.inflate(R.layout.activity_main_list, container, false);
        try
        {
            listView.setAdapter(new EventsListAdapter(((EventScreen) getScreen()).getEvents(), getActivity()));
        } catch (JSONException e)
        {
            Toast.makeText(getActivity(), "Не удалось загрузить контент", Toast.LENGTH_SHORT).show();
        }
        return listView;
    }

}
