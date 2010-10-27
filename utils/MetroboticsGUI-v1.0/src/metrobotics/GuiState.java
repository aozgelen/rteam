package metrobotics;


/**
 * @author Pablo Munoz
 *
 */
public class GuiState extends Thread {

	long time1, time2;
	boolean checkTime;
	GuiState(){
		time2 = System.currentTimeMillis() + GUIConstants.STATE_UPDATE_INTERVAL;
		Gui.setGUIState(GUIConstants.STATE_INIT);
	}
	public void run(){
		while(Gui.getGUIState() != GUIConstants.STATE_QUIT){
			switch(Gui.getGUIState()){
				case GUIConstants.STATE_INIT:
					if(Gui.debug){
						System.out.println("GUISTATE INIT");
					}
					Gui.serverComm.init();
					Gui.setGUIState(GUIConstants.STATE_ACK);
					break;
				case GUIConstants.STATE_ACK:
					if(Gui.debug){
						System.out.println("GUISTATE ACK");
					}
					if(Gui.serverComm.ack()){
						Gui.setGUIState(GUIConstants.STATE_GUI_WAIT);
						//Gui.serverComm.writeStream("IDENT");
					}
					else
						Gui.setGUIState(GUIConstants.STATE_INIT);
					break;
				case GUIConstants.STATE_GUI_WAIT:
//					if(Gui.debug){
//						System.out.println("GUISTATE WAIT");
//					}
					Gui.serverComm.check4Msg();
					break;
			}
			time1 = System.currentTimeMillis();
			if(time1  > time2 + GUIConstants.STATE_UPDATE_INTERVAL)
				checkTime = true;
			if(checkTime){
				time2 = System.currentTimeMillis(); 
				Gui.serverComm.askPose(-1);
				checkTime = false;
			}
			
			try {
				Thread.sleep(GUIConstants.STATE_UPDATE_SLEEP_TIME); // This sleep time should come also from the configuration file. 
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}	
		}
	}
}
