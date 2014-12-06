package mrhs.ce.smstest;

import java.io.File;
import java.util.ArrayList;

import android.os.Bundle;
import android.os.Environment;
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
import android.widget.Toast;

public class MainActivity extends Activity {
    
	BroadcastReceiver sendBroadcastReciever = new sentReciever();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReciever();
   
    Button send ;
    EditText messageText,phoneNum;
    
    // Constants related to the condition of the file and the sdcard
    
    Integer file_Exists_In_sd ;
    Integer file_is_created_in_sd;
    Integer error_creating_in_sd;
    Integer no_sd_available_use_internal;
    Integer no_text_file_in_sd;
    Integer no_text_file_in_internal;
    Integer text_file_in_sd;
    Integer text_file_in_internal;
	
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");
        
        file_Exists_In_sd=0;
        file_is_created_in_sd=1;
        error_creating_in_sd=2;
        no_sd_available_use_internal=3;
        no_text_file_in_sd=4;
        no_text_file_in_internal=5;
        text_file_in_sd=6;
        text_file_in_internal=7;
        
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
    
    @SuppressWarnings("deprecation")
	private void sendSMS(String message,String phone){
    	String SENT = "SMS_SENT";
        String DELIVERED = "SMS_DELIVERED";
        
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
		@SuppressWarnings("deprecation")
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
    
    public Integer createDirectory(){ 
    	if(android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)
    			&& !android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED_READ_ONLY)){
    		File dir= new File (Environment.getExternalStorageDirectory().toString()+"شماره های مخاطبان/");
    		if(dir.exists()){
    			log(Environment.getExternalStorageDirectory().toString()+"شماره های مخاطبان/"+" exists");
    			return file_Exists_In_sd;
    		}    		
    		else{
    			try{
    				if(dir.mkdir()){
    					log(Environment.getExternalStorageDirectory().toString()+"شماره های مخاطبان/"+
    				" is created in the sdcard");
    					Toast.makeText(this, "فایل (شماره های مخاطبان ) با موفقیت در حافظه خارجی ساخته شد", Toast.LENGTH_LONG).show();
    					return file_is_created_in_sd;
    				}
    				else{
    					log("The directory could not be created in the sdcard");
    					Toast.makeText(this, "اشکال در ساخت فایل در حافظه خارجی", Toast.LENGTH_LONG).show();
    					return error_creating_in_sd;
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				return error_creating_in_sd;
    			}
    		}
    	}
    	else
    		return no_sd_available_use_internal;
    }
    public Integer chkTextFile(Integer place){ // This function checks 
    	//the sdcard or the internal memory for finding the text files and returns the specific condition
    	return 0;
    	
    }
}
    
    

