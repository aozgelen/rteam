package metrobotics;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

import javaclient3.PlayerClient;
import javaclient3.structures.PlayerConstants;
import javaclient3.structures.PlayerMsgHdr;
import javaclient3.structures.camera.PlayerCameraData;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * @author Pablo Munoz - Metrobotics
 * This class is used when a Robot is using a Player CameraInterface. 
 * The Thread will start every time a Robot is selected and it will die when 
 * the user selects another robot.
 *
 */
public class CameraThread extends Thread {
	ArrayList<Robot> robots;
	Robot inUse;
	PlayerCameraData playerData;
	PlayerMsgHdr header;
	BufferedImage img;
	VisionDisplay vi; 
	Image image;
	int indexRobot;
	
	CameraThread(Robot bot){// VisionDisplay vi){  //ArrayList<Robot> robots, BufferedImage img){
		//robots = vi.robots;
		//img = vi.img;
		//this.vi = vi;
		header = new PlayerMsgHdr();
		inUse = bot;
		//indexRobot = Robot.getRobotInUse();
	}
	public void run(){
		System.out.println("Thread for robot " + indexRobot + " started");
		int imageCounter = 0;
		
		while(Robot.getCameraThread()){//Robot.visionThreadStarted){ //true) {//indexRobot == Robot.getRobotInUse()){
				if(Gui.debug){
					System.out.println("In VisionThread");
				}
				if(Robot.getRobotInUseVideo()!=-1){
					// System.out.println(Robot.getRobotInUse());
					// inUse = robots.get(Robot.getRobotInUse());
					//System.out.println("Robot vision in use: " + inUse.getName());
					inUse.playerclient.readAll(); // I might not need this. 
					header.setAddr(inUse.cam.getDeviceAddress());
					header.setSeq(0);
					header.setSize(PlayerConstants.PLAYER_CAMERA_IMAGE_SIZE);
					header.setType(PlayerConstants.PLAYER_MSGTYPE_DATA);
					header.setTimestamp(inUse.cam.getTimestamp()); // THis should be changed
					header.setSubtype(PlayerConstants.PLAYER_CAMERA_CODE);
					inUse.cam.readData(header);
					//inUse.playerclient.readAll(); // I might not need this. 
					//inUse.cam.readData(header);
					//playerData = inUse.cam.getData();
					if(inUse.cam.isDataReady()){
								//System.out.println("DataReady");
								playerData = inUse.cam.getData();
								//System.out.println("Bits per pixel: " + playerData.getBpp());
								//System.out.println("Compression: " + playerData.getCompression());
								//System.out.println("Fdiv: " + playerData.getFdiv());
								//System.out.println("Format: " + playerData.getFormat());
								//System.out.println("Height: " + playerData.getHeight());
								//System.out.println("Width: " + playerData.getWidth());
								//System.out.println("Image Count: " + playerData.getImage_count());
								//System.out.println("Has code: " + playerData.hashCode());
								//System.out.println("toString: " + playerData.toString());
								
								// This could be done better by using 
								byte [] imgBytes = playerData.getImage();
								ByteBuffer bufimg = ByteBuffer.wrap(imgBytes);
								//System.out.println(bufimg.order());
								bufimg.order(ByteOrder.LITTLE_ENDIAN);
								//System.out.println(bufimg.order());
								
								bufimg.get(imgBytes);
								//System.out.println("Bytes new order: " + imgBytes);
								
								if(playerData.getCompression()==1){
								
									image = Toolkit.getDefaultToolkit().createImage(imgBytes);
									image = new ImageIcon(image).getImage(); 
									if(image==null){
										System.out.println("Image is null");
									}
									boolean hasAlpha = hasAlpha(image);
									//System.out.println("image has alpha: " + hasAlpha);
									GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment(); 
									try { 
										// Determine the type of transparency of the new buffered image 
										int transparency = Transparency.OPAQUE; 
										if (hasAlpha) { 
											transparency = Transparency.BITMASK; 
										} 
										// Create the buffered image 
										GraphicsDevice gs = ge.getDefaultScreenDevice(); 
										GraphicsConfiguration gc = gs.getDefaultConfiguration();
										img = gc.createCompatibleImage( image.getWidth(null), image.getHeight(null), transparency); //image.getWidth(null), image.getHeight(null), transparency); 
										//System.out.println("Creating image Width: " + image.getWidth(null) + " Height: " + image.getHeight(null));
									} catch (HeadlessException e) { 
											// The system does not have a screen 
									} 
									if (img == null) { 
												// Create a buffered image using the default color model 
										int type = BufferedImage.TYPE_INT_RGB; 
										if (hasAlpha) { 
												type = BufferedImage.TYPE_INT_ARGB; 
										} 
										//System.out.println("image to buffered Image");
										img = new BufferedImage( image.getWidth(null), image.getHeight(null), type);// image.getWidth(null), image.getHeight(null), type); 
									} 
									// Copy image to buffered image 
									//System.out.println("Image is not null");
									
									Graphics2D g2 = img.createGraphics(); 
									// Paint the image onto the buffered image 
									// Graphics2D g2 = (Graphics2D)g;
									g2.drawImage(image, 0, 0, null); 
									g2.dispose(); 	
//									File file = new File(inUse.getName() + "test.jpg");
//								    try {
//										ImageIO.write(img, "jpg", file);
//									} catch (IOException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
									//vi.setImage(img);
									inUse.cameraImage = img;
									//vi.repaint();
									//System.out.println("Vision Updated");
								}
								else if(playerData.getCompression()==0){ // The Aibo image is raw image 
									//System.out.println("Processing image without compression");
									// Try1
									// InputStream in = new ByteArrayInputStream(imgBytes);
									//try {
										// img = ImageIO.read(in);
										img = toImage(playerData.getWidth(), playerData.getHeight(), imgBytes);
										if(img == null){
											//System.out.println("Image is NULL");
										}
										else {
											File file = new File(inUse.getName() + imageCounter + "test.jpg");
										    try {
												ImageIO.write(img, "jpg", file);
											} catch (IOException e) {
												// TODO Auto-generated catch block
												e.printStackTrace();
											}
											inUse.cameraImage = img;
										}
									//} catch (IOException e) {
										// TODO Auto-generated catch block
										//e.printStackTrace();
									//}

									
//									try {
//										img = new BufferedImage( playerData.getWidth(), playerData.getHeight(), BufferedImage.TYPE_BYTE_BINARY);
//										img = ImageIO.read ( new ByteArrayInputStream ( imgBytes ) );
//										
//									} catch (IOException e) {
//										// TODO Auto-generated catch block
//										e.printStackTrace();
//									}
									//vi.setImage(img);
									
								}
					}
				}
						
//				else{
//							//System.out.println("It could be that data is not ready");
//				}
			
//				try {
//					Thread.sleep(30); // This sleep time should come also from the configuration file. 
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//					return;
//				}
		}
		System.out.println("Thread ended for Robot " + indexRobot);
		return;
	}
	public static boolean hasAlpha(Image image) { 
		// If buffered image, the color model is readily available 
		if (image instanceof BufferedImage) { 
			BufferedImage bimage = (BufferedImage)image; 
			return bimage.getColorModel().hasAlpha(); 
		} 
		// Use a pixel grabber to retrieve the image's color model; 
		// grabbing a single pixel is usually sufficient 
		PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false); 
		try { 
			pg.grabPixels(); 
		} catch (InterruptedException e) {
		} 
		// Get the image's color model 
		ColorModel cm = pg.getColorModel(); 
		return cm.hasAlpha(); 
	}
	public static BufferedImage toImage(int w, int h, byte[] data) {
	    DataBuffer buffer = new DataBufferByte(data, w*h);
	 
	    int pixelStride = 3;// JP: it was 4; //assuming r, g, b, skip, r, g, b, skip...
	    int scanlineStride = 3*w; // JP: it was 4*w //no extra padding   
	    int[] bandOffsets = {0, 1, 2}; //r, g, b
	    WritableRaster raster = Raster.createInterleavedRaster(buffer, w, h, scanlineStride, pixelStride, bandOffsets, null);
	 
	    ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_sRGB);
	    boolean hasAlpha = false;
	    boolean isAlphaPremultiplied = false;
	    int transparency = Transparency.OPAQUE;
	    int transferType = DataBuffer.TYPE_BYTE;
	    ColorModel colorModel = new ComponentColorModel(colorSpace, hasAlpha, isAlphaPremultiplied, transparency, transferType);
	 
	    return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
	}
	
	public static BufferedImage toBufferedImage(Image image) {
	    if (image instanceof BufferedImage) {
	        return (BufferedImage)image;
	    }

	    // This code ensures that all the pixels in the image are loaded
	    image = new ImageIcon(image).getImage();

	    // Determine if the image has transparent pixels; for this method's
	    // implementation, see Determining If an Image Has Transparent Pixels
	    boolean hasAlpha = hasAlpha(image);

	    // Create a buffered image with a format that's compatible with the screen
	    BufferedImage bimage = null;
	    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	    try {
	        // Determine the type of transparency of the new buffered image
	        int transparency = Transparency.OPAQUE;
	        if (hasAlpha) {
	            transparency = Transparency.BITMASK;
	        }

	        // Create the buffered image
	        GraphicsDevice gs = ge.getDefaultScreenDevice();
	        GraphicsConfiguration gc = gs.getDefaultConfiguration();
	        bimage = gc.createCompatibleImage(
	            image.getWidth(null), image.getHeight(null), transparency);
	    } catch (HeadlessException e) {
	        // The system does not have a screen
	    }

	    if (bimage == null) {
	        // Create a buffered image using the default color model
	        int type = BufferedImage.TYPE_INT_RGB;
	        if (hasAlpha) {
	            type = BufferedImage.TYPE_INT_ARGB;
	        }
	        bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
	    }

	    // Copy image to buffered image
	    Graphics g = bimage.createGraphics();

	    // Paint the image onto the buffered image
	    g.drawImage(image, 0, 0, null);
	    g.dispose();

	    return bimage;
	}

}