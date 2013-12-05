package com.example.careapp;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.widget.Toast;
/**
 * 
 * Class to handle downloading pdfs
 *
 */

public class PdfDownloader {
	
	Activity itsActivity;
	
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
	        	    int pdfSize = 0;
	        	    
	        	    //call method depending on api level
	        	    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR2) {
	        	    	availableBytes = calculateSize_JELLY_BEAN_MR2(fileSysemStats);
	        	    } else {
	        	    	availableBytes = calculateSize(fileSysemStats);
	        	    }
	        	    try {
	        	    	//get size of file to be downloaded
	        	    	
	        	    	//allow networking on main thread
	        	    	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	        	    	StrictMode.setThreadPolicy(policy);
	        	    	//TODO: start thread requesting info, if it takes to long end it and just try to download
	        	    	
	        	    	URL downloadFileUrl = new URL(url);
	        	    	URLConnection connection = downloadFileUrl.openConnection();
	        	    	connection.connect();
	        	    	pdfSize = connection.getContentLength();
	        	    	if (pdfSize == -1) {
	        	    		throw new IOException("Content length of pdf not set.");
	        	    	}
	        	    //Could't determine size of the file to download
	        	    } catch (IOException e) {
	        	    	//Oh well, try to download anyway, there is more than likely enough space
	        	    }
	        	    
	        	    if (availableBytes >= pdfSize) {        	    	

	        	    	//Download the pdf
	        			DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
	        			request.setDescription("Download Stockton PDF");
	        			request.setDestinationInExternalFilesDir(itsActivity, null, fileName + ".pdf");
	        			DownloadManager manager = (DownloadManager) itsActivity.getSystemService(Context.DOWNLOAD_SERVICE);
	        			manager.enqueue(request);
	        			
	        			//TODO:Problem checking if file is downloaded before download manager downloads it
	        			try {
							Thread.sleep(3000);
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
	        	            
	        	        //file doesn't exist    
	        	        } else {
	        	        	Toast.makeText(itsActivity, "Download failed.", Toast.LENGTH_LONG).show();
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

}
