package amstelhaege;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.*;

import java.net.URL;

import javax.imageio.ImageIO;

public class Graphics  extends JComponent implements ChangeListener {
	
	final int HOUSE=1;
	final int WATER=2;
	final int CLEARANCE=3;
	final int NOTHING=0;
	
	JFrame frame;
	Polder earth;
	int[][] matrix;
	BufferedImage img;
	int width;
	int height;

	
	BufferedImage newImage(int[][] matrix) {
		//create random image pixel by pixel
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		int a, r, g, b;
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
	//			if(matrix[y][x].poison){
	//				a = 100;
	//			}else{
	//				a = 255;
	//			}
				a = 255;
				int code = matrix[x][y];
				if(code == HOUSE) {
					r = 155;   //red
					g = 22;   //green
					b = 11;   //blue
					//gives red-ish
				} else if (code == NOTHING) {
					r = 0;   //red
					g = 104;   //green
					b = 10;   //blue
					//gives grass
				} else if (code == WATER) {
					r = 100;   //red
					g = 100;   //green
					b = 250;   //blue
					//gives blueish
				}  else if (code == CLEARANCE) {
					r = 174;   //red
					g = 137;   //green
					b = 118;   //blue
				}	else {
					r = 255;   //red
					g = 255;   //green
					b = 255;   //blue
					//gives white
				}
				int p = (a<<24) | (r<<16) | (g<<8) | b; //pixel
				img.setRGB(x, y, p);
			}
		}
		return img;
	}

	
	
    JPanel gui;
    /**
     * Displays the image.
     */
    JLabel imageCanvas;
    Dimension size;
    double scale = 1.0;
    BufferedImage image;

    public Graphics(JFrame frame) {
    	this.frame=frame;
    	earth= new Polder();
		width=earth.POLDER_WIDTH;
		height=earth.POLDER_HEIGHT;
		matrix= earth.world_matrix;
		image = newImage(matrix);
		
        size = new Dimension(10, 10);
        setBackground(Color.black);
    }


    public void setImage(Image image) {

        imageCanvas.setIcon(new ImageIcon(image));
    }

    public void initComponents() {
        if (gui == null) {
            gui = new JPanel(new BorderLayout());
            gui.setBorder(new EmptyBorder(5, 5, 5, 5));
            imageCanvas = new JLabel();
            JPanel imageCenter = new JPanel(new GridBagLayout());
            imageCenter.add(imageCanvas);
            JScrollPane imageScroll = new JScrollPane(imageCenter);
            imageScroll.setPreferredSize(new Dimension(300, 100));
            gui.add(imageScroll, BorderLayout.CENTER);
        }
    }

    public Container getGui() {
        initComponents();
        return gui;
    }

   
    public void stateChanged(ChangeEvent e) {
        int value = ((JSlider) e.getSource()).getValue();
        scale = value / 100.0;
        paintImage();
    }
	

    protected void paintImage() {
        int w = getWidth();
        int h = getHeight();
        int imageWidth = image.getWidth();
        int imageHeight = image.getHeight();
        BufferedImage bi = new BufferedImage(
                (int)(imageWidth*scale), 
                (int)(imageHeight*scale), 
                image.getType());
        Graphics2D g2 = bi.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        double x = (w - scale * imageWidth) / 2;
        double y = (h - scale * imageHeight) / 2;
        AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
        at.scale(scale, scale);
        g2.drawRenderedImage(image, at);
        setImage(bi);
    }

    public Dimension getPreferredSize() {
        int w = (int) (scale * size.width);
        int h = (int) (scale * size.height);
        return new Dimension(w, h);
    }
    
    void refreshImage(ActionEvent e){
  //  	matrix = earth.iterate();
		// img = newImage(matrix);
		// image=img;
	 //    frame.setTitle("Population: "+earth.pop+"\t Generation :"+earth.gen);
	
		// int w = getWidth();
  //       int h = getHeight();
  //       int imageWidth = img.getWidth();
  //       int imageHeight = img.getHeight();
  //       BufferedImage bi = new BufferedImage(
  //               (int)(imageWidth*scale), 
  //               (int)(imageHeight*scale), 
  //               img.getType());
  //       Graphics2D g2 = bi.createGraphics();
  //       g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
  //               RenderingHints.VALUE_ANTIALIAS_ON);
  //       double x = (w - scale * imageWidth) / 2;
  //       double y = (h - scale * imageHeight) / 2;
  //       AffineTransform at = AffineTransform.getTranslateInstance(0, 0);
  //       at.scale(scale, scale);
  //       g2.drawRenderedImage(img, at);
  //       setImage(bi);
    }
    JSlider getControl() {
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 500, 100);
        slider.setMajorTickSpacing(50);
        slider.setMinorTickSpacing(25);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.addChangeListener(this);
        return slider;
    }
    void startGraphicSimulation(){
    	Timer timerOne = new Timer(1, this::refreshImage);
		timerOne.start();
    }
    
   
}
