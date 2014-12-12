Dense-sms-sender-for-android
============================

This application automatically send the determined text to the phone numbers inside a text file

**********************************************

Things to do ->
	First phase:
	- ✔ disable screen rotation
	- ✔ check to see if there exist any sd card
	- ✔ if sdcard exists make a directory inside the sdcard else inside the internal memory called "شماره های مخاطبان"
	- ✔ The program checks the directory and make a combo box filled with the names of the text files inside the directory
	- ✔ when any text file is selected the count of the phone numbers inside it is shown in a small label adjacent to it
	- ✔ when the text of the message editText is changed the number of the messages is shown alike phone numbers
	- ✔ Based on the size of the message the application shows the number of sent and delivered messages
	- ✔ when the send button is pressed the edit text becomes empty and the text of the previous message will be shown in a new activity
	and in it should be two small numbers indicating the count of sent and delivered
	- as long as the number of the sent and delivered messages hasn't reached the maximum there should be a small animation
	running for each
	- ✔ Take care for when the message is null or no phone number is choosed
	- Use branched weights for the gui
	Second phase:
	- take care of the rotation
	- also add the database for storing numbers and groups
	- design a mechanism for creating groups from contacts and inserting (number,name) tupple
	- a mechanism for edditing the groups and deleting them	
	-Show exactly for which phone numbers message has been send and also for which has been delivered
	- 
