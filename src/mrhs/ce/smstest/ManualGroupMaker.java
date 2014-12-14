package mrhs.ce.smstest;

import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ManualGroupMaker extends Activity {
	public ArrayList<String> nameList;
	public ArrayList<String> phoneList;
	ListView list;
	Button delete,add,done;
	EditText groupName;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual_group_maker);
		list=(ListView)findViewById(R.id.manualList);
		delete=(Button)findViewById(R.id.buttonDelete);
		add=(Button)findViewById(R.id.buttonAdd);
		done=(Button)findViewById(R.id.buttonAccept);
		groupName=(EditText)findViewById(R.id.groupNameEdit);
		
		nameList=new ArrayList<String>();
		phoneList=new ArrayList<String>();		
		
		addField(3);
		
		list.setAdapter(new ManualArrayAdaptor(this, nameList));
		
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addField(1);
				list.setAdapter(new ManualArrayAdaptor(ManualGroupMaker.this, nameList));
			}
		});
		
		delete.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				addField(-1);
				list.setAdapter(new ManualArrayAdaptor(ManualGroupMaker.this, nameList));
			}
		});
		done.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(groupName.getText().toString().equals(""))
					Toast.makeText(ManualGroupMaker.this, "لطفا گروه را نامگذاری کنید", Toast.LENGTH_SHORT).show();
				else
					chkList(groupName.getText().toString());
			}
		});
		
		
	}
	
	private void chkList(String group){
		ArrayList<String> tmpName=new ArrayList<String>();
		ArrayList<String> tmpphone=new ArrayList<String>();
		for(int i=0;i<nameList.size();i++){
			if(/*!nameList.get(i).equals("") && */phoneList.get(i).matches("(0)[0-9]{10}")){
				if(nameList.get(i).equals(""))
					tmpName.add(null);
				else
					tmpName.add(nameList.get(i));
				tmpphone.add(phoneList.get(i));
			}
		}
		if(tmpName.size()>0){
			log(Integer.toString(tmpphone.size())+" contacts has been accepted");
			log("Group name is "+group);
			Intent intent=new Intent();
			intent.putStringArrayListExtra("names", tmpName);
			intent.putStringArrayListExtra("phones", tmpphone);
			intent.putExtra("groupName", group);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
		else
			Toast.makeText(ManualGroupMaker.this, "لطفا مشخصات را به خوبی وارد کنید", Toast.LENGTH_SHORT).show();
	}

	private int addField(int freq){
		
		if(freq==-1 && phoneList.size()>0){
			nameList.remove(nameList.size()-1);
			phoneList.remove(phoneList.size()-1);
		}
		else{
			for(int i=0;i<freq;i++){
				nameList.add("");
				phoneList.add("");
			}
		}
		return phoneList.size();
	}
	
	private void log(String text){
    	Log.d("Manual group maker", text);
    }

}
