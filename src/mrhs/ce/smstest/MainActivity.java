package mrhs.ce.smstest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import android.R.integer;
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
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	BroadcastReceiver sendBroadcastReciever = new sentReciever();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReciever();
   
    Button send ;
    EditText messageText;
    Spinner phoneNumSpinner;
    
    // Constants related to the condition of the file and the sdcard
    
    Integer file_Exists ;
    Integer file_is_created;
    Integer error_creating;
    Integer no_sd_available;
    Integer no_text_file;
    Integer text_file_exists;
	
    ArrayList<String> fileNames;
    ArrayList<ArrayList<String>> phoneNums;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");
        
        file_Exists=0;
        file_is_created=1;
        error_creating=2;
        no_sd_available=3;
        no_text_file=4;
        text_file_exists=5;        
        
        createPhonelist();
        
        setContentView(R.layout.activity_main);
        send=(Button)findViewById(R.id.sendButton);
        messageText=(EditText)findViewById(R.id.messageText);
        phoneNumSpinner=(Spinner)findViewById(R.id.phoneNumSpinner);
        log("All items are initiated oncreate");
        send.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				log("The message text is : "+messageText.getText().toString());
				//log("The phone number is : "+phoneNum.getText().toString());
				//sendSMS(messageText.getText().toString(), phoneNum.getText().toString());
			}
		});
    }
    private void createPhonelist(){
    	fileNames=new ArrayList<String>();
        phoneNums=new ArrayList<ArrayList<String>>();
        Integer condition;
        if((condition=createDirectory())==file_Exists){
        	log("The file exists");
        	fileNames= getTextFileNames();
        	if(fileNames.size()>0){
        		log(Integer.toString(fileNames.size())+" text files are found");
        		phoneNums=getTextFileContents(fileNames);
        		for(int i=0;i<phoneNums.size();i++){
        			log("The text file "+String.valueOf(i)+"has"+Integer.toString(phoneNums.get(i).size())+"numbers");
        			if(phoneNums.get(i).size()>0)
        				log("The first one is "+phoneNums.get(i).get(0));
        		}
        	}
        	else{
        		Toast.makeText(this, "هیچ فایل متنی در فولدر مورد نظر موجود نیست", Toast.LENGTH_LONG).show();        
        	}
        }
        else if(condition==file_is_created){
    		Toast.makeText(this, "لطفا فایل شماره ها را درون فولدر (شماره های مخاطبان) قرار دهید", Toast.LENGTH_LONG).show();  
        }
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
	private void sendSMS(String message,ArrayList<String> phone){
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
        
        //sms.sendMultipartTextMessage(phone, null, mesgParts, sentPI, deliveredPI);
        
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
    		File dir= new File (Environment.getExternalStorageDirectory().toString()+
    				File.separator+"شماره های مخاطبان"+File.separator);
    		if(dir.exists()){
    			log(Environment.getExternalStorageDirectory().toString()+"/شماره های مخاطبان/"+" exists");
    			return file_Exists;
    		}    		
    		else{
    			try{
    				if(dir.mkdirs()){
    					log(Environment.getExternalStorageDirectory().toString()+"/شماره های مخاطبان/"+
    				" is created in the sdcard");
    					Toast.makeText(this, "فایل (شماره های مخاطبان ) با موفقیت در حافظه خارجی ساخته شد", Toast.LENGTH_LONG).show();
    					return file_is_created;
    				}
    				else{
    					log("The directory could not be created in the sdcard");
    					Toast.makeText(this, "اشکال در ساخت فایل در حافظه خارجی", Toast.LENGTH_LONG).show();
    					return error_creating;
    				}
    			}catch(Exception e){
    				e.printStackTrace();
    				return error_creating;
    			}
    		}
    	}
    	else
    		return no_sd_available;
    }
    public ArrayList<String> getTextFileNames(){ // Finds the text files in the specific folder and returns the names
    	File dir= new File (Environment.getExternalStorageDirectory().toString()+
				File.separator+"شماره های مخاطبان"+File.separator);
    	File[] files = dir.listFiles();
    	ArrayList<String> addrList=new ArrayList<String>();
    	for(File file : files){
    		if(file.isFile() && file.getName().endsWith(".txt"))
    			addrList.add(file.getAbsolutePath());
    	}
    	return addrList;
    	
    }
    public ArrayList<ArrayList<String>> getTextFileContents(ArrayList<String> addrList){ // returns the contents of the text files based on the names list
    	ArrayList<ArrayList<String>> phoneList=new ArrayList<ArrayList<String>>();
    	for(int i=0 ; i<addrList.size() ; i++){
    		phoneList.add(new ArrayList<String>());
    		File file=new File(addrList.get(i));
    		try{
    			BufferedReader br = new BufferedReader(new FileReader(file));
    			String line;
    			while ((line=br.readLine())!= null){
    				if(line.matches("0([0-9]){10}"))
    					phoneList.get(i).add(line);
    			}
    		}catch(Exception e){
    			e.printStackTrace();
    		}
    	}
    	return phoneList;
    	
    }
}
    
    

