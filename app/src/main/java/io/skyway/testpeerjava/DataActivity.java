package io.skyway.testpeerjava;

import io.skyway.Peer.*;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DataActivity
		extends Activity
{
	private static final String TAG = DataActivity.class.getSimpleName();

	private Peer           _peer;
	private DataConnection _data;
	private Handler _handler;
	private String   _id;
	private String[] _listPeerIds;
	private Boolean  _bConnecting;
	private Runnable     _runAddLog;
	private List<String> _aryLogs;
	private Bitmap _image;


	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Window wnd = getWindow();
		wnd.addFlags(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.activity_chat);
		_handler = new Handler(Looper.getMainLooper());

		Context context = getApplicationContext();


		//////////////////////////////////////////////////////////////////////
		//////////////////  START: Initialize Peer ///////////////////////////
		//////////////////////////////////////////////////////////////////////


		// connect option
		PeerOption options = new PeerOption();

		// Please check this page. >> https://skyway.io/ds/
		//Enter your API Key and registered Domain.
		options.key = "";
		options.domain = "";

		// PeerOption has many options. Please check the document. >> http://nttcom.github.io/skyway/docs/

		_peer = new Peer(context, options);
		setPeerCallback(_peer);

		//////////////////////////////////////////////////////////////////////
		////////////////// END: Initialize Peer //////////////////////////////
		//////////////////////////////////////////////////////////////////////


		//
		// Initialize views
		//

		// Action button
		_bConnecting = false;
		Button btnAction = (Button) findViewById(R.id.btnAction);
		if (null != btnAction)
		{
			btnAction.setText("Connect");
			btnAction.setEnabled(true);
			btnAction.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					v.setEnabled(false);

					if (!_bConnecting) {
						// Calling dialog
						listingPeers();
					} else {
						// Close
						closing();
					}

					v.setEnabled(true);
				}
			});
		}

		// Send data button
		Button btnSendData = (Button)findViewById(R.id.btnSendData);
		if (null != btnSendData)
		{
			btnSendData.setText("Send Data");
			btnSendData.setEnabled(false);
			btnSendData.setOnClickListener(new View.OnClickListener()
			{
				@Override
				public void onClick(View v)
				{
					v.setEnabled(false);

					Spinner spinner = (Spinner)findViewById(R.id.spnSendType);
					int iType = spinner.getSelectedItemPosition();

					executeTest(iType);

					v.setEnabled(true);
				}
			});
		}

		// OwdId
		TextView tvId = (TextView)findViewById(R.id.tvOwnId);
		tvId.setTextColor(Color.BLACK);
		tvId.setBackgroundColor(Color.LTGRAY);
		tvId.setGravity(Gravity.CENTER);

		// Log
		TextView tvLog = (TextView)findViewById(R.id.tvLog);
		tvLog.setTextColor(Color.BLACK);
		tvLog.setBackgroundColor(Color.LTGRAY);


		// Send type
		ArrayAdapter<String> aaSend = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		aaSend.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		aaSend.add("Hello SkyWay.(String)");
		aaSend.add("3.14 (Double)");
		aaSend.add("SkyWay Image (Binary)");
		aaSend.add("1,2,3(Array)");
		aaSend.add("1:Val1 2:Val2 3:Val3 (Hash)");


		Spinner spinnerSend = (Spinner)findViewById(R.id.spnSendType);
		spinnerSend.setAdapter(aaSend);
		spinnerSend.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
		{
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
			{
			}
			@Override
			public void onNothingSelected(AdapterView<?> parent)
			{
			}
		});
		updateUI();
	}


	void connecting(String strPeerId)
	{
		if (null == _peer)
		{
			return;
		}

		if (null != _data)
		{
			_data.close();
			_data = null;
		}

		//////////////////////////////////////////////////////////////////////
		///////////////  START: Connecting Peer   ////////////////////////////
		//////////////////////////////////////////////////////////////////////

		// connect option
		ConnectOption option = new ConnectOption();
		option.metadata = "data connection";
		option.label = "chat";
		option.serialization = DataConnection.SerializationEnum.BINARY;


		// connect
		_data = _peer.connect(strPeerId, option);

		if (null != _data) {
			setDataCallback(_data);
		}
		//////////////////////////////////////////////////////////////////////
		////////////////  END: Connecting Peer   /////////////////////////////
		//////////////////////////////////////////////////////////////////////
	}



	/**
	 * Set peer callback
	 * @param peer
	 */


	private void setPeerCallback(Peer peer)
	{
		//////////////////////////////////////////////////////////////////////////////////
		////////////////////  START: Set SkyWay peer callback   //////////////////////////
		//////////////////////////////////////////////////////////////////////////////////

		// !!!: Event/Open
		peer.on(Peer.PeerEventEnum.OPEN, new OnCallback() {
			@Override
			public void onCallback(Object object) {
				// TODO: PeerEvent/OPEN

				if (object instanceof String) {
					_id = (String) object;
					_handler.post(new Runnable() {
						@Override
						public void run() {
							View vw = findViewById(R.id.tvOwnId);
							if ((null != vw) && (vw instanceof TextView)) {
								TextView tv = (TextView) vw;
								tv.setText("ID【"+_id+"】");
								tv.invalidate();
							}
						}
					});
				}
			}
		});

		// !!!: Event/Connection
		peer.on(Peer.PeerEventEnum.CONNECTION, new OnCallback()
		{
			@Override
			public void onCallback(Object object)
			{
				// TODO: PeerEvent/CONNECTION

				if (!(object instanceof DataConnection))
				{
					return;
				}
				_data = (DataConnection) object;
				setDataCallback(_data);
				_bConnecting = true;

				updateUI();
			}
		});


		// !!!: Event/Close
		peer.on(Peer.PeerEventEnum.CLOSE, new OnCallback()
		{
			@Override
			public void onCallback(Object object)
			{
			}
		});

		// !!!: Event/Disconnected
		peer.on(Peer.PeerEventEnum.DISCONNECTED, new OnCallback() {
			@Override
			public void onCallback(Object object) {
			}
		});

		// !!!: Event/Error
		peer.on(Peer.PeerEventEnum.ERROR, new OnCallback() {
			@Override
			public void onCallback(Object object) {
			}
		});
	}
	//////////////////////////////////////////////////////////////////////////////////
	/////////////////////  END: Set SkyWay peer callback   ///////////////////////////
	//////////////////////////////////////////////////////////////////////////////////



	//Unset peer callback
	void unsetPeerCallback(Peer peer)
	{
		peer.on(Peer.PeerEventEnum.OPEN, null);
		peer.on(Peer.PeerEventEnum.CONNECTION, null);
		peer.on(Peer.PeerEventEnum.CALL, null);
		peer.on(Peer.PeerEventEnum.CLOSE, null);
		peer.on(Peer.PeerEventEnum.DISCONNECTED, null);
		peer.on(Peer.PeerEventEnum.ERROR, null);
	}

	//////////////////////////////////////////////////////////////////////////////////
	///////////////  START: Set SkyWay peer Data connection callback   ///////////////
	//////////////////////////////////////////////////////////////////////////////////
	void setDataCallback(DataConnection data)
	{
		// !!!: DataEvent/Open
		data.on(DataConnection.DataEventEnum.OPEN, new OnCallback()
		{
			@Override
			public void onCallback(Object object)
			{
				// TODO: DataEvent/OPEN
				addLog("system","serialization:"+_data.serialization.toString());
				connected();
			}
		});

		// !!!: DataEvent/Data
		data.on(DataConnection.DataEventEnum.DATA, new OnCallback() {
			@Override
			public void onCallback(Object object) {
				String strValue = null;

				if (object instanceof String) {
					// TODO: Receive String object
					strValue = (String) object;
				} else if (object instanceof Double) {
					Double doubleValue = (Double) object;

					strValue = doubleValue.toString();
				} else if (object instanceof ArrayList) {
					// TODO: receive Array list object
					ArrayList arrayValue = (ArrayList) object;

					StringBuilder sbResult = new StringBuilder();

					for (Object item : arrayValue) {
						sbResult.append(item.toString());
						sbResult.append("\n");
					}

					strValue = sbResult.toString();
				} else if (object instanceof Map) {
					// TODO: receive Map object
					Map mapValue = (Map) object;

					StringBuilder sbResult = new StringBuilder();

					Object[] objKeys = mapValue.keySet().toArray();
					for (Object objKey : objKeys) {
						Object objValue = mapValue.get(objKey);

						sbResult.append(objKey.toString());
						sbResult.append(" = ");
						sbResult.append(objValue.toString());
						sbResult.append("\n");
					}

					strValue = sbResult.toString();
				} else if (object instanceof byte[]) {
					// TODO: receive byte[] object
					Bitmap bmp = null;
					byte[] byteArray = (byte[])object;
					if (byteArray != null) {
						bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
					}

					_image = bmp;

					updateUI();
					strValue = "Received Image.(Type:byte[])";
				}
				addLog("Partner", strValue);
			}
		});

		// !!!: DataEvent/Close
		data.on(DataConnection.DataEventEnum.CLOSE, new OnCallback() {
			@Override
			public void onCallback(Object object) {
				// TODO: DataEvent/CLOSE
				_data = null;
				disconnected();
			}
		});

		// !!!: DataEvent/Error
		data.on(DataConnection.DataEventEnum.ERROR, new OnCallback() {
			@Override
			public void onCallback(Object object) {
				// TODO: DataEvent/ERROR
				PeerError error = (PeerError) object;

				Log.d(TAG, "[On/DataError]" + error);

				String strMessage = error.message;
				String strLabel = getString(android.R.string.ok);

				MessageDialogFragment dialog = new MessageDialogFragment();
				dialog.setPositiveLabel(strLabel);
				dialog.setMessage(strMessage);

				dialog.show(getFragmentManager(), "error");
			}
		});
	}
	//////////////////////////////////////////////////////////////////////////////////
	/////////////////  END: Set SkyWay peer Data connection callback   ///////////////
	//////////////////////////////////////////////////////////////////////////////////

	void unsetDataCallback(DataConnection data)
	{
		data.on(DataConnection.DataEventEnum.OPEN, null);
		data.on(DataConnection.DataEventEnum.DATA, null);
		data.on(DataConnection.DataEventEnum.CLOSE, null);
		data.on(DataConnection.DataEventEnum.ERROR, null);
	}




	/**
	 * Destroy Peer.
	 */
	private void destroyPeer()
	{
		if (null != _data)
		{
			unsetDataCallback(_data);

			_data = null;
		}

		if (null != _peer)
		{
			unsetPeerCallback(_peer);

			if (false == _peer.isDisconnected)
			{
				_peer.disconnect();
			}

			if (false == _peer.isDestroyed)
			{
				_peer.destroy();
			}

			_peer = null;
		}
	}



	/**
	 * Get peers from signaling server.
	 */
	void listingPeers()
	{
		if ((null == _peer) || (null == _id) || (0 == _id.length()))
		{
			return;
		}

		_peer.listAllPeers(new OnCallback() {
			@Override
			public void onCallback(Object object) {
				if (!(object instanceof JSONArray)) {
					return;
				}

				JSONArray peers = (JSONArray) object;

				StringBuilder sbItems = new StringBuilder();
				for (int i = 0; peers.length() > i; i++) {
					String strValue = "";
					try {
						strValue = peers.getString(i);
					} catch (Exception e) {
						e.printStackTrace();
					}

					if (0 == _id.compareToIgnoreCase(strValue)) {
						continue;
					}

					if (0 < sbItems.length()) {
						sbItems.append(",");
					}

					sbItems.append(strValue);
				}

				String strItems = sbItems.toString();
				_listPeerIds = strItems.split(",");

				if ((null != _listPeerIds) && (0 < _listPeerIds.length)) {
					selectingPeer();
				}
			}
		});

	}

	/**
	 * Selecting peer
	 */
	void selectingPeer()
	{
		if (null == _handler)
		{
			return;
		}

		_handler.post(new Runnable() {
			@Override
			public void run() {
				FragmentManager mgr = getFragmentManager();

				PeerListDialogFragment dialog = new PeerListDialogFragment();
				dialog.setListener(
						new PeerListDialogFragment.PeerListDialogFragmentListener() {
							@Override
							public void onItemClick(final String item) {

								_handler.post(new Runnable() {
									@Override
									public void run() {
										connecting(item);
									}
								});
							}
						});
				dialog.setItems(_listPeerIds);

				dialog.show(mgr, "peerlist");
			}
		});
	}



	/**
	 * Closing connection.
	 */
	void closing()
	{
		if (false == _bConnecting)
		{
			return;
		}

		_bConnecting = false;

		if (null != _data)
		{
			_data.close();
		}
	}

	void connected()
	{
		_bConnecting = true;

		updateUI();
	}

	void disconnected()
	{
		_bConnecting = false;

		updateUI();
	}

	void addLog(String name, String strLog)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		sb.append(name);
		sb.append("]");
		sb.append(strLog);
		sb.append("\r\n");

		String strMessage = sb.toString();

		if (null == _aryLogs)
		{
			_aryLogs = Collections.synchronizedList(new ArrayList<String>());
		}

		_aryLogs.add(strMessage);

		if (null == _runAddLog)
		{
			_runAddLog = new Runnable()
			{
				@Override
				public void run()
				{
					if (null == _aryLogs)
					{
						return;
					}

					for (;;)
					{
						if (0 >= _aryLogs.size())
						{
							break;
						}

						String strMsg = _aryLogs.get(0);
						addLogMessage(strMsg);

						_aryLogs.remove(0);
					}
				}
			};
		}

		_handler.post(_runAddLog);
	}

	void addLogMessage(String strMessage)
	{
		TextView tvLog = (TextView)findViewById(R.id.tvLog);

		tvLog.append(strMessage);
	}

	void updateUI() {
		_handler.post(new Runnable() {
			@Override
			public void run() {
				Button btnAction = (Button) findViewById(R.id.btnAction);
				if (null != btnAction) {
					if (false == _bConnecting) {
						btnAction.setText("Connecting");
					} else {
						btnAction.setText("Disconnect");
					}
				}

				TextView tvOwnId = (TextView) findViewById(R.id.tvOwnId);
				if (null != tvOwnId) {
					if (null == _id) {
						tvOwnId.setText("");
					} else {
						tvOwnId.setText(_id);
					}
				}

				Button btnSendData = (Button) findViewById(R.id.btnSendData);
				if (null != btnSendData) {
					btnSendData.setEnabled(_bConnecting);
				}


				ImageView imageView = (ImageView) findViewById(R.id.receivedImg);
				imageView.setImageBitmap(_image);


			}
		});
	}

	void sendString()
	{
		String strData = "Hello SkyWay.";

		boolean bResult =  _data.send(strData);
		if(true == bResult){
			addLog("You", strData);
		}
	}

	void sendDouble()
	{
		//Double doubleValue = Double.MAX_VALUE;
		Double doubleValue = 3.14;

		boolean bResult =  _data.send(doubleValue);

		if(true == bResult){
			addLog("You", doubleValue.toString());
		}



	}

	void sendArray()
	{
		ArrayList<String> al = new ArrayList<>();

		for (int i = 1 ; 4 > i ; i++)
		{
			String strValue = String.format("[%d]", i);
			al.add(strValue);
		}

		boolean bResult =  _data.send(al);


		if(true == bResult){
			addLog("you", al.toString());
		}
	}

	void sendMap()
	{
		HashMap<String, String> map = new HashMap<>();

		for (int i = 1 ; 4 > i ; i++)
		{
			String strKey = String.format("%d", i);
			String strValue = String.format("Val:%d", i);

			map.put(strKey, strValue);
		}

		boolean bResult =  _data.send(map);


		if(true == bResult){
			addLog("You", map.toString());
		}


	}


	void sendByteBuffers()
	{
		ByteArrayOutputStream bo = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];

		try {

			BufferedInputStream is = new BufferedInputStream(getAssets().open("sample.png"));

			while(true){
				int len = is.read(buffer);
				if(len <0){
					break;
				}
				bo.write(buffer,0,len);
			};

			byte[] btValue = bo.toByteArray();

			ByteBuffer bbValue = ByteBuffer.wrap(btValue);

			boolean bResult = _data.send(bbValue);


			if(true == bResult){
				addLog("You", "send Image:Success");
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}



	void executeTest(int iType)
	{
		// TODO: Execute test
		if (null == _data)
		{
			return;
		}else if (0 == iType)
		{
			sendString();
		}
		else if (1 == iType)
		{
			sendDouble();
		}
		else if (2 == iType)
		{
			sendByteBuffers();
		}
		else if (3 == iType)
		{
			sendArray();
		}
		else if (4 == iType)
		{
			sendMap();
		}
	}


	@Override
	protected void onStart()
	{
		super.onStart();

		// Disable Sleep and Screen Lock
		Window wnd = getWindow();
		wnd.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		wnd.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	@Override
	protected void onResume() {
		super.onResume();

		_handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(
						Context.INPUT_METHOD_SERVICE);

				View view = getCurrentFocus();
				if (null != view) {
					imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
				}
			}
		}, 1000);
	}

	@Override
	protected void onStop()
	{
		// Enable Sleep and Screen Lock
		Window wnd	= getWindow();
		wnd.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		wnd.clearFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		destroyPeer();

		_listPeerIds = null;

		if (null != _handler)
		{
			_handler.removeCallbacks(_runAddLog);
		}

		_handler = null;

		super.onDestroy();
	}

}
