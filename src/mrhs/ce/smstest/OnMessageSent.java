package mrhs.ce.smstest;


import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.telephony.gsm.SmsManager;
import android.util.Log;
import android.widget.TextView;

public class OnMessageSent extends Activity {

	BroadcastReceiver sendBroadcastReciever = new sentReciever();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReciever();
    
    Integer sentCounter=0;
    Integer deliveredCounter=0;
    
    TextView messageTextView,sentTextView,deliveredTextView;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_on_message_sent);
		messageTextView=(TextView)findViewById(R.id.messageTextView);
		sentTextView=(TextView)findViewById(R.id.sentCountLabel);
		deliveredTextView=(TextView)findViewById(R.id.deliveredCountLabel);
		
		messageTextView.setText(getIntent().getExtras().getString("message text"));
		
		sendMessage(getIntent().getExtras().getString("message text"),
				getIntent().getExtras().getStringArrayList("phone numbers"));
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try{
    		unregisterReceiver(sendBroadcastReciever);
    		unregisterReceiver(deliveryBroadcastReciever);
    		log(" both broadcast recievers are unregistered");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
		super.onDestroy();
	}
	
	@SuppressWarnings("deprecation")
	private void sendMessage(String text,ArrayList<String> phoneNumbers){
        SmsManager sms = SmsManager.getDefault();
        
        String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";      
                
        ArrayList<PendingIntent> sentPI = new ArrayList<PendingIntent>(); //
        ArrayList<PendingIntent> deliveredPI = new ArrayList<PendingIntent>(); //
        
        ArrayList<String> mesgParts= sms.divideMessage(text);
        int numPart = mesgParts.size();
        int phoneCount=phoneNumbers.size();
        for (int i=0 ; i<numPart*phoneCount; i++){
        	sentPI.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
        	deliveredPI.add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0));
        }
        registerReceiver(sendBroadcastReciever, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));
        //log(Integer.toString(phoneNumbers.size())+" message will be sent..");
        for(String phoneNum : phoneNumbers){
        	sms.sendMultipartTextMessage(phoneNum, null, mesgParts, sentPI, deliveredPI);
        }
        
	}	
	
	
	class sentReciever extends BroadcastReceiver{
		@SuppressWarnings("deprecation")
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			switch(getResultCode()){
			case Activity.RESULT_OK:
				log("Sms was sent");
				sentTextView.setText(Integer.toString(++sentCounter)+"\nفرستاده شد");
				break;
			case SmsManager.RESULT_ERROR_GENERIC_FAILURE:
				log("Genegic failure");
				break;
			case SmsManager.RESULT_ERROR_NO_SERVICE:
				log("No service");
                break;
            case SmsManager.RESULT_ERROR_NULL_PDU:
            	log("Null PDU");
                break;
            case SmsManager.RESULT_ERROR_RADIO_OFF:
            	log("Radio off");
                break;	
			}
		}
	}
	
	class deliverReciever extends BroadcastReceiver{
    	@Override
    	public void onReceive(Context arg0, Intent arg1) {
    		// TODO Auto-generated method stub
    		switch(getResultCode()){
    			case Activity.RESULT_OK:
    				log("Message was successfully delivered");
    				deliveredTextView.setText(Integer.toString(++deliveredCounter)+"\nدریافت شد");
    				break;
    			case Activity.RESULT_CANCELED:
    				log("Message was not delivered");
    				break;
    		}
    	}
    }
	
	private void log(String text){
    	Log.d("Main Activity", text);
    }

}
