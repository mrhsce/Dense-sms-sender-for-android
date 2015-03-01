package mrhs.ce.DenseSms.MessageLog;

import java.util.ArrayList;

import mrhs.ce.DenseSms.R;
import mrhs.ce.DenseSms.Database.OperationDatabaseHandler;
import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MessageLogMainActivity extends Activity {

	ListView mainList;
	public OperationDatabaseHandler dbHandler;
	public ArrayList<Integer> oprIdList;
	public ArrayList<String> oprMsgList,oprDateList;
	SentReciever reciever;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dbHandler = new OperationDatabaseHandler(this).open();
		oprIdList = new ArrayList<Integer>();
		oprMsgList = new ArrayList<String>();
		oprDateList = new ArrayList<String>();
		
		// Getting all the operation details
		Cursor cursor = dbHandler.getAllOperations();
		do{
			oprIdList.add(cursor.getInt(0));
			oprMsgList.add(cursor.getString(1));
			oprDateList.add(cursor.getString(2));
			log("The array size is: "+Integer.toString(oprIdList.size()));
		}while(cursor.moveToNext());
		setContentView(R.layout.activity_message_log_main);
		mainList = (ListView)findViewById(R.id.listMainLog);
		
		mainList.setAdapter(new MainLogArrayAdaptor(this,oprIdList));
		mainList.setDividerHeight(10); 
		mainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapter, View view,int position, long id){
				// TODO Auto-generated method stub
				// here there should be an an intent with the position to the messageLogActivity
			}
		});
		reciever = new SentReciever();
		registerReceiver(reciever, new IntentFilter("smsReport"));
	}
	
	class SentReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			mainList.setAdapter(new MainLogArrayAdaptor(MessageLogMainActivity.this,oprIdList));
		}
	}
	
	private void log(String text){
    	Log.d("MessageLogMainActivity", text);
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		dbHandler.close();
		super.onDestroy();
	}

}
