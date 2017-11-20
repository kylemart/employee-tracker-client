package group19.employeetracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by John on 11/20/2017.
 */
public class CameraTest
{
    @Test
    public void takePicture() throws Exception
    {
        boolean testComplete = false;

        try
        {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            testComplete = true;
        }
        catch(Exception e)
        {

        }

        assertEquals(testComplete, true);
    }

    /*
    @Test
    public void sendSuccessful(Bitmap pic)
    {
        Bitmap returnPic;

        toDatabasePicture(employeeID, pic);
        returnPic = fromDatabasePicture(employeeID);

        assertEquals(returnPic, pic);
    }
    */
}