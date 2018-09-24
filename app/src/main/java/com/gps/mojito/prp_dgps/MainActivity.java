package com.gps.mojito.prp_dgps;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.gps.mojito.database.DBHelper;
import com.gps.mojito.decode.model.message;

public class MainActivity extends AppCompatActivity {
  private LocationManager locationManager = null;
  private Criteria criteria = null;
  private LocationListener locationListener = null;
  private GpsStatus.NmeaListener nmeaListener = null;
  private GpsStatus.Listener gpsStatusListener = null;
  private TextView txtGPS_Quality = null;
  private TextView txtGPS_Location = null;
  private TextView txtGPS_Satellites = null;
  private Handler mHandler = null;
  private ListView listView = null;

  private static final int REQUEST_EXTERNAL_STORAGE = 1;
  private static String[] PERMISSIONS_STORAGE = {
      "android.permission.READ_EXTERNAL_STORAGE",
      "android.permission.WRITE_EXTERNAL_STORAGE" };

  // Database
  private DBHelper helper;

  private boolean run = false;
  private final Handler handler = new Handler();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    // verifyStoragePermissions();

    //
    txtGPS_Quality = (TextView) findViewById(R.id.textGPS_Quality);
    txtGPS_Location = (TextView) findViewById(R.id.textGPS_Location);
    txtGPS_Satellites = (TextView) findViewById(R.id.textGPS_Satellites);
    listView = (ListView) findViewById(R.id.log_list);
    registerHandler();
    registerListener();
    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    locationManager.addNmeaListener(nmeaListener);

    helper = new DBHelper(MainActivity.this);
    try {
      Runtime.getRuntime().exec("logcat -f "
          + Environment.getExternalStorageDirectory().getAbsolutePath()
          + "/PRP-DGPS.txt");
    } catch (Exception e) {
      Log.d("LOG", e.getMessage());
    }
    run = true;
    handler.postDelayed(task, 1000);
  }

  private final Runnable task = new Runnable() {
    @Override
    public void run() {
      // TODO Auto-generated method stub
      if (run) {
        List<String> logList = Txt();
        Collections.reverse(logList);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
            R.layout.support_simple_spinner_dropdown_item, logList);
        listView.setAdapter(adapter);
        handler.postDelayed(this, 1000);
      }
    }
  };



  @Override
  protected void onDestroy() {
    Log.d("MAIN", "QUIT");
    // SaveDatabase();
    // TODO Auto-generated method stub

    super.onDestroy();
    locationManager.removeUpdates(locationListener);
    locationManager.removeNmeaListener(nmeaListener);
  }

  @Override
  public void finish() {

  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();
    //if (id == R.id.action_settings) {
    //	return true;
    //}
    return super.onOptionsItemSelected(item);
  }

  private void registerListener() {
    locationListener = new LocationListener() {

      @Override
      public void onLocationChanged(Location loc) {
        // TODO Auto-generated method stub
        //定位資料更新時會回呼
        Log.d("GPS-NMEA", loc.getLatitude() + "," + loc.getLongitude());
      }

      @Override
      public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub
        //定位提供者如果關閉時會回呼，並將關閉的提供者傳至provider字串中
      }

      @Override
      public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub
        //定位提供者如果開啟時會回呼，並將開啟的提供者傳至provider字串中
      }

      @Override
      public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
        Log.d("GPS-NMEA", provider + "");
        //GPS狀態提供，這只有提供者為gps時才會動作
        switch (status) {
          case LocationProvider.OUT_OF_SERVICE:
            Log.d("GPS-NMEA", "OUT_OF_SERVICE");
            break;
          case LocationProvider.TEMPORARILY_UNAVAILABLE:
            Log.d("GPS-NMEA", " TEMPORARILY_UNAVAILABLE");
            break;
          case LocationProvider.AVAILABLE:
            Log.d("GPS-NMEA", "" + provider + "");

            break;
        }

      }

    };
//
    nmeaListener = new GpsStatus.NmeaListener() {
      public void onNmeaReceived(long timestamp, String nmea) {
        //check nmea's checksum
        if (isValidForNmea(nmea)) {
          nmeaProgress(nmea);
          Log.d("GPS-NMEA", nmea);
          helper.insert(nmea);
          message tmp = new message(nmea);
          tmp.split();
        }

      }
    };
//
    gpsStatusListener = new GpsStatus.Listener() {
      public void onGpsStatusChanged(int event) {
        // TODO Auto-generated method stub
        GpsStatus gpsStatus;
        gpsStatus = locationManager.getGpsStatus(null);

        switch (event) {
          case GpsStatus.GPS_EVENT_FIRST_FIX:
            //
            gpsStatus.getTimeToFirstFix();
            Log.d("GPS-NMEA", "GPS_EVENT_FIRST_FIX");
            break;
          case GpsStatus.GPS_EVENT_SATELLITE_STATUS:

            Iterable<GpsSatellite> allSatellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite> it = allSatellites.iterator();

            int count = 0;
            while (it.hasNext()) {
              GpsSatellite gsl = (GpsSatellite) it.next();

              if (gsl.getSnr() > 0.0) {
                count++;
              }

            }


            break;
          case GpsStatus.GPS_EVENT_STARTED:
            //Event sent when the GPS system has started.
            Log.d("GPS-NMEA", "GPS_EVENT_STARTED");
            break;
          case GpsStatus.GPS_EVENT_STOPPED:
            //Event sent when the GPS system has stopped.
            Log.d("GPS-NMEA", "GPS_EVENT_STOPPED");
            break;
          default:
            break;
        }
      }

    };

  }

  private void registerHandler() {
	/*
	GGA Global Positioning System Fix Data. Time, Position and fix related data for a GPS receiver
	11
	1 2 34 5678 910|121314 15
	||||||||||||||| $--GGA,hhmmss.ss,llll.ll,a,yyyyy.yy,a,x,xx,x.x,x.x,M,x.x,M,x.x,xxxx*hh
	1) Time (UTC)
	2) Latitude
	3) N or S (North or South)
	4) Longitude
	5) E or W (East or West)
	6) GPS Quality Indicator,
	0 - fix not available,
	1 - GPS fix,
	2 - Differential GPS fix
	7) Number of satellites in view, 00 - 12
	8) Horizontal Dilution of precision
	9) Antenna Altitude above/below mean-sea-level (geoid)
	10) Units of antenna altitude, meters
	11) Geoidal separation, the difference between the WGS-84 earth
	ellipsoid and mean-sea-level (geoid), "-" means mean-sea-level below ellipsoid
	12) Units of geoidal separation, meters
	13) Age of differential GPS data, time in seconds since last SC104
	type 1 or 9 update, null field when DGPS is not used
	14) Differential reference station ID, 0000-1023
	15) Checksum
		 */
    mHandler = new Handler() {
      public void handleMessage(Message msg) {

        String str = (String) msg.obj;
        String[] rawNmeaSplit = str.split(",");
        txtGPS_Quality.setText(rawNmeaSplit[6]);
        txtGPS_Location.setText(rawNmeaSplit[2] + " " + rawNmeaSplit[3] + "," + rawNmeaSplit[4] + " " + rawNmeaSplit[5]);
        txtGPS_Satellites.setText(rawNmeaSplit[7]);

      }
    };


  }

  //custom
//取得nmea資料的callback
  private void nmeaProgress(String rawNmea) {

    String[] rawNmeaSplit = rawNmea.split(",");

    if (rawNmeaSplit[0].equalsIgnoreCase("$GPGGA")) {
      //send GGA nmea data to handler
      Message msg = new Message();
      msg.obj = rawNmea;
      mHandler.sendMessage(msg);
    }

  }


  private boolean isValidForNmea(String rawNmea) {
    boolean valid = true;
    byte[] bytes = rawNmea.getBytes();
    int checksumIndex = rawNmea.indexOf("*");
    //NMEA 星號後為checksum number
    byte checksumCalcValue = 0;
    int checksumValue;

    //檢查開頭是否為$
    if ((rawNmea.charAt(0) != '$') || (checksumIndex == -1)) {
      valid = false;
    }
    //
    if (valid) {
      String val = rawNmea.substring(checksumIndex + 1, rawNmea.length()).trim();
      checksumValue = Integer.parseInt(val, 16);
      for (int i = 1; i < checksumIndex; i++) {
        checksumCalcValue = (byte) (checksumCalcValue ^ bytes[i]);
      }
      if (checksumValue != checksumCalcValue) {
        valid = false;
      }
    }
    return valid;
  }

  public static void verifyStoragePermissions(Activity activity) {
    try {
      //检测是否有写的权限
      int permission = ActivityCompat.checkSelfPermission(activity,
          "android.permission.WRITE_EXTERNAL_STORAGE");
      if (permission != PackageManager.PERMISSION_GRANTED) {
        // 没有写的权限，去申请写的权限，会弹出对话框
        ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static String getLogcatInfo(){
    String strLogcatInfo = "";
    try{
      ArrayList<String> commandLine = new ArrayList<String>();
      commandLine.add("logcat");
      commandLine.add( "-d");

      commandLine.add("*:E"); // 过滤所有的错误信息

      ArrayList<String> clearLog = new ArrayList<String>();  //设置命令  logcat -c 清除日志
      clearLog.add("logcat");
      clearLog.add("-c");

      Process process = Runtime.getRuntime().exec(commandLine.toArray(new String[commandLine.size()]));
      BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(process.getInputStream()));

      String line = null;
      while ((line = bufferedReader.readLine()) != null) {
        Runtime.getRuntime().exec(clearLog.toArray(new String[clearLog.size()]));
        strLogcatInfo = strLogcatInfo + line + "\n";
      }

      bufferedReader.close();
    }
    catch(Exception ex)
    {
      Log.e("LOG", ex.getMessage());
    }
    return strLogcatInfo;
  }

  public List<String> Txt() {
    String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()
        + "/PRP-DGPS.txt";

    List newList=new ArrayList<String>();
    try {
      File file = new File(filePath);
      int count = 0;
      if (file.isFile() && file.exists()) {
        InputStreamReader isr = new InputStreamReader(new FileInputStream(file));
        BufferedReader br = new BufferedReader(isr);
        String lineTxt = null;
        while ((lineTxt = br.readLine()) != null) {
          if (!"".equals(lineTxt)) {
            String[] reds = lineTxt.split(" ");
            String msg = "";
            int time = 0;
            Boolean flag = false;
            for (String txt: reds) {
              if (txt.contains("E") || txt.contains("D") || txt.contains("E")) {
                flag = true;
              }

              if (flag)
                msg = msg + txt + " ";
              time++;
            }
            newList.add(count, msg);
            count++;
          }
        }
        isr.close();
        br.close();
      }else {
        Log.e("tag", "can not find file");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    if (newList.size()-10 > 0)
      return newList.subList(newList.size()-10, newList.size());
    return newList.subList(0, newList.size());
  }
}
