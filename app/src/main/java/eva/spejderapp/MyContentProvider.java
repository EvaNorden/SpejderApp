package eva.spejderapp;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class MyContentProvider extends ContentProvider {
    public static final Uri URI = Uri.parse("content://eva.spejderapp.MyContentProvider/1");
    public static final String FILNAVN = "billede.jpg";

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        log("openFile(\n" + uri + "\nmode=" + mode);
        try {
            File f = new File(getContext().getFilesDir(), FILNAVN);
            if (mode.equals("w")) {
                f.delete();
                f.createNewFile();
            }
            return ParcelFileDescriptor.open(f, ParcelFileDescriptor.MODE_READ_WRITE);
        } catch (IOException e) {
            e.printStackTrace();
            throw new FileNotFoundException(uri.getPath());
        }
    }

    private void log(final String tekst) {
        Log.d("KamerabilledeProvider", tekst);
    }

    @Override
    public boolean onCreate() {
        log("onCreate");
        return true;
    }

    @Override
    public String getType(Uri uri) {
        log("getType(" + uri);
        return "image/jpg";
    }


    @Override
    public void attachInfo(Context context, ProviderInfo info) {
        super.attachInfo(context, info);
        log("attachInfo(" + info.toString());

        // Sanity check our security
        if (info.exported) {
            //throw new SecurityException("Provider must not be exported");
        }
        if (!info.grantUriPermissions) {
            //throw new SecurityException("Provider must grant uri permissions");
        }
    }


    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        log("query( " + uri);
        return null;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
