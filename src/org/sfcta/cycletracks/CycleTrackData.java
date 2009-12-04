package org.sfcta.cycletracks;

import java.util.Vector;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.widget.Toast;

public class CycleTrackData implements LocationListener {
	Vector <CyclePoint> coords;
	Activity activity = null;
	LocationManager lm = null;
	double startTime, latestUpdate;
	ItemizedOverlayTrack gpspoints;
	int lathigh,lgthigh,latlow,lgtlow,latestlat,latestlgt;
	boolean idle = true;
	
	// ---Singleton design pattern!  Only one CTD should ever exist.
	private CycleTrackData() {}
	private static class CTDHolder {
		private static final CycleTrackData INSTANCE = new CycleTrackData();
	}
	public static CycleTrackData get() {
		CycleTrackData ctd = CTDHolder.INSTANCE;
		return CTDHolder.INSTANCE;
	}
	// ---End Singleton design pattern.

	// If we're just starting to record, set initial conditions.
	void initializeData() {
		coords = new Vector <CyclePoint> ();
		startTime = System.currentTimeMillis();
		latestUpdate = latestlat = latestlgt = 0;
		lathigh = (int)(-100 * 1E6);
		latlow  = (int)(100 * 1E6);
		lgtlow  = (int)(360 * 1E6);
		lgthigh = (int)(-360 * 1E6);
		Drawable drawable = activity.getResources().getDrawable(R.drawable.point);
		gpspoints = new ItemizedOverlayTrack(drawable);
	}
	
	// Start getting updates
	void activateListener() {
		if (idle) {
			idle = false;
			initializeData();
		}
		lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
	}
	
	void killListener() {
		if (lm != null) lm.removeUpdates(this);
	}

	void addPointNow(Location loc, double currentTime) {
		int lat = (int)(loc.getLatitude() * 1E6);
		int lgt = (int)(loc.getLongitude() *1E6);
		addPointNow(lat,lgt,currentTime);
	}
	
	void addPointNow(int lat, int lgt, double currentTime) {
		// Skip duplicates
		if (latestlat==lat && latestlgt==lgt) return;
		
		CyclePoint pt = new CyclePoint(lat,lgt,currentTime); 
		coords.add(pt);
		//String startMsg = (coords.size()==1 ? "Start" : "");
		OverlayItem opoint = new OverlayItem(pt,"","");
		gpspoints.addOverlay(opoint);
		
		latlow = Math.min (latlow,lat);
		lathigh = Math.max(lathigh,lat);
		lgtlow = Math.min (lgtlow,lgt);
		lgthigh = Math.max(lgthigh,lgt);		
	}

	// LocationListener implementation:
	@Override
	public void onLocationChanged(Location loc) {
		if (loc != null) {
			// Only save one beep per second.
			double currentTime = System.currentTimeMillis();
			if (currentTime-latestUpdate >999) {
				addPointNow(loc,currentTime);
				latestUpdate = currentTime;
			}
		}
	}
	
	@Override
	public void onProviderDisabled(String arg0) {
		// Nothing
	}

	@Override
	public void onProviderEnabled(String arg0) {
		// Nothing
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// Nothing
	}
	// END LocationListener implementation:	
}
