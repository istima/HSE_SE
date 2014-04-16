package ru.hse.se.shugurov.gui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import ru.hse.se.shugurov.R;
import ru.hse.se.shugurov.ViewsPackage.HSEView;
import ru.hse.se.shugurov.ViewsPackage.HSEViewTypes;

/**
 * Created by Иван on 15.03.14.
 */
public class ViewOfOtherViewsAdapter extends ScreenAdapter implements View.OnClickListener
{
    private static final int MINIMUM_WIDTH_OF_THE_ELEMENT = 300;
    private int screenWidth;
    // private ScreenAdapter additionalAdapter; TODO а зачем он тут?
    private HSEView currentView;

    public ViewOfOtherViewsAdapter(ActivityCallback callback, HSEView hseView)
    {
        super(callback, hseView);
        currentView = hseView;
    }

    /*@Override
    public void showPreviousView()
    {
        if (additionalAdapter != null && additionalAdapter.hasPreviousView())
        {
            additionalAdapter.showPreviousView();
        } else
        {
            if (hasPreviousView())
            {
                currentView = getHseView().getViewByIndex(currentView.getParentIndex());//TODO сделать из HSEView двусвязный список
            }
            super.showPreviousView();
        }
    }TODO delete*/


    @Override
    public String getActionBarTitle()
    {
        /*if (additionalAdapter != null && additionalAdapter.hasPreviousView())
        {
            return additionalAdapter.getActionBarTitle();
        } else TODO change or delete
        {*/
        return currentView.getName();
        //}
    }

    @Override
    public int getMenuId()
    {
        int menuId;
        if (currentView.isMainView())
        {
            menuId = R.menu.refresh_menu;
        } else
        {
            menuId = super.getMenuId();
        }
        return menuId;
    }


    private LinearLayout getLinearLayoutWithScreenItems(LayoutInflater inflater, HSEView[] elements)//TODO исправить использование поля indexes
    {
        int numberOfViewsInRow = 1;
        while (screenWidth / numberOfViewsInRow > (MINIMUM_WIDTH_OF_THE_ELEMENT + 20))
        {
            numberOfViewsInRow++;
        }
        numberOfViewsInRow--;
        LinearLayout content = new LinearLayout(getActivity());
        content.setOrientation(LinearLayout.VERTICAL);
        int rows = elements.length / numberOfViewsInRow;
        if (elements.length % numberOfViewsInRow != 0)
        {
            rows++;
        }
        int idOfCurrentView = 0;
        for (int i = 0; i < rows; i++)
        {
            LinearLayout linearLayout;
            linearLayout = new LinearLayout(getActivity());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            linearLayout.setPadding(10, 0, 10, 10);
            final ViewGroup[] items;
            int currentAmount = elements.length - i * numberOfViewsInRow >= numberOfViewsInRow ? numberOfViewsInRow : (elements.length - i * numberOfViewsInRow) % numberOfViewsInRow;
            items = new ViewGroup[currentAmount];
            for (int j = 0; j < currentAmount; j++)
            {


                int indexOfCurrentItem;
                indexOfCurrentItem = i * numberOfViewsInRow + j;
                items[j] = (ViewGroup) inflater.inflate(R.layout.item, null, false);
                ((TextView) items[j].findViewById(R.id.item_text_id)).setText(elements[indexOfCurrentItem].getName());
                Drawable image = getIconDrawable(elements[indexOfCurrentItem].getHseViewType());
                ((ImageView) items[j].findViewById(R.id.item_image_id)).setImageDrawable(image);
                items[j].setId(idOfCurrentView);
                idOfCurrentView++;
                items[j].setOnClickListener(this);
                items[j].setLayoutParams(new RelativeLayout.LayoutParams(screenWidth / numberOfViewsInRow - 20, ViewGroup.LayoutParams.MATCH_PARENT));
            }
            for (ViewGroup item : items)
            {
                linearLayout.addView(item);
            }
            content.addView(linearLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));
        }

        return content;
    }

    private int getScreenWidth()
    {
        WindowManager windowManager = ((WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE));
        Display display = windowManager.getDefaultDisplay();
        return display.getWidth();
    }

    private Drawable getIconDrawable(int HSEViewType) throws IllegalArgumentException
    {
        int drawableID;
        switch (HSEViewType)
        {
            case HSEViewTypes.VIEW_OF_OTHER_VIEWS:
                drawableID = R.drawable.section10;
                break;
            case HSEViewTypes.FILE:
                drawableID = R.drawable.section13;
                break;
            case HSEViewTypes.FAQ:
                drawableID = R.drawable.section7;
                break;
            case HSEViewTypes.FACEBOOK:
                drawableID = R.drawable.section5;
                break;
            case HSEViewTypes.VK_PUBLIC_PAGE_WALL:
                drawableID = R.drawable.section6;
                break;
            case HSEViewTypes.INNER_WEB_PAGE:
                drawableID = R.drawable.section1;
                break;
            case HSEViewTypes.HTML_CONTENT:
                drawableID = R.drawable.section4;
                break;
            case HSEViewTypes.WEB_PAGE:
                drawableID = R.drawable.section2;
                break;
            case HSEViewTypes.VK_FORUM:
                drawableID = R.drawable.section6;
                break;
            case HSEViewTypes.EVENTS:
                drawableID = R.drawable.section9;
                break;
            case HSEViewTypes.RSS_WRAPPER:
                drawableID = R.drawable.section8;
                break;
            case HSEViewTypes.MAP:
                drawableID = R.drawable.section13;
                break;
            case HSEViewTypes.LINKEDIN:
                drawableID = R.drawable.section12;
                break;
            default:
                throw new IllegalArgumentException("Unknown ID");
        }
        Resources resources = getActivity().getResources();
        return resources.getDrawable(drawableID);
    }


    @Override
    public void onClick(View view)
    {
        HSEView selectedView = currentView.getViewElements()[view.getId()];
        if (selectedView.getHseViewType() == HSEViewTypes.VIEW_OF_OTHER_VIEWS)
        {
            ScreenAdapter adapter = ScreenFactory.instance().createAdapter(selectedView);//TODO удалить вообще второй параметр, наверное
            changeFragments(getActivity().getFragmentManager(), adapter);
        } else
        {
            /*additionalAdapter = ScreenFactory.instance().createAdapter(selectedView, getCurrentView());
            refreshActionBar();TODO fix it*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        screenWidth = getScreenWidth();
        View content = getLinearLayoutWithScreenItems(inflater, currentView.getViewElements());
        ScrollView scrollView = (ScrollView) inflater.inflate(R.layout.activity_main_scroll, container, false);
        scrollView.addView(content);
        return scrollView;
    }
}
