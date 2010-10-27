package metrobotics;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import javax.swing.ImageIcon;

/**
 * @author Pablo Munoz
 *
 */
public class GuiUtils {
	// Resize images to fit in ImageIcon
	public static ImageIcon resizeJP(Image src, int width, int height) {
        int type = BufferedImage.TYPE_INT_RGB;
        BufferedImage dst = new BufferedImage(width, height, type);
        Graphics2D g2 = dst.createGraphics();
        g2.drawImage(src, 0, 0, width, height, null);
        g2.dispose();
        return new ImageIcon(dst);
    }
	
	private static int swapByteOrderInt(int toSwap) {
		byte [] intBytes = intToByteArray(toSwap);
		ByteBuffer bufimg = ByteBuffer.wrap(intBytes);
		bufimg.order(ByteOrder.LITTLE_ENDIAN);
		//System.out.println(bufimg.order());
		bufimg.get(intBytes);
		
		int fromByte = 0;
		for (int i = 0; i < 4; i++)
		{
			int n = (intBytes[i] < 0 ? (int)intBytes[i] + 256 : (int)intBytes[i]) << (8 * i);
			//System.out.println(n);
			fromByte += n;
		}
		//System.out.println(fromByte);
		
		return fromByte;
	}
	
	public static byte[] intToByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }

	

}
