package mrhs.ce.smstest;

import java.util.ArrayList;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ManualArrayAdaptor extends ArrayAdapter<String> {
	
	EditGroupActivity context;
	final int EDIT=1;
	final int MAKE=0;
	int mode;
	
	public ManualArrayAdaptor(EditGroupActivity context, ArrayList<String> nameList,int mode) {
		super(context, R.layout.edit_group_item, nameList);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.mode=mode;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		
		if(convertView==null){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.edit_group_item, parent, false);
		}		
		EditText nameEdit=(EditText) convertView.findViewById(R.id.editTextName);
		EditText phoneEdit=(EditText)convertView.findViewById(R.id.editTextPhone);
		ImageButton deleteButton=(ImageButton)convertView.findViewById(R.id.deleteButton);
		if(mode==MAKE)
			deleteButton.setVisibility(View.GONE);
		TextView numberLabel=(TextView)convertView.findViewById(R.id.numberLabel);
		
		nameEdit.setText(context.nameList.get(context.nameList.size()-1-position));
		phoneEdit.setText(context.phoneList.get(context.phoneList.size()-1-position));
		numberLabel.setText(Integer.toString(context.phoneList.size()-position));
		log("All views are found");
		
		deleteButton.requestFocus();
		deleteButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				context.nameList.remove(context.nameList.size()-1-position);
				context.phoneList.remove(context.phoneList.size()-1-position);
				context.chkDeleteAll();
				context.setAdaptor(mode);
			}
		});
		
		nameEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				context.nameList.set(context.nameList.size()-1-position, arg0.toString());
				context.chkDeleteAll();
				log("name edit text has been changed");
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
		phoneEdit.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				context.phoneList.set(context.phoneList.size()-1-position, arg0.toString());
				context.chkDeleteAll();
				log("phone edit text has been changed");
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
		
		
		log("All values are set");	
		
		return convertView;
	}
	
	private void log(String text){
    	Log.d("Manual Array Adaptor", text);
    }

}
