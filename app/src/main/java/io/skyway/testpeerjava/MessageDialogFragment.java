package io.skyway.testpeerjava;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 *
 */
public class MessageDialogFragment extends DialogFragment
{

	public interface MessageDialogFragmentListener
	{
		void onItemClick(int which);
	}

	private MessageDialogFragmentListener	_listener;

	private String							_strTitle;
	private String							_strMessage;
	private String							_strPositive;
	private String							_strNegative;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)
	{
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

		if (null != _strTitle)
		{
			builder.setTitle(_strTitle);
		}

		if (null != _strMessage)
		{
			builder.setMessage(_strMessage);
		}

		if (null != _strPositive)
		{
			builder.setPositiveButton(_strPositive, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (null != _listener)
					{
						_listener.onItemClick(which);
					}

					dialog.dismiss();
				}
			});
		}

		if (null != _strNegative)
		{
			builder.setNegativeButton(_strNegative, new DialogInterface.OnClickListener()
			{
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					if (null != _listener)
					{
						_listener.onItemClick(which);
					}

					dialog.dismiss();
				}
			});
		}

		return builder.create();
	}

	public void setListener(MessageDialogFragmentListener listener)
	{
		_listener = listener;
	}

	public void setTitle(String strTitle)
	{
		_strTitle = strTitle;
	}

	public void setMessage(String strMessage)
	{
		_strMessage = strMessage;
	}

	public void setPositiveLabel(String strLabel)
	{
		_strPositive = strLabel;
	}

	public void setNegativeLabel(String strLabel)
	{
		_strNegative = strLabel;
	}
}
