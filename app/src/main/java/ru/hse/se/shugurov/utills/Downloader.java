package ru.hse.se.shugurov.utills;

import android.content.Context;
import android.os.AsyncTask;

import org.apache.http.HttpEntity;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;

import ru.hse.se.shugurov.screens.FileDescription;


/**
 * Downloads files and stores them in a local storage
 *
 * Created by Ivan Shugurov
 */
public class Downloader extends AsyncTask<FileDescription, Void, Void>
{
    private Collection<FileDescription> fileDescriptions;
    private Context context;
    private DownloadCallback callback;

    public Downloader(Context context, DownloadCallback callback)
    {
        this.context = context;
        this.callback = callback;
    }

    public Downloader(Context context, Collection<FileDescription> fileDescriptions, DownloadCallback callback)
    {
        this(context, callback);
        this.fileDescriptions = fileDescriptions;
    }

    @Override
    protected Void doInBackground(FileDescription... fileToDownloads)
    {
        if (fileToDownloads.length != 0)
        {
            for (FileDescription description : fileToDownloads)
            {
                if (isCancelled())
                {
                    return null;
                }
                downloadFile(description);
            }
        }
        if (fileDescriptions != null)
        {
            for (FileDescription description : fileDescriptions)
            {
                if (isCancelled())
                {
                    return null;
                }
                downloadFile(description);
            }
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        super.onPostExecute(aVoid);
        if (callback != null && !isCancelled())
        {
            callback.downloadFinished();
        }
    }


    private void downloadFile(FileDescription description)
    {
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpEntity entity = client.execute(new HttpGet(description.getUrl())).getEntity();
            OutputStream outputStream = context.openFileOutput(description.getName(), Context.MODE_WORLD_READABLE);
            entity.writeTo(outputStream);
            outputStream.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public interface DownloadCallback
    {
        void downloadFinished();
    }
}
