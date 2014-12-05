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
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	BroadcastReceiver sendBroadcastReciever = new sentReciever();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReciever();
   
    Button send ;
    EditText messageText,phoneNum;
    
	
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");
        setContentView(R.layout.activity_main);
        send=(Button)findViewById(R.id.sendButton);
        messageText=(EditText)findViewById(R.id.messageText);
        phoneNum=(EditText)findViewById(R.id.phoneNumText);
        log("All items are initiated oncreate");
        send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				log("The message text is : "+messageText.getText().toString());
				log("The phone number is : "+phoneNum.getText().toString());
				sendSMS(messageText.getText().toString(), phoneNum.getText().toString());
			}
		});
    }
    @Override
    protected void onPause() {
    	// TODO Auto-generated method stub
    	super.onPause();
    	
    	try{
    		unregisterReceiver(sendBroadcastReciever);
    		unregisterReceiver(deliveryBroadcastReciever);
    		log(" both broadcast recievers are unregistered");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    @Override
    protected void onDestroy() {
    	// TODO Auto-generated method stub
    	super.onDestroy();
    	try{
    		unregisterReceiver(sendBroadcastReciever);
    		unregisterReceiver(deliveryBroadcastReciever);
    		log(" both broadcast recievers are unregistered");
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
    
    private void sendSMS(String message,String phone){
    	String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        
        @SuppressWarnings("deprecation")
		SmsManager sms = SmsManager.getDefault();
        ArrayList<String> mesgParts= sms.divideMessage(message);
        int numPart = mesgParts.size();
        log(Integer.toString(numPart)+" message will be sent..");
        
        ArrayList<PendingIntent> sentPI = new ArrayList<PendingIntent>(); //
        ArrayList<PendingIntent> deliveredPI = new ArrayList<PendingIntent>(); //
                   
        for (int i=0 ; i<numPart; i++){
        	sentPI.add(PendingIntent.getBroadcast(this, 0, new Intent(SENT), 0));
        	deliveredPI.add(PendingIntent.getBroadcast(this, 0, new Intent(DELIVERED), 0));
        }
        registerReceiver(sendBroadcastReciever, new IntentFilter(SENT));
        registerReceiver(deliveryBroadcastReciever, new IntentFilter(DELIVERED));
        
        sms.sendMultipartTextMessage(phone, null, mesgParts, sentPI, deliveredPI);
        
    }
    
    class sentReciever extends BroadcastReceiver{
		@Override
		public void onReceive(Context arg0, Intent arg1) {
			// TODO Auto-generated method stub
			switch(getResultCode()){
			case Activity.RESULT_OK:
				log("Sms was sent");
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
    				break;
    			case Activity.RESULT_CANCELED:
    				log("Message was not delivered");
    				break;
    		}
    	}
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    private void log(String text){
    	Log.d("Main Activity", text);
    }
    
}
