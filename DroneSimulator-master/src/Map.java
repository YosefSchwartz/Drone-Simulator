import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


public class Map{
	private boolean[][] map;
	Drone drone;
	Point drone_start_point;

	public Map(String path,Point drone_start_point) {
		try {
			this.drone_start_point = drone_start_point;
			BufferedImage img_map = ImageIO.read(new File(path));
			this.map = render_map_from_image_to_boolean(img_map);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private boolean[][] render_map_from_image_to_boolean(BufferedImage map_img) {
		int w = (int) (map_img.getWidth() * 0.925);
		int h = (int) (map_img.getHeight() * 0.95);
		boolean[][] map = new boolean[w][h];
		for (int y = 1; y < h-1 ; y++) {
			for (int x = 1; x < w-1 ; x++) {
				int clr = map_img.getRGB(x, y);
				int red = (clr & 0x00ff0000) >> 16;
				int green = (clr & 0x0000ff00) >> 8;
				int blue = clr & 0x000000ff;
				if (red != 0 && green != 0 && blue != 0) { // think black
					map[x][y] = true;
				}
			}
		}
//		for (int i = 0; i < w + 2; i++){
//			map[i][0] = false;
//			map[i][h + 1] = false;
//		}
//		for(int i = 0; i<h+2; i++) {
//			map[0][i] = false;
//			map[w+1][i] = false;
//		}
		return map;
	}
	
	boolean isCollide(int x,int y) {
		return !map[x][y];
	}
	
	public void paint(Graphics g) {
		Color c = g.getColor();
		g.setColor(Color.GRAY);
		for(int i=0;i<map.length;i++) {
			for(int j=0;j<map[0].length;j++) {
				if(!map[i][j])  {
					g.drawLine(i, j, i, j);
				}
			}
		}
		g.setColor(c);
	}

}

