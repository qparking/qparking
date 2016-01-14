package funion.app.qparking.tools;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import funion.app.qparking.R;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;

/**
 * 更新管理类
 * 
 * @author Administrator
 *
 */
public class UpdateManger {
	private Context context;
	private String updateMsg = "";
	private String updateContent = "";
	
	private String apkUrl = "";
	
	private Dialog noticeDialog;
	private Dialog downloadDialog;

	@SuppressLint("SdCardPath")
	private static final String savePath = "/sdcard/update_qparking/";
	private static final String saveFileName = savePath
			+ "UpdateQParkingRelease.apk";
	private ProgressBar mProgress;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;

	private int progress;
	private Thread downLoadThread; 
	private boolean interceptFlag = false;

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				installApk();
				break;

			}
			super.handleMessage(msg);
		};

	};

	public UpdateManger(Context context) {
		this.context = context;
	}

	public void checkUpdateInfo() {
		showNoticeDialog();
	}

	private void showNoticeDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("");
		builder.setMessage(updateContent);
		builder.setPositiveButton("", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				downLoadDialog();
			}
		});

		noticeDialog = builder.create();
		noticeDialog.show();

	}
	private void downLoadDialog() {

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("");
		builder.setMessage(updateMsg);
		final LayoutInflater inflater = LayoutInflater.from(context);
		View view = inflater.inflate(R.layout.progres_dialog, null);
		mProgress = (ProgressBar) view.findViewById(R.id.progressbar_dialog);
		builder.setView(view);

		// builder.setPositiveButton("", new OnClickListener() {
		//
		// @Override
		// public void onClick(DialogInterface dialog, int which) {
		// // TODO Auto-generated method stub
		// if(progress < 100){
		// downloadApk();
		// return ;
		// }else if(progress ==100){
		// dialog.dismiss();
		// }
		//
		// }
		// });
		builder.setNegativeButton("", new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				interceptFlag = true;

			}
		});
		downloadDialog = builder.create();
		downloadDialog.show();
		downloadApk();

	}
    
	//开启线程下载
	private void downloadApk() {
		downLoadThread = new Thread(mdownApkRunnable);
		downLoadThread.start();
	}
	//安装apk
	protected void installApk() {
		if (!AppTools.ExistSDCard()) {
//			AppTools.getToast(context, "SD卡不存在或未挂载！");
			return;
		} else {
			File apkfile = new File(saveFileName);
			if (!apkfile.exists()) {
				return;
			}
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.parse("file://" + apkfile.toString()),
					"application/vnd.android.package-archive");
			// File.toString();
			context.startActivity(intent);
		}

	}

	/**
	 */
	private Runnable mdownApkRunnable = new Runnable() {
		@Override
		public void run() {
			URL url;
			try {
				url = new URL(apkUrl);
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();

//				System.out.println(""
//						+ AppTools.FormetFileSize(Long.valueOf(length)));
				InputStream ins = conn.getInputStream();
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdir();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream outStream = new FileOutputStream(ApkFile);
				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = ins.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);

					mHandler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						mHandler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					outStream.write(buf, 0, numread);
				} while (!interceptFlag);
				outStream.close();
				ins.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

}
