/*
 * Copyright (C) 2010 Felix Bechstein
 * 
 * This file is part of WebSMS.
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 3 of the License, or (at your option) any later
 * version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License along with
 * this program; If not, see <http://www.gnu.org/licenses/>.
 */
package com.test.android.developergarden;

import java.io.IOException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.telekom.api.common.ServiceEnvironment;
import com.telekom.api.common.auth.TelekomUPAuth;
import com.telekom.api.common.model.TelekomResponse;
import com.telekom.api.sendsms.SendSmsClient;
import com.telekom.api.sendsms.model.SendSmsRequest;

import de.ub0r.android.websms.connector.common.Connector;
import de.ub0r.android.websms.connector.common.ConnectorCommand;
import de.ub0r.android.websms.connector.common.ConnectorSpec;
import de.ub0r.android.websms.connector.common.ConnectorSpec.SubConnectorSpec;
import de.ub0r.android.websms.connector.common.Log;
import de.ub0r.android.websms.connector.common.Utils;
import de.ub0r.android.websms.connector.common.WebSMSException;

/**
 * Receives commands coming as broadcast from WebSMS.
 * 
 * @author flx
 */
public class DeveloperGardenConnector extends Connector {
	/** Tag for debug output. */
	private static final String TAG = "developer-garden-test";

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectorSpec initSpec(final Context context) {
		final String name = context.getString(R.string.connector_example_name);
		ConnectorSpec c = new ConnectorSpec(name);
		c.setAuthor(// .
		context.getString(R.string.connector_example_author));
		c.setBalance(null);
		c.setCapabilities(ConnectorSpec.CAPABILITIES_BOOTSTRAP
				| ConnectorSpec.CAPABILITIES_UPDATE
				| ConnectorSpec.CAPABILITIES_SEND
				| ConnectorSpec.CAPABILITIES_PREFS);
		c.addSubConnector(TAG, c.getName(),
				SubConnectorSpec.FEATURE_MULTIRECIPIENTS
						| SubConnectorSpec.FEATURE_CUSTOMSENDER
						| SubConnectorSpec.FEATURE_SENDLATER);
		return c;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final ConnectorSpec updateSpec(final Context context,
			final ConnectorSpec connectorSpec) {
		final SharedPreferences p = PreferenceManager
				.getDefaultSharedPreferences(context);
		if (p.getBoolean(Preferences.PREFS_ENABLED, false)) {
			connectorSpec.setReady();
		} else {
			connectorSpec.setStatus(ConnectorSpec.STATUS_INACTIVE);
		}
		return connectorSpec;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doBootstrap(final Context context, final Intent intent)
			throws WebSMSException {
		// TODO: bootstrap settings.
		// If you don't need to bootstrap any config, remove this method.
		Log.i(TAG, "bootstrap");

		// if (1 != 1) {
		// // If something fails, you should abort this method
		// // by throwing a WebSMSException.
		// throw new WebSMSException("message to user.");
		// }
		// The surrounding code will assume positive result of this method,
		// if no WebSMSException was thrown.
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected final void doUpdate(final Context context, final Intent intent)
			throws WebSMSException {
		// TODO: update account balance
		Log.i(TAG, "update");
		// See doBootstrap() for more details.
	}

	/**
	 * {@inheritDoc}
	 */
	// @Override
	// protected final void doSend(final Context context, final Intent intent)
	// throws WebSMSException {
	// // TODO: send a message provided by intent
	// Log.i(TAG, "send with sender "
	// + Utils.getSender(context, new ConnectorCommand(intent)
	// .getDefSender()));
	// // See doBootstrap() for more details.
	// }

	@Override
	protected final void doSend(final Context context, final Intent intent) {
		// TODO: send a message provided by intent
		//
		Log.i(TAG,
				"send with sender "
						+ Utils.getSender(context,
								new ConnectorCommand(intent).getDefSender()));

		String text = new ConnectorCommand(intent).getText();

		String[] numbers = new ConnectorCommand(intent).getRecipients();

		String number = "";

		for (String s : numbers) {
			number += s + ",";
		}

		String username = PreferenceManager
				.getDefaultSharedPreferences(context).getString(
						Preferences.USER, null);
		String password = PreferenceManager
				.getDefaultSharedPreferences(context).getString(
						Preferences.PASSWORD, null);

		String sender = PreferenceManager.getDefaultSharedPreferences(context)
				.getString(Preferences.SENDER, null);

		TelekomUPAuth auth = new TelekomUPAuth(username, password);
		auth.requestAccessToken();

		// if (!auth.hasValidToken()) {
		// throw new RuntimeException("Authentication error");
		// }

		SendSmsClient client = new SendSmsClient(auth, ServiceEnvironment.MOCK);

		SendSmsRequest request = new SendSmsRequest();

		// request.setNumber("+49yourFirstNumber,+49yourSecondNumber");
		request.setNumber(number);
		request.setMessage(text);
		request.setOriginator(sender);
		request.setFlash(false); // Not as flash message
		request.setAccount(null);

		TelekomResponse response;
		try {
			response = client.sendSms(request);
			if (!response.getSuccess()) {
				// throw new WebSMSException("sms not sent!!!");
				Log.i(TAG, "NOT SENT!!!");
				// See doBootstrap() for more details.
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}