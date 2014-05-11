package ru.hse.se.shugurov.screens;

import android.os.Parcel;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Иван on 25.10.13.
 */
public class ScreenWithFile extends BaseScreen implements HasFile
{
    public static final Creator<ScreenWithFile> CREATOR = new Creator<ScreenWithFile>()
    {
        @Override
        public ScreenWithFile createFromParcel(Parcel source)
        {
            return new ScreenWithFile(source);
        }

        @Override
        public ScreenWithFile[] newArray(int size)
        {
            return new ScreenWithFile[size];
        }
    };
    private String fileType;
    private String fileName;

    ScreenWithFile(JSONObject jsonObject, String serverUrl) throws JSONException
    {
        super(jsonObject, serverUrl);
        fileType = jsonObject.getString("filetype");
        fileName = jsonObject.getString("filename");
        url = serverUrl + url;
    }

    private ScreenWithFile(Parcel source)
    {
        super(source);
    }

    public String getFileName()
    {
        return fileName;
    }

    public String getFileType()
    {
        return fileType;
    }

    @Override
    public FileDescription getFileDescription()
    {
        return new FileDescription(fileName, getUrl());
    }
}
