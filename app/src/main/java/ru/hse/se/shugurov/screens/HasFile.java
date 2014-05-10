package ru.hse.se.shugurov.screens;

/**
 * Used to mark screens that have files which have to be downloaded and saved in local storage.
 * <strong>It is assumed that </strong>
 * Created by Ivan Shugurov
 */
public interface HasFile
{
    /**
     * Asks to construct and return description of a file which has to be downloaded
     *
     * @return file description
     */
    public FileDescription getFileDescription();
}