package mrhs.ce.DenseSms;

public interface Commons {
	public final Integer OPERATION_INSERT_FAILED = -333;
	
	//Message condition
	public final boolean MESSAGE_CONDITION=true;
	public final Integer MESSAGE_PENDING=0;
	public final Integer MESSAGE_SENT=1;
	public final Integer MESSAGE_DELIVERED=2;
	public final Integer MESSAGE_FAILED=9;
	
	//Response condition when the users respond to the message
	public final boolean RESPONSE_CONDITION=false;
	public final Integer RESPONSE_NOT_ANSWERED=0;
	public final Integer RESPONSE_ACCEPTED=1;
	public final Integer RESPONSE_INVALID=8;
	public final Integer RESPONSE_REJECTED=9;
	
	// Interval between messages (ms)
	
	public final Integer MESSAGE_INTERVAL = 5000;
}
