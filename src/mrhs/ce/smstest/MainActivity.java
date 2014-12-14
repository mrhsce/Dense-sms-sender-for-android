package mrhs.ce.smstest;


import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class MainActivity extends Activity {
    
	
   
    Button send ;
    EditText messageText;
    Spinner phoneNumsArraySpinner;
    TextView phoneCountLabel;
    TextView messageCountLabel;
    
    //tmp
    
    Button pickContactButton;
    Button manualGroupMakier;
    
    DatabaseHandler db;
    SdCardHandler sdHandler;
    
    String selectedGroup="";
	
   
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        log("Entered on create");         
        
        setContentView(R.layout.activity_main);         
        send=(Button)findViewById(R.id.sendButton);
        phoneCountLabel=(TextView)findViewById(R.id.phoneCountLabel);
        messageCountLabel=(TextView)findViewById(R.id.messageCountLabel);
        messageText=(EditText)findViewById(R.id.messageText);        
        log("All items are initiated oncreate");
        
        db=new DatabaseHandler(this);
        db.open();
        sdHandler=new SdCardHandler(db, this);  
        sdHandler.execute();									// In this part all the files in the directory 
        log("Sdcard has been checked for adding new contacts");	//are checked and inserted into the database        
        settingUpTheSpinner();
        
        pickContactButton=(Button)findViewById(R.id.buttonAddUsingContacts);
        pickContactButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
//			    startActivityForResult(contactPickerIntent, 1001);
				
				Intent intent=new Intent(MainActivity.this,ContactPickerMulti.class);
				startActivityForResult(intent, 0);
			}
		});
        
        manualGroupMakier=(Button)findViewById(R.id.buttonAddManually);
        manualGroupMakier.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
//				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, Phone.CONTENT_URI);
//			    startActivityForResult(contactPickerIntent, 1001);
				
				Intent intent=new Intent(MainActivity.this,ManualGroupMaker.class);
				startActivityForResult(intent, 1);
			}
		});
        
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
				sendSMS();
			}
		});
    } 
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	// TODO Auto-generated method stub
    	super.onActivityResult(requestCode, resultCode, data);
    	switch(requestCode){
    	case(0):{    		
	    	if(resultCode==Activity.RESULT_OK){
	    		dbPumping(data.getExtras().getString("groupName"),data.getStringArrayListExtra("names"), data.getStringArrayListExtra("phones"));
	    	}
	    	break;
    	}
    	case(1):{
    		if(resultCode==Activity.RESULT_OK){    			
	    		dbPumping(data.getExtras().getString("groupName"),data.getStringArrayListExtra("names"), data.getStringArrayListExtra("phones"));
	    	}
	    	break;
    	}
    	}	    	    	
    }
    
	private void sendSMS(){   		
        String text=messageText.getText().toString();
        ArrayList<String> addrList=db.getPhoneList(selectedGroup);
        if(!text.equals("") && addrList.size()>0){
	    	Intent intent=new Intent(MainActivity.this,OnMessageSent.class);
	    	Bundle b=new Bundle();
	    	b.putString("message text", text);
	    	b.putStringArrayList("phone numbers", addrList);
	    	intent.putExtras(b);
	    	startActivity(intent);
        }
    }
        
    
    private void settingUpTheSpinner(){
    	phoneNumsArraySpinner=(Spinner)findViewById(R.id.phoneNumSpinner);
        log("The number of the added groups are "+Integer.toString(db.getGroupList().size()));
    	ArrayAdapter<String> adaptor=new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_spinner_item,db.getGroupList());
        adaptor.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        phoneNumsArraySpinner.setAdapter(adaptor);
        phoneNumsArraySpinner.setOnItemSelectedListener(new spinnerListener());
        if(db.isFilled())
        	setPhoneCount(0);
        else
        	phoneCountLabel.setText("0\nشماره");
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
    	selectedGroup = phoneNumsArraySpinner.getItemAtPosition(pos).toString();
    	int count= db.getPhoneList(selectedGroup).size();
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
    
    public void dbPumping(String groupName,ArrayList<String> namesList,ArrayList<String> phonesList){
    	if(namesList.size()==phonesList.size()){
    		for (int i=0;i<namesList.size();i++){
    			db.insert(groupName, phonesList.get(i), namesList.get(i)); // here should be revised
    		}
    	}
    	 settingUpTheSpinner();
    }
    
    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
	   db.close();
	   log("going to be destroyed");	
	   super.onDestroy();
	}
    
    private void log(String text){
    	Log.d("Main Activity", text);
    }
}
    
    

