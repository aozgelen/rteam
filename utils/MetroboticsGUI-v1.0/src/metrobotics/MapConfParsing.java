package metrobotics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

// TODO: THis should be able to set: Marker, Wall, Objects?

public class MapConfParsing {
	boolean isSizeSet = false;
	ArrayList<Object> mapObjects;
	Dimension size;
	MapConfParsing(){
		mapObjects = new ArrayList<Object>();
		size = new Dimension(0,0);
		try {
			BufferedReader reader = new BufferedReader(new FileReader("map.conf"));
			String line = reader.readLine();
			boolean skip = false;
			while(line != null){
				// Skip comments 
				for(int i=0; i<line.length(); i++){
					if(line.charAt(i)==' '){
						continue;
					}
					if(line.charAt(i)=='\n'){
						skip = true;
						break;
					}
					//System.out.print("Checking...");
					if(line.charAt(i)=='/' && line.charAt(i+1)=='/'){
						//line = reader.readLine();
						i = 0;
						//System.out.println("Here");
						skip = true;
					}
					else{
						break;
					}
				}
				if(line.length() == 0){
					skip = true;
				}
				//System.out.println("Skip = " + skip);
				if(skip == false){
					// System.out.println(line);
					Scanner scan = new Scanner(line);
					if(scan.hasNext() && !isSizeSet){
						if(scan.next().equalsIgnoreCase("size") ){
							setGridSize(scan);
							isSizeSet = true;
						}
						else{
							System.out.println("No Size specified. Please check your map.conf file");
						}
					}
					if(scan.hasNext() && isSizeSet){
						// Process Other objects in the map. Check if size of Grid has been defined
						String next = scan.next();
						if(next.equalsIgnoreCase("marker")){
							processMarker(scan);
						}
						else if(next.equalsIgnoreCase("wall")){
							processWall(scan);
						}
					}
					// Send to process line. 
				}
				line = reader.readLine();
				skip = false;
				
			}
			reader.close();
			
			
		} catch (FileNotFoundException e) {
			System.out.println("map.conf is not in the folder");
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	public ArrayList<Object> getMapObjects(){
		return mapObjects;
	}
	public Dimension getSize(){
		return size; 
	}
	private void setGridSize(Scanner scan) {
		double width, height;
		if(scan.hasNext()){
			width = Double.parseDouble(scan.next());
			if(scan.hasNext()){
				height = Double.parseDouble(scan.next());
			}
			else
				size.setSize(width, 0);
		}
		else{
			size.setSize(0, 0);
		}
	}

	private void processMarker(Scanner scan) {
		// System.out.println("Processing Marker");
		String name;
		double xPos, yPos;
		// How many colors and how is going to be the format for this?
		// for now:
		Color primary = new Color(129,129,129);
		Color secondary = new Color(50, 50, 50);
		if(scan.hasNext()){
			name = scan.next();
			if(scan.hasNext()){
				xPos = Double.parseDouble(scan.next());
				if(scan.hasNext()){
					yPos = Double.parseDouble(scan.next());	
					Marker marker = new Marker(name, primary, secondary, xPos, yPos);
					mapObjects.add(marker);
					// System.out.println("Marker added to list of objects");
				}
				else{
					System.out.println("Marker disregarded because of wrong information");
					return;
				}
			}
			else{
				System.out.println("Marker disregarded because of wrong information");
				return;
			}
		}
		else{
			System.out.println("Marker disregarded because of wrong information");
		}
	}
	private void processWall(Scanner scan) {
		// System.out.println("Processing Wall");
		String name;
		double xPos, yPos, width, height;
		// Are we going to have different colors in the walls?
		// For now;
		Color color = new Color(200, 200, 200);
		if(scan.hasNext()){
			name = scan.next();
			if(scan.hasNext()){
				xPos = Double.parseDouble(scan.next());
				if(scan.hasNext()){
					yPos = Double.parseDouble(scan.next());	
					if(scan.hasNext()){
						width = Double.parseDouble(scan.next()) - xPos;
						if(width< 1){
							width = 10;
						}
						
						
						if(scan.hasNext()){
							height = Double.parseDouble(scan.next());
							if(height <= yPos){
								height = yPos - height;
							}
							else if(height >= yPos){
								double temp = yPos;
								yPos = height;
								height = temp;
								
								height = yPos - height;
							}
							//System.out.println(height);
							if(height < 1){
								height = 10;
							}
							Wall wally = new Wall(color, new Rectangle((int)xPos, (int)yPos, (int)width, (int)height), name);
							mapObjects.add(wally);
							// System.out.println("Wall added to list of objects");
						}
						else{
							System.out.println("Wall disregarded because of wrong information");
							return;
						}
					}
					else{
						System.out.println("Wall disregarded because of wrong information");
						return;
					}
				}
				else{
					System.out.println("Wall disregarded because of wrong information");
					return;
				}
			}
			else{
				System.out.println("Wall disregarded because of wrong information");
				return;
			}
		}
		else{
			System.out.println("Wall disregarded because of wrong information");
		}
				
	}

	// For testing purposes
	public static void main(String []args){
		MapConfParsing parser = new MapConfParsing();
	}
}
