package ru.hse.se.shugurov.gui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;

/**
 * Created by Иван on 14.03.14.
 */
public abstract class ScreenAdapter extends Fragment
{
    private static String HSE_VIEW_TAG = "hse_view";
    private HSEView hseView;

    public ScreenAdapter()
    {
    }

    public ScreenAdapter(HSEView hseView)//TODO а может и от него избавиться?
    {
        this.hseView = hseView;
        Bundle instanceState = new Bundle();
        instanceState.putSerializable(HSE_VIEW_TAG, hseView);
        setArguments(instanceState);
    }

    protected static void changeFragments(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    public static void setFragment(FragmentManager manager, Fragment fragmentToAppear)
    {
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.main, fragmentToAppear);
        transaction.commit();
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null && hseView == null)//TODO возможно, второе условие лишнее
        {
            hseView = (HSEView) savedInstanceState.get(HSE_VIEW_TAG);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        outState.putAll(getArguments());
        super.onSaveInstanceState(outState);
    }

    protected HSEView getHseView()
    {
        return hseView;
    }


    public String getActionBarTitle()
    {
        return hseView.getName();
    }//TODO зачем?

    public int getMenuId()
    {
        return -1;
    }

    public interface ActivityCallback
    {

        public Context getContext(); //TODO а надо ли вообще?

        public void refreshActionBar();//TODO а здесь ли?

    }

}
