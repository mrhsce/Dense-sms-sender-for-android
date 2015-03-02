package mrhs.ce.DenseSms.Database;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import mrhs.ce.DenseSms.Commons;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class OperationDatabaseHandler {
	private static  int DATABASE_VERSION=2;
	private static final String DATABASE_NAME = "denseSMS";
	private static final String OPERATION_TABLE_NAME = "operations";
	private static final String STATUS_TABLE_NAME = "status";
	
	private DbHelper dbHelper;
	private  SQLiteDatabase db;
	
	// Necessary functions (DDL)
	public OperationDatabaseHandler(Context ctx){
			dbHelper=new DbHelper(ctx);
	}
		
	public OperationDatabaseHandler open() throws SQLException{
			db=dbHelper.getWritableDatabase();
			return this;
	}
		
	public void close(){
			dbHelper.close();
	}
	
		
	public Integer insertOperation(String message){ // if successful returns id else returns fail code
		ContentValues values=new ContentValues();
		values.put("msgtxt", message);		
		try{
			if(db.insert(OPERATION_TABLE_NAME, null, values)>0){	
				Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"oprId"}, null, null, null, null, "oprId desc");
				cursor.moveToFirst();
				return cursor.getInt(0);
			}
				else
					return Commons.OPERATION_INSERT_FAILED;
			}catch(Exception e){
			e.printStackTrace();
			log("Error inserting values to the operation databse");
			return Commons.OPERATION_INSERT_FAILED;
		}
	}
	
	public boolean insertStatus(Integer oprId,String groupName,String phoneNum,String name){
		ContentValues values=new ContentValues();
		values.put("oprId",oprId);
		values.put("groupName",groupName);
		values.put("phoneNum", phoneNum);
		values.put("status",Commons.MESSAGE_PENDING);
		values.put("acceptance",Commons.RESPONSE_NOT_ANSWERED);
		if(name!=null)
			values.put("name", name);
		try{
			return db.insert(STATUS_TABLE_NAME, null, values)>0;
		}catch(Exception e){
			e.printStackTrace();
			log("Error inserting values to the status databse");
			return false;
		}
	}
	
	public Cursor getAllOperations(){
		Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"oprId","msgtxt","created_at"}, null, null, null, null, "created_at desc");
		if(cursor != null){
			cursor.moveToFirst();
			return cursor;
		}return null;
	}
	
	public String getOperationText(Integer oprId){
		Cursor cursor=db.query(OPERATION_TABLE_NAME, new String[]{"msgtxt"}, "oprId = "+Integer.toString(oprId), null, null, null, null);
		if(cursor != null){
			if(cursor.moveToFirst())
				return cursor.getString(0);
			else
				return null;
		}return null;
	}
	
	public boolean updateStatus(Integer id,boolean conditionType,int condition){
		ContentValues values=new ContentValues();
		if(conditionType==Commons.MESSAGE_CONDITION){			
			values.put("status",condition);			
		}
		else{
			values.put("acceptance",condition);
		}
		// Getting the current time and formating it
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
		Date date = new Date();
			        
		values.put("stat_at",dateFormat.format(date));
		return db.update(STATUS_TABLE_NAME, values, "id = "+Integer.toString(id), null)>0;
		
	}
	
	boolean ascending = true;
	public Cursor getAllStatusOfOperation(Integer oprId,boolean order){
		Cursor cursor;
		if(order == ascending)
			cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId","groupName","phoneNum","name","status","acceptance","stat_at"}, "oprId = "+Integer.toString(oprId), null, null, null, "id asc");
		else
			cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId","groupName","phoneNum","name","status","acceptance","stat_at"}, "oprId = "+Integer.toString(oprId), null, null, null, "stat_at desc");		if(cursor != null){
			cursor.moveToFirst();
			return cursor;
		}return null;		
	}	
	
//	public Cursor getAllDetailsOfStatus(Integer id){
//		Cursor cursor;
//		if(type == ascending)
//			cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId","groupName","phoneNum","name","status","acceptance","stat_at"}, "id = "+Integer.toString(id), null, null, null, "id asc");
//		else
//			cursor=db.query(STATUS_TABLE_NAME, new String[]{"id","oprId","groupName","phoneNum","name","status","acceptance","stat_at"}, "id = "+Integer.toString(id), null, null, null, "stat_at desc");				
//		if(cursor != null){
//			cursor.moveToFirst();
//			return cursor;
//		}return null;		
//	}
	
	public boolean deleteOperation(Integer oprId){
		// Delete the operation as well as all the related status
		Cursor cursor=getAllStatusOfOperation(oprId,true);
		do{
			db.delete(OPERATION_TABLE_NAME, "id = "+Integer.toString(cursor.getInt(0)), null);
		}while(cursor.moveToNext());
		return db.delete(OPERATION_TABLE_NAME, "oprId = "+oprId, null)>0;
	} 	
	
	private static class DbHelper extends SQLiteOpenHelper{
		
		public DbHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			log("The database has been initialized");
		}
	
		@Override
		public void onCreate(SQLiteDatabase db) {
			// TODO Auto-generated method stub
			String OPERATION_CREATE_DATABASE="CREATE TABLE IF NOT EXISTS "+OPERATION_TABLE_NAME+ 
					" ( oprId INTEGER PRIMARY KEY," +
						"msgtxt  text NOT NULL," +
							"created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP)";
			String STATUS_CREATE_DATABASE="CREATE TABLE IF NOT EXISTS "+STATUS_TABLE_NAME+
											"(id INTEGER PRIMARY KEY," +
											"oprId int(10) NOT NULL," +
											"groupName varchar(40) NOT NULL," +
											"name varchar(30)," +
											"phoneNum varchar(13) NOT NULL," +
											"status int(1) NOT NULL," +
											"acceptance int(1),"+
											"stat_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP," +
											"foreign key(oprId) REFERENCES " +OPERATION_TABLE_NAME+
											"(oprId))";
					
			db.execSQL(OPERATION_CREATE_DATABASE);
			db.execSQL(STATUS_CREATE_DATABASE);
			log("Both Databse schematics has been craeted");
		}
	
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			// TODO Auto-generated method stub
			db.execSQL("DROP TABLE IF EXISTS "+OPERATION_TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS "+STATUS_TABLE_NAME);
			onCreate(db);
		}	
		
	}
	private static void log(String message){
		if(Commons.SHOW_LOG)
			Log.d("operationsDbHelper",message);
	}
}
