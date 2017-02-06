/**
 * Copyright © 2014-2015 Adaptive Handy Apps, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/
// Project: AHA PM Data Access Layer
// Contributor(s): Nathan A. Tucker, M.A.Tucker

package com.adaptivehandyapps.ahathing.ahautils;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NetMetrics {

	private final static String TAG = "DiagTools";
	private final static Integer LAST_MSG_MAX_LEN = 32;

	private static String DIAGTOOLS_PREFS_NAME = "DIAGTOOLS_PREFS";
	private static String DIAGTOOLS_KEY_MIN_REQUEST_TIME = "minRequestTime";
	private static String DIAGTOOLS_KEY_MAX_REQUEST_TIME = "maxRequestTime";
	private static String DIAGTOOLS_KEY_TOTAL_REQUEST_TIME = "totalRequestTime";
	private static String DIAGTOOLS_KEY_AVERAGE_REQUEST_TIME = "averageRequestTime";
	private static String DIAGTOOLS_KEY_AMOUNT_OF_REQUESTS = "amountOfRequests";
	private static String DIAGTOOLS_KEY_CLOUD_TIMEOUT = "CloudTimeout";
	private static String DIAGTOOLS_KEY_PHONE_TIMEOUT = "PhoneTimeout";
	private static String DIAGTOOLS_KEY_LAST_MSG = "LastMsg";
	private static String DIAGTOOLS_KEY_SEND_DIAG = "pref_key_send_diag";
	private static String DIAGTOOLS_KEY_ACE_URL = "pref_key_ace_url";

	///////////////////////////////////////////////////////////////////////////
	// TODO: consolidate with NetUtils?
	private static boolean isNetworkAvailable(Context context) {
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return false;
		}
		
	    ConnectivityManager connectivityManager
	          = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		if (activeNetworkInfo != null) return activeNetworkInfo.isConnected();

		return false;
	}
	///////////////////////////////////////////////////////////////////////////
	public static synchronized void updateSuccess(Context context, long time)
	{
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return;
		}

		SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		
		long minRequestTime = settings.getLong(DIAGTOOLS_KEY_MIN_REQUEST_TIME, Long.MAX_VALUE);
		if( time < minRequestTime )
		{
			editor.putLong(DIAGTOOLS_KEY_MIN_REQUEST_TIME, time);
		}
		
		long maxRequestTime = settings.getLong(DIAGTOOLS_KEY_MAX_REQUEST_TIME, 0);
		if( time > maxRequestTime )
		{
			editor.putLong(DIAGTOOLS_KEY_MAX_REQUEST_TIME, time);
		}

		int amountOfRequests = settings.getInt(DIAGTOOLS_KEY_AMOUNT_OF_REQUESTS, 0);
		long totalRequestTime = settings.getLong(DIAGTOOLS_KEY_TOTAL_REQUEST_TIME, 0);
		
		editor.putInt(DIAGTOOLS_KEY_AMOUNT_OF_REQUESTS, amountOfRequests + 1);
		editor.putLong(DIAGTOOLS_KEY_TOTAL_REQUEST_TIME, (totalRequestTime + time) );
		editor.putLong(DIAGTOOLS_KEY_AVERAGE_REQUEST_TIME, (totalRequestTime + time) / (amountOfRequests + 1 ) );

		editor.commit();
	}
	///////////////////////////////////////////////////////////////////////////
	public static synchronized void updateFailure(Context context)
	{
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return;
		}

		SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		if( isNetworkAvailable(context) )
		{
			editor.putInt(DIAGTOOLS_KEY_CLOUD_TIMEOUT, settings.getInt(DIAGTOOLS_KEY_CLOUD_TIMEOUT, 0) + 1 );
		}
		else
		{
			editor.putInt(DIAGTOOLS_KEY_PHONE_TIMEOUT, settings.getInt(DIAGTOOLS_KEY_PHONE_TIMEOUT, 0) + 1 );
		}

		editor.commit();
	}
	///////////////////////////////////////////////////////////////////////////
	public static synchronized void updateLastMessage(Context context, String lastMsg) {
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return;
		}

		SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		// truncate message if length exceeds max
		if (lastMsg.length() > LAST_MSG_MAX_LEN) lastMsg = lastMsg.substring(0, LAST_MSG_MAX_LEN);

		editor.putString(DIAGTOOLS_KEY_LAST_MSG, lastMsg);
		editor.commit();
	}
	///////////////////////////////////////////////////////////////////////////
	public static String getDiagInfo(Context context)
	{
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return "Context undefined.";
		}

		SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);
		
		return "Total Requests: " + settings.getInt(DIAGTOOLS_KEY_AMOUNT_OF_REQUESTS, 0) + 
				"\nMin. Request Time: " + settings.getLong(DIAGTOOLS_KEY_MIN_REQUEST_TIME, 0) + 
				"ms\nMax. Request Time: " + settings.getLong(DIAGTOOLS_KEY_MAX_REQUEST_TIME, 0) + 
				"ms\nAvg. Request Time: " + settings.getLong(DIAGTOOLS_KEY_AVERAGE_REQUEST_TIME, 0) + 
				"ms\nService Timeouts: " + settings.getInt(DIAGTOOLS_KEY_CLOUD_TIMEOUT, 0) + 
				"\nPhone Timeouts: " + settings.getInt(DIAGTOOLS_KEY_PHONE_TIMEOUT, 0) +
				"\nLast Message: " + settings.getString(DIAGTOOLS_KEY_LAST_MSG, "nada");
	}
//	///////////////////////////////////////////////////////////////////////////
//	// display diag info
//	public static void getDisplayDiagInfo(final Context context, final Activity callerContext)
//	{
//		View view = callerContext.getLayoutInflater().inflate(R.layout.diag_info_detail_dialogue, null);
//
//		final EditText etAceUrl = (EditText)view.findViewById(R.id.etAceUrl);
//		etAceUrl.setText(AhaCloudDal.getAceUrl());
//
//		TextView tvHealth = (TextView)view.findViewById(R.id.tvHealth);
//		tvHealth.setText(NetMetrics.getDiagInfo(context));
//
//		new AlertDialog.Builder(callerContext)
//		.setTitle("Diagnosis Info")
//		.setView(view)
//				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						String newAceUrl = etAceUrl.getText().toString();
//						AhaCloudDal.setAceUrl(newAceUrl);
//						Log.v(TAG, "AlertDialog.Builder url: " + newAceUrl);
//						SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(callerContext).edit();
//						editor.putString(DIAGTOOLS_KEY_ACE_URL, newAceUrl);
//						editor.commit();
//
//						dialog.dismiss();
//					}
//				})
//				.setNeutralButton(R.string.label_reset, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						Boolean override = AhaCloudDal.clearOverride();
//						Log.v(TAG, "AlertDialog.Builder reset with clear, override=" + override);
//						setDefaults(context);
//
//						dialog.dismiss();
//					}
//				})
//				.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//					public void onClick(DialogInterface dialog, int which) {
//						Log.v(TAG, "AlertDialog.Builder cancel...");
//
//						dialog.dismiss();
//					}
//				})
//		.show();
//	}
	///////////////////////////////////////////////////////////////////////////
	public static void registerHandler(final Context context)
	{
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return;
		}

		ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);

		executorService.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
				
				if( sharedPref.getBoolean(DIAGTOOLS_KEY_SEND_DIAG, false) )
				{
					SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);

					//TODO: for now, just log the stuff. When DAL implemented, change this to a post
					
					Log.d(TAG + "ASD", "" +
							settings.getLong(DIAGTOOLS_KEY_MIN_REQUEST_TIME, 0) + "," + 
							settings.getLong(DIAGTOOLS_KEY_MAX_REQUEST_TIME, 0) + "," + 
							settings.getLong(DIAGTOOLS_KEY_AVERAGE_REQUEST_TIME, 0) + "," + 
							settings.getInt(DIAGTOOLS_KEY_CLOUD_TIMEOUT, 0) + "," + 
							settings.getInt(DIAGTOOLS_KEY_PHONE_TIMEOUT, 0) + "," +
							settings.getString(DIAGTOOLS_KEY_LAST_MSG, "nada"));
				}
			}    
		}, 60, 60, TimeUnit.SECONDS);
	}
	///////////////////////////////////////////////////////////////////////////
	public static boolean setDefaults(Context context) {
		// ensure context defined
		if (context == null) {
			Log.e(TAG, "Context undefined.");
			return false;
		}

		SharedPreferences settings = context.getSharedPreferences(DIAGTOOLS_PREFS_NAME, 0);
		SharedPreferences.Editor editor = settings.edit();

		editor.putLong(DIAGTOOLS_KEY_MIN_REQUEST_TIME, 0);
		editor.putLong(DIAGTOOLS_KEY_MAX_REQUEST_TIME, 0);
		editor.putLong(DIAGTOOLS_KEY_TOTAL_REQUEST_TIME, 0);
		editor.putLong(DIAGTOOLS_KEY_AVERAGE_REQUEST_TIME, 0);
		editor.putInt(DIAGTOOLS_KEY_AMOUNT_OF_REQUESTS, 0);
		editor.putInt(DIAGTOOLS_KEY_CLOUD_TIMEOUT, 0);
		editor.putInt(DIAGTOOLS_KEY_PHONE_TIMEOUT, 0);
		editor.putString(DIAGTOOLS_KEY_LAST_MSG, "nada");

		editor.commit();

		return true;
	}
	///////////////////////////////////////////////////////////////////////////
}
