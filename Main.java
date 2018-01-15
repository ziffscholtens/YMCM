package amstelhaege;

import javax.swing.JFrame;

public class Main {

	static void run_with_Graphics(){

        JFrame frame = new JFrame();
		Graphics app = new Graphics(frame);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(app.getGui());
        app.setImage(app.image);
        frame.getContentPane().add(app.getControl(), "Last");
        frame.setSize(app.width, app.height);
        frame.setLocation(200, 200);
        frame.setVisible(true);
        app.startGraphicSimulation();
	}
	
	public static void main(String[] args) {
		run_with_Graphics();
	}

}