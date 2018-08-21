package meatstore;

import android.util.Log;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;

import java.io.File;

public class exportCSV {
    private String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
    private String fileName = "gpsData.csv";
    private String filePath = baseDir + File.separator + fileName;
    private String tag = "exportCSV";
    private File f;

    public exportCSV (String fileName) {
        this.fileName = fileName;
    }

    public boolean creatFile () {
        try {
            this.f = new File(this.filePath);
            Log.d(this.tag, "Created file " + this.fileName);
            return true;
        }
        catch(Exception e) {
            Log.e(this.tag, e.getMessage());
            return false;
        }
    }

}
