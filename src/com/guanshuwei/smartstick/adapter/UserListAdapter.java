package com.guanshuwei.smartstick.adapter;

import java.util.Vector;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.guanshuwei.smartstick.R;
import com.guanshuwei.smartstick.data.UserInfoDatabase;
import com.guanshuwei.smartstick.instance.UserModule;
import com.guanshuwei.smartstick.util.CommandSender;
import com.guanshuwei.smartstick.util.UserStore;

public class UserListAdapter extends BaseAdapter {
	private Vector<UserModule> mUserList;
	private Context mContext;

	public UserListAdapter(Vector<UserModule> mUserList, Context mContext) {
		super();
		this.mUserList = mUserList;
		this.mContext = mContext;
	}

	public UserListAdapter(Vector<UserModule> userList) {
		super();
		this.mUserList = userList;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.mUserList.size();
	}

	@Override
	public Object getItem(int position) {
		return this.mUserList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	public class ViewHolder {
		TextView NameTextView;
		TextView PhoneTextVew;
		Button EditButton;
		Button DeleteButton;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = LayoutInflater.from(this.mContext).inflate(
					R.layout.item_userinfo, null);
		}

		viewHolder.DeleteButton = (Button) convertView
				.findViewById(R.id.delete);
		viewHolder.EditButton = (Button) convertView.findViewById(R.id.change_active);
		viewHolder.NameTextView = (TextView) convertView
				.findViewById(R.id.history_user_name);
		viewHolder.PhoneTextVew = (TextView) convertView
				.findViewById(R.id.user_phone);

		final UserModule user = this.mUserList.get(position);

		viewHolder.NameTextView.setText(user.getUserName());
		viewHolder.PhoneTextVew.setText(user.getUserPhoneNumber());

		final UserModule currentUser = UserStore.getInstance().getCurrentUser(
				mContext);

		if (user.getUserName().equals(currentUser.getUserName())) {
			viewHolder.EditButton.setText(this.mContext.getResources()
					.getString(R.string.actived));
			viewHolder.EditButton.setEnabled(false);
		} else {
			viewHolder.EditButton.setText(this.mContext.getResources()
					.getString(R.string.unactived));
			viewHolder.EditButton.setEnabled(true);
		}
		viewHolder.EditButton.setOnClickListener(new OnClickListener() {
			// BUG
			@Override
			public void onClick(View v) {
				Toast.makeText(mContext,
						"Current Activie User is " + user.getUserName(),
						Toast.LENGTH_LONG).show();
				UserStore.getInstance().saveUser(user, mContext);
				UserListAdapter.this.notifyDataSetChanged();

				new Thread() {

					@Override
					public void run() {
						super.run();
						try {
							Log.i("unbind", currentUser.getUserPhoneNumber());
							Log.i("bind", user.getUserPhoneNumber());

							CommandSender.getInstance().UnBindUser(mContext,
									currentUser.getUserPhoneNumber());
							CommandSender.getInstance().BindUser(mContext,
									user.getUserPhoneNumber());
						} catch (Exception e) {
							e.printStackTrace();
						}
					}

				}.start();

				// UserStore.getInstance().saveUser(user, mContext);

				// Intent intent = new Intent();
				// intent.setClass(UserListAdapter.this.mContext,
				// UserDetailActivity.class);
				// intent.putExtra(Constant.USER_INFO_OPERATION,
				// Constant.USER_INFO_OPERATION_EDIT);
				// intent.putExtra(Constant.USER_MODULE_ID, user.getID());
				// UserListAdapter.this.mContext.startActivity(intent);
			}
		});

		viewHolder.DeleteButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						mContext);
				builder.setMessage("是否确定删除用户")
						.setPositiveButton("删除",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										UserInfoDatabase userInfoDatabase = new UserInfoDatabase(
												UserListAdapter.this.mContext);
										UserModule item = UserListAdapter.this.mUserList
												.get(position);
										userInfoDatabase.remove(item);
										UserListAdapter.this.mUserList = userInfoDatabase
												.getAll();
										UserListAdapter.this
												.notifyDataSetChanged();
									}

								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub

									}

								});
			}
		});

		return convertView;
	}

	public Vector<UserModule> getUserList() {
		return mUserList;
	}

	public void setUserList(Vector<UserModule> mUserList) {
		this.mUserList = mUserList;
	}
}
