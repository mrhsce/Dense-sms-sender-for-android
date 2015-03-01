package mrhs.ce.DenseSms.MessageLog;

import mrhs.ce.DenseSms.R;
import mrhs.ce.DenseSms.R.layout;
import mrhs.ce.DenseSms.R.menu;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class MessageLogActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_log);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.message_log, menu);
		return true;
	}

}
