package com.guanshuwei.smartstick.activity;

import java.util.Vector;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.config.Constant;
import com.guanshuwei.smartstick.data.UserInfoDatabase;
import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.util.Utilities;

public class UserDetailActivity extends Activity {
	private Button mSaveButton;
	private Button mBack;
	
	private EditText mUserName;
	private EditText mUserPhoneNumber;

	private UserModule mOldUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userdetail);

		this.mBack = (Button) this.findViewById(R.id.back);
		this.mBack.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		
		this.mSaveButton = (Button) this.findViewById(R.id.save);
		this.mUserName = (EditText) this.findViewById(R.id.username);
		this.mUserPhoneNumber = (EditText) this
				.findViewById(R.id.userphonenumber);

		String operation = this.getIntent().getStringExtra(
				Constant.USER_INFO_OPERATION);

		if (Utilities.isStringNullOrEmpty(operation)) {
			this.finish();
		}

		if (operation.equals(Constant.USER_INFO_OPERATION_ADD)) {
			this.mSaveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					saveUser();
					Toast.makeText(UserDetailActivity.this, "Saved",
							Toast.LENGTH_LONG).show();
				}
			});
		} else if (operation.equals(Constant.USER_INFO_OPERATION_EDIT)) {
			int id = this.getIntent().getIntExtra(Constant.USER_MODULE_ID, 0);

			UserInfoDatabase userDB = new UserInfoDatabase(this);

			UserModule user = new UserModule();
			Vector<UserModule> users = userDB.getAll();

			for (int i = 0; i < users.size(); i++) {
				if (users.get(i).getID() == id) {
					user = users.get(i);
					break;
				}
			}

			this.mUserName.setText(user.getUserName());
			this.mUserPhoneNumber.setText(user.getUserPhoneNumber());

			this.mSaveButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					mOldUser.setUserName(mUserName.getText().toString());
					mOldUser.setUserPhoneNumber(mUserPhoneNumber.getText()
							.toString());
					editUser();
				}
			});
		} else {
			this.finish();
		}
	}

	void saveUser() {
		UserModule user = new UserModule();
		user.setUserName(this.mUserName.getText().toString());
		user.setUserPhoneNumber(this.mUserPhoneNumber.getText().toString());

		UserInfoDatabase userDB = new UserInfoDatabase(this);
		userDB.addItem(user);
	}

	void editUser() {
		UserModule user = new UserModule();
		user.setUserName(this.mUserName.getText().toString());
		user.setUserPhoneNumber(this.mUserPhoneNumber.getText().toString());

		UserInfoDatabase userDB = new UserInfoDatabase(this);
		userDB.remove(this.mOldUser);
		userDB.addItem(user);
	}
}
