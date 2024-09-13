package net.studioblueplanet.superset.util;

import android.content.ContentValues;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class FileWriter
{
    /**
     * This method checks if the given file exists in the LOGFOLDER
     * @param folder Folder to check
     * @param fileName File name to check
     * @param context Context to use
     * @return An Uri to the file if the file exists, otherwise null
     */
    private Uri isFilePresent(String folder, String fileName, Context context)
    {
        Uri contentUri          = MediaStore.Files.getContentUri("external");

        String selection        = MediaStore.MediaColumns.RELATIVE_PATH + "=?";

        String[] selectionArgs  = new String[]{Environment.DIRECTORY_DOCUMENTS + folder};

        Cursor cursor           = context.getContentResolver().query(contentUri, null, selection, selectionArgs, null);

        Uri uri = null;

        if (cursor.getCount() == 0)
        {

        }
        else
        {
            while (cursor.moveToNext())
            {
                String file = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DISPLAY_NAME));

                if (fileName.equals(fileName))
                {
                    long id = cursor.getLong(cursor.getColumnIndex(MediaStore.MediaColumns._ID));

                    uri = ContentUris.withAppendedId(contentUri, id);

                    break;
                }
            }
        }
        return uri;
    }


    /**
     * This method writes a message to a file in a folder in the MediaStore. It always uses the indicated
     * file and does not create new files
     * @param folder Folder to write the file to
     * @param fileName File name to write to
     * @param fileContent Content to write or append
     * @param context Context of given action
     * @param append Boolean indicating whether to append or overwrite
     */
    public void write(String folder, String fileName, String fileContent, Context context, boolean append)
    {
        OutputStream fos;
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {

                ContentValues values = new ContentValues();

                Uri uri=isFilePresent(folder, fileName, context);

                if (uri==null)
                {
                    values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);       //file name
                    values.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");        //file extension, will automatically add to file
                    values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS + folder);     //end "/" is not mandatory

                    uri = context.getContentResolver().insert(MediaStore.Files.getContentUri("external"), values);      //important!
                }
                if (append)
                {
                    fos = context.getContentResolver().openOutputStream(uri, "wa");
                }
                else
                {
                    fos = context.getContentResolver().openOutputStream(uri, "w");
                }
            }
            else
            {
                String docsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).toString();
                File file = new File(docsDir, fileName);
                System.out.println("FILE: "+file.toString());
                fos=new FileOutputStream(file, append);
            }
            try
            {
                fos.write(fileContent.getBytes());
            }
            finally
            {
                fos.close();
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
