package funion.app.qparking.sqlite;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import funion.app.qparking.R;
import funion.app.qparking.SelectInfo;

/**
 * Created by 运泽 on 2015/12/30.
 */
public class MySqliteManager {
    private final int BUFFER_SIZE = 500000;
    List<String> msg;
    List<String> municipality;
    List<String> dises;


    //保存的数据库的文件名称

    public static final String DB_NAME = "qpking";

    //表名
    public static final String TABLE_NAME = "areaTable";

    //该工程的包名称

    public static final String PACKAGE_NAME = "funion.app.qparking";

    //在手机当中存放数据库的位置

    public static final String DB_PATH = "/data"

            + Environment.getDataDirectory().getAbsolutePath() + "/"

            + PACKAGE_NAME;

    private SQLiteDatabase database;

    private Context mContext;

    public MySqliteManager(Context context) {

        mContext = context;

    }

    public void openDatabase() {

        database = this.openDatabase(DB_PATH + "/" + DB_NAME);

    }

    private SQLiteDatabase openDatabase(String dbfile) {

        try {

            //判断数据库文件是否存在，如果不存在直接导入，否则直接打开

            if (!(new File(dbfile).exists())) {

                InputStream is = mContext.getResources().openRawResource(R.raw.areainfo);

                FileOutputStream fos = null;

                try {
                    fos = new FileOutputStream(dbfile);
                    byte[] buffer = new byte[BUFFER_SIZE];

                    int count = 0;

                    while ((count = is.read(buffer)) > 0) {

                        fos.write(buffer, 0, count);

                    }

                    fos.close();

                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,

                    null);
            municipality = new ArrayList<String>();
            municipality.add("北京");
            municipality.add("天津");
            municipality.add("上海");
            municipality.add("重庆");

            return db;

        } catch (Exception e) {

            Log.e("Database", "IO exception");

            e.printStackTrace();

        }

        return null;
    }

    public void select_pro(String info) {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where region_name= ?", new String[]{info});
        while (cursor.moveToNext()) {
            SelectInfo selectInfo = new SelectInfo();
            selectInfo.setPro_code(cursor.getInt(cursor.getColumnIndex("region_code")));
            String code = selectInfo.getPro_code() + "";
            msg = new ArrayList<String>();
        }
        cursor.close();
    }

    public void select_city(String info) {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where region_name= ?", new String[]{info});
        while (cursor.moveToNext()) {
            SelectInfo selectInfo = new SelectInfo();
            selectInfo.setCity_code(cursor.getInt(cursor.getColumnIndex("region_code")));
        }
        cursor.close();
    }

    public void select_dis(String info) {
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where region_name= ?", new String[]{info});
        while (cursor.moveToNext()) {
            SelectInfo selectInfo = new SelectInfo();
            selectInfo.setDis_code(cursor.getInt(cursor.getColumnIndex("region_code")));
        }
        cursor.close();
    }

    public List<String> select_dis_list(String info, String code) {
        String code_ = null;
        if (municipality.contains(info)) {
            code_ = code.substring(0, 3);
        } else {
            code_ = code.substring(0, 4);
        }
        dises = new ArrayList<String>();
        Cursor cursor = database.rawQuery("select * from " + TABLE_NAME + " where region_code like ?", new String[]{code_ + "%"});
        if (cursor.moveToFirst()!=false) {
        while (cursor.moveToNext()) {
          String dis=cursor.getString(cursor.getColumnIndex("region_name"));
            dises.add(dis);
        }
        }
        Log.e("show", dises.toString());
        cursor.close();
        return dises;

    }
}
