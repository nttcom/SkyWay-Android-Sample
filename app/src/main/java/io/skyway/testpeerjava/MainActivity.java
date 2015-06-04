package io.skyway.testpeerjava;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.*;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;


public class MainActivity extends FragmentActivity
{
	private static final String TAG = MainActivity.class.getSimpleName();
	public static final String OPTION_SERVER_TYPE = "server";

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);

		if (savedInstanceState == null)
		{
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new MenuFragment())
					.commit();
		}
	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();

		android.os.Process.killProcess( android.os.Process.myPid() );
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		int id = item.getItemId();


		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class MenuFragment
			extends Fragment
			implements View.OnClickListener
	{
		private final static String TAG = MenuFragment.class.getSimpleName();

		private final int[] _buttons =
				{
						R.id.btn_video,
						R.id.btn_data,
				};

		private int _iSelectedType;

		@Override
		public void onCreate(Bundle savedInstanceState)
		{
			super.onCreate(savedInstanceState);

		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout.fragment_main, container, false);

//			// Assign click event
			for (int iId : _buttons)
			{
				Button btn = (Button) rootView.findViewById(iId);
				btn.setOnClickListener(this);

				if ((R.id.btn_data == iId) || (R.id.btn_video == iId))
				{
					continue;
				}
			}
			return rootView;
		}


		@Override
		public void onClick(View v)
		{
			int iId = v.getId();

			if (R.id.btn_video == iId)
			{
				startVideoChat();
			}
			else if (R.id.btn_data == iId)
			{
				startChat();
			}
		}


		private void startVideoChat()
		{

			try
			{
				Context context = getActivity().getApplicationContext();

				Intent intentParam = new Intent();
				intentParam.setClass(context, MediaActivity.class);
				intentParam.putExtra(OPTION_SERVER_TYPE, _iSelectedType);

				startActivity(intentParam);
			}
			catch (Exception exc)
			{

			}
		}

		private void startChat()
		{

			try
			{
				Context context =  getActivity().getApplicationContext();

				Intent intentParam = new Intent();
				intentParam.setClass(context, DataActivity.class);
				intentParam.putExtra(OPTION_SERVER_TYPE, _iSelectedType);

				startActivity(intentParam);
			}
			catch (Exception exc)
			{

			}
		}

	}
}
