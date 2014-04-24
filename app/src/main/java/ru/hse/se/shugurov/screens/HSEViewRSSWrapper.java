package ru.hse.se.shugurov.screens;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ru.hse.se.shugurov.utills.FileManager;


/**
 * Created by Иван on 28.10.13.
 */
public class HSEViewRSSWrapper extends HSEView implements HasFile
{

    private HSEViewRSS[] childViews;

    HSEViewRSSWrapper(JSONObject jsonObject) throws JSONException
    {
        super(jsonObject);
        url = HSEView.SERVER_LINK + "/api/structure/rss/" + getKey();
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(getKey(), url);
    }

    @Override
    public void notifyAboutFiles(Context context) throws JSONException
    {
        FileManager fileManager = new FileManager(context);
        String content = fileManager.getFileContent(getKey());  //TODO а можно ли использовать key как идентификатор?
        JSONObject jsonObject;   //TODO не забыть чистить старые файлы!!!! но не тут, а вообще
        jsonObject = new JSONObject(content);
        JSONArray jsonArray = jsonObject.getJSONArray("entries");
        childViews = new HSEViewRSS[jsonArray.length()];
        for (int i = 0; i < jsonArray.length(); i++)
        {
            childViews[i] = new HSEViewRSS(jsonArray.getJSONObject(i));
        }
    }

    public HSEViewRSS[] getConnectedViews()
    {
        return childViews;
    }
}
