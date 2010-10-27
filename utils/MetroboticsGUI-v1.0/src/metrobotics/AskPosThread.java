package metrobotics;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;


/**
 * @author Pablo Munoz
 * THIS CLASS IS NOT IN USE. I was using it before, but with the addition of GuiState, I
 * stopped using it. 
 * I will delete it in a future cleaning. PLEASE DISREGARD IT.
 * Pablo
 */
public class AskPosThread extends Thread {
//	Socket socPos2D = null;	
//	OutputStream outStPos2d = null;
//	InputStream inStPos2d = null;
	DataOutputStream daoutPos2d = null;
	DataInputStream dataInPos2d = null;

	public AskPosThread(DataOutputStream daoutPos2d2,
			DataInputStream dataInPos2d2) {
		this.daoutPos2d = daoutPos2d2;
		this.dataInPos2d = dataInPos2d2;
	}

	public void run(){
		while(true){
			String toServer = " ASKPOSE" + '\0';
			try {
				daoutPos2d.write(toServer.getBytes(), 0, toServer.length());
				daoutPos2d.flush();
			} catch (IOException e) {
				e.printStackTrace();
			} 
			// TODO: MISSING RESPONSE FROM SERVER
			
			try {
				Thread.sleep(5000); // This sleep time should come also from the configuration file. 
			} catch (InterruptedException e) {
				e.printStackTrace();
				return;
			}
		}
		
		
	}

}
