package com.example.com.reeder.gotoblu;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class GoToBluMainActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		View myLayout = findViewById(R.id.background);
		myLayout.setBackgroundResource(R.drawable.background);

		TextView link = (TextView) findViewById(R.id.bluLink);
		link.setText(Html.fromHtml("http://www.blucigs.com/store-locator/"));
		link.setAutoLinkMask(Linkify.WEB_URLS);

		Button useCurrentPos = (Button) findViewById(R.id.curPositionBtn);

		useCurrentPos.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Position currentPos = getCurrentPosition();
				new GetTask().execute(currentPos);
			}
		});

	}

	public Position getCurrentPosition() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (loc == null){
			Log.i("null", "loc is null");
		}
		try{
			Position curPos = new Position(loc.getLatitude(), loc.getLongitude());
			return curPos;
		}catch (NullPointerException e){
			e.printStackTrace();
		}
		return null;

	}	
	
	public void navigate(Position pos) {
		String uri = "google.navigation:ll=%f,%f";
		Intent navIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String
				.format(uri, pos.getLatitude(), pos.getLongitude())));
		startActivity(navIntent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_go_to_blu_main, menu);
		return true;
	}



	public Position getClosestStore(Position pos) {
		String url = "http://www.blucigs.com/store-locator/location/search/?lat="
				+ pos.getLatitude()
				+ "&lng="
				+ pos.getLongitude()
				+ "&radius=20";
		URL u;
		InputStream is = null;
		DataInputStream dis;
		String s;

		try {
			u = new URL(url);
			is = u.openStream();
			dis = new DataInputStream(new BufferedInputStream(is));
			String doc = "";
			while ((s = dis.readLine()) != null) {
				doc += s;
			}
			int start = doc.indexOf("latitude");
			int end = doc.indexOf("longitude");
			String latitude = doc.substring(start, end);
			start = latitude.indexOf("\"");
			latitude = latitude.substring(start).replace("\"", "");
			double lat = Double.parseDouble(latitude);
			
			start = doc.indexOf("longitude");
			end = doc.indexOf("address_display");
			String longitude = doc.substring(start, end);
			start = longitude.indexOf("\"");
			longitude = longitude.substring(start).replace("\"", "");
			double lon = Double.parseDouble(longitude);
			
			Position storeLocation = new Position(lat, lon);
			Log.i("Position:", storeLocation.toString());
			return storeLocation;
		} catch (MalformedURLException mue) {
			System.err.println("Ouch - a MalformedURLException happened.");
			mue.printStackTrace();
			System.exit(2);
		} catch (IOException ioe) {
			System.err.println("Oops- an IOException happened.");
			ioe.printStackTrace();
			System.exit(3);
		} finally {
			try {
				is.close();
			} catch (IOException ioe) {
			}
		}
		return null;
	}

	private class GetTask extends AsyncTask<Void, Void, Void> {
		Position pos;
		Position storeLoc;

		protected Void doInBackground(Void... urls) {
			storeLoc = getClosestStore(pos);
			return null;
		}

		protected void execute(Position pos) {
			this.pos = pos;
			super.execute();
		}

		protected void onProgressUpdate(Void... progress) {

		}

		protected void onPostExecute(Void result) {
			navStoreLoc(storeLoc);
		}

	}

	public void navStoreLoc(Position pos) {
		navigate(pos);
	}

    
}
