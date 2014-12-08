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
import android.telephony.gsm.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    
	BroadcastReceiver sendBroadcastReciever = new sentReciever();
    BroadcastReceiver deliveryBroadcastReciever = new deliverReciever();
   
    Button send ;
    EditText messageText;
    Spinner phoneNumsArraypinner;
    TextView phoneCountLabel;
    TextView messageCountLabel;
    
    // Constants related to the condition of the file and the sdcard
    
    Integer file_Exists ;
    Integer file_is_created;
    Integer error_creating;
    Integer no_sd_available;
    Integer no_text_file;
    Integer text_file_exists;
	
    ArrayList<String> fileNamesArray;
    ArrayList<ArrayList<String>> phoneNumsArray;
	
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
        phoneCountLabel=(TextView)findViewById(R.id.phoneCountLabel);
        messageCountLabel=(TextView)findViewById(R.id.messageCountLabel);
        messageText=(EditText)findViewById(R.id.messageText);        
        log("All items are initiated oncreate");
        settingUpTheSpinner();
        
        messageText.addTextChangedListener(new TextWatcher() {
			
					
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				setMessageCount(arg0.toString());
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
		});
        
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
    private void settingUpTheSpinner(){
    	phoneNumsArraypinner=(Spinner)findViewById(R.id.phoneNumSpinner);
        ArrayAdapter<String> adaptor=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,fileNamesArray);
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneNumsArraypinner.setAdapter(adaptor);
        phoneNumsArraypinner.setOnItemSelectedListener(new spinnerListener());
        if(phoneNumsArray.size()>0)
        	setPhoneCount(0);
        else
        	phoneCountLabel.setText("0\nشماره");
    }
    private void createPhonelist(){
    	fileNamesArray=new ArrayList<String>();
        phoneNumsArray=new ArrayList<ArrayList<String>>();
        Integer condition;
        if((condition=createDirectory())==file_Exists){
        	log("The file exists");
        	fileNamesArray= getTextfileNames();
        	if(fileNamesArray.size()>0){
        		log(Integer.toString(fileNamesArray.size())+" text files are found");
        		phoneNumsArray=getTextFileContents(fileNamesArray);
        		for(int i=0;i<phoneNumsArray.size();i++){
        			log("The text file "+String.valueOf(i)+"has"+Integer.toString(phoneNumsArray.get(i).size())+"numbers");
        			if(phoneNumsArray.get(i).size()>0)
        				log("The first one is "+phoneNumsArray.get(i).get(0));
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
    class spinnerListener implements  OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> parent, View arg1, int pos,
				long id) {
			// TODO Auto-generated method stub
			log("Item "+Integer.toString(pos)+" is selected");
			setPhoneCount(pos);
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
    	
    }
    public void setPhoneCount(int pos){
    	int count= phoneNumsArray.get(pos).size();
    	phoneCountLabel.setText(Integer.toString(count)+"\nشماره");
    	log(Integer.toString(count)+" is the number of the phone numbers");
    }
    
    public void setMessageCount(String editableText){
     	int num=SmsManager.getDefault().divideMessage(editableText).size();
     	messageCountLabel.setText(Integer.toString(num)+"\nپیام");
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
    public ArrayList<String> getTextfileNames(){ // Finds the text files in the specific folder and returns the names
    	File dir= new File (Environment.getExternalStorageDirectory().toString()+
				File.separator+"شماره های مخاطبان"+File.separator);
    	File[] files = dir.listFiles();
    	ArrayList<String> addrList=new ArrayList<String>();
    	for(File file : files){
    		if(file.isFile() && file.getName().endsWith(".txt"))
    			addrList.add(file.getName());
    	}
    	return addrList;
    	
    }
    public ArrayList<ArrayList<String>> getTextFileContents(ArrayList<String> addrList){ // returns the contents of the text files based on the names list
    	ArrayList<ArrayList<String>> phoneList=new ArrayList<ArrayList<String>>();
    	for(int i=0 ; i<addrList.size() ; i++){
    		phoneList.add(new ArrayList<String>());
    		File file=new File(Environment.getExternalStorageDirectory().toString()+
    				File.separator+"شماره های مخاطبان"+File.separator+addrList.get(i));
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
    
    

