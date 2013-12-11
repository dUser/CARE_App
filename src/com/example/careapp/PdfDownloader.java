package com.example.careapp;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.widget.Toast;
/**
 * 
 * Class to handle downloading pdfs
 *
 */

public class PdfDownloader {

	Activity itsActivity;
	//int pdfSize = 0;

	/**
	 * 
	 * @param activity The activity that the pdfdownload is created within
	 */
	public PdfDownloader(Activity activity) {
		itsActivity = activity;
	}

	/**
	 * Download the indicated pdf. Once download display it. If the pdf file
	 * already exists just display it and don't download.
	 * 
	 * @param fileName name to save the pdf locally under
	 * @param url the pdf is located at
	 * @param title Title of the pdf
	 */
	public void viewPDF(String fileName, String url, String title) {

		//Check if external storage is available (code from google)
		boolean mExternalStorageAvailable = false;
		boolean mExternalStorageWriteable = false;
		String state = Environment.getExternalStorageState();

		if (Environment.MEDIA_MOUNTED.equals(state)) {
			// We can read and write the media
			mExternalStorageAvailable = mExternalStorageWriteable = true;
		} else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
			// We can only read the media
			mExternalStorageAvailable = true;
			mExternalStorageWriteable = false;
		} else {
			// Something else is wrong. It may be one of many other states, but all we need
			//  to know is we can neither read nor write
			mExternalStorageAvailable = mExternalStorageWriteable = false;
		}
		//end google code

		if (mExternalStorageAvailable || mExternalStorageWriteable) {
			//check if file exists
			File file = new File(itsActivity.getExternalFilesDir(null), fileName + ".pdf");

			if (file.exists()) {

				Uri path = Uri.fromFile(file);
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.setDataAndType(path, "application/pdf");
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

				try {
					itsActivity.startActivity(intent);
				} 
				catch (ActivityNotFoundException e) {
					Toast.makeText(itsActivity, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
				}

				//file doesn't exist    
			} else {
				//Download if external storage is available and writable
				if (mExternalStorageAvailable) {

					//check if there's enough space for the file

					//get available space on sd card
					StatFs fileSysemStats = new StatFs(itsActivity.getExternalFilesDir(null).getPath());
					long availableBytes = 0;
					int pdfSize = -1;

					//call method depending on api level
					if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
						availableBytes = calculateSize_JELLY_BEAN_MR2(fileSysemStats);
					} else {
						availableBytes = calculateSize(fileSysemStats);
					}
					try {
						//get size of file to be downloaded
						
						final long PDF_TIMEOUT_MILLIS = 500;
						PdfSizeGetter pdfSizeGetter = null;
						URL pdfUrl = new URL(url);						
						try {
							pdfSizeGetter = new PdfSizeGetter(Thread.currentThread());
							pdfSizeGetter.execute(pdfUrl);


							Thread.sleep(PDF_TIMEOUT_MILLIS);

							//request took too long
							pdfSizeGetter.cancel(true);

						} catch (InterruptedException e) {

							//got size in less than PDF_TIMEOUT_MILLIS/1000 seconds
							pdfSize = pdfSizeGetter.getPdfSize();
						}

					} catch (MalformedURLException e) {
						//can't download, bad url
						return;
						//although i don't think this can happen since we are hardcoding the urls
					}

					if (availableBytes >= pdfSize) {        	    	

						//Download the pdf
						final long DOWNLOAD_TIMEOUT_MILLIS = 1000;
						DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
						request.setDescription("Download Stockton PDF");
						request.setDestinationInExternalFilesDir(itsActivity, null, fileName + ".pdf");
						DownloadManager manager = (DownloadManager) itsActivity.getSystemService(Context.DOWNLOAD_SERVICE);
						long downloadRequestId = manager.enqueue(request);

						//TODO:Add something like what is being done for the pdf size checking, that is
						//add use asynctask, and interupt pause if download complete before the sleep timeout

						try {
							//timeout
							Thread.sleep(DOWNLOAD_TIMEOUT_MILLIS);
						} catch (InterruptedException e1) {
							Toast.makeText(itsActivity, "couldn't sleep", Toast.LENGTH_LONG).show();
						}
						//show downloaded file
						File downloadFile = new File(itsActivity.getExternalFilesDir(null), fileName + ".pdf");

						if (downloadFile.exists()) {
							Uri path = Uri.fromFile(downloadFile);
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(path, "application/pdf");
							intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

							try {
								itsActivity.startActivity(intent);
							} 
							catch (ActivityNotFoundException e) {
								Toast.makeText(itsActivity, "No Application Available to View PDF", Toast.LENGTH_LONG).show();
							}

						//file doesn't exist (yet)   
						} else {

							Query pdfQuery = new Query();
							pdfQuery.setFilterById(downloadRequestId);
							Cursor cur = manager.query(pdfQuery);

							if (cur.moveToFirst()) {
								int columnIndex = cur.getColumnIndex(DownloadManager.COLUMN_STATUS);
								int status = cur.getInt(columnIndex);
								
								switch (status) {
								case DownloadManager.STATUS_SUCCESSFUL:
									Toast.makeText(itsActivity, "Download successful, click again to view.", Toast.LENGTH_LONG).show();	        	    	
									break;
								case DownloadManager.STATUS_PAUSED:
									Toast.makeText(itsActivity, "Download paused.", Toast.LENGTH_LONG).show();	        	    	
									break;	
								case DownloadManager.STATUS_PENDING:
									Toast.makeText(itsActivity, "Download is waiting to start, when finished click again to view", Toast.LENGTH_LONG).show();	        	    	
									break;									
								case DownloadManager.STATUS_RUNNING:
									Toast.makeText(itsActivity, "Download running in background, when finished click again to view", Toast.LENGTH_LONG).show();	        	    	
									break;									
								case DownloadManager.STATUS_FAILED:
									Toast.makeText(itsActivity, "Download failed.", Toast.LENGTH_LONG).show();	        	    	
									break;									
								}
							}

						}

					//there's not enough space for the download	
					} else {
						Toast.makeText(itsActivity, "Not enough storage memory available. Can't download pdf.", Toast.LENGTH_LONG).show();	        	    	
					}

				//external storage not writable, can't download
				} else {
					Toast.makeText(itsActivity, "External storage is not writable. Can't download pdf.", Toast.LENGTH_LONG).show();
				}

			}
		// can't read or write external storage	
		} else { 
			Toast.makeText(itsActivity, "External storage is not available. Can't access or download pdf.", Toast.LENGTH_LONG).show();
		}
	}

	/*
	 * There of different ways of calculating the availble memory, depending on api version.
	 */

	@TargetApi(18)
	private long calculateSize(StatFs fileSysemStats) {
		return fileSysemStats.getBlockSizeLong() * fileSysemStats.getAvailableBlocksLong();
	}
	@SuppressWarnings("deprecation")
	@TargetApi(10)
	private long calculateSize_JELLY_BEAN_MR2(StatFs fileSysemStats) {

		return (long) fileSysemStats.getBlockSize() * (long) fileSysemStats.getBlockCount();
	}
	private class PdfSizeGetter extends AsyncTask<URL, Void, Void>  {
		Activity downloaderAct;
		Thread itsThread;
		int pdfSize = 0;
		
		/*
		 * Need activity param to create toasts
		 */
		PdfSizeGetter(Activity downloaderAct, Thread thread) {
			this.downloaderAct = downloaderAct;
			this.itsThread = thread;
		}
		PdfSizeGetter(Thread thread) {
			this.itsThread = thread;
		}
		public int getPdfSize() {
			return pdfSize;
		}
		@Override
		protected Void doInBackground(URL... urls) {
			int pdf_size = 0;
			URL downloadFileUrl = urls[0];
			URLConnection connection;
			try {
				connection = downloadFileUrl.openConnection();
				connection.connect();
				pdf_size = connection.getContentLength();

				/*
				 * If this thread has been cancelled, which 
				 * will happen if the specified time limit is exceeded,
				 * then don't try to interrupt the ui thread.
				 * Only interrupt it if we get the pdfSize within the time limit.
				 * 
				 */

				if (!isCancelled()) {
					pdfSize = pdf_size;
					itsThread.interrupt();
				} 


				/*
				 * 
				 * If there are problems and an exception is thrown
				 * there's nothing to do about it, when getPdfSize() is called
				 * it will simply return 0.
				 * 
				 * 
				 */
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return null;
		}







	}


}
