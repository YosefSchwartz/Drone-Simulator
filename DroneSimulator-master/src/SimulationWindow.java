import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SimulationWindow {

	private JFrame frame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					SimulationWindow window = new SimulationWindow();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public SimulationWindow() {
		initialize();
	}

	public static JLabel info_label;
	public static boolean return_home = false;
	boolean toggleStop = true;

	private void initialize() {
		frame = new JFrame();
		frame.setSize(1550,750);
		frame.setTitle("Drone Simulator");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		/*
		 * Stop\Resume
		 */
	
		JButton stopBtn = new JButton("Start/Pause");
		stopBtn.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  if(toggleStop) {
				  	CPU.stopAllCPUS();
				  } else {
				  	CPU.resumeAllCPUS(); }
				  toggleStop = !toggleStop;
			  }
		});
		stopBtn.setBounds(1300, 0, 170, 50);
		frame.getContentPane().add(stopBtn);

		/*
		 * Speeds
		 */
		JButton speedBtn1 = new JButton("speedUp");
		speedBtn1.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { algo1.speedUp();}
		});
		speedBtn1.setBounds(1300, 65, 100, 50);
		frame.getContentPane().add(speedBtn1);
		
		JButton speedBtn2 = new JButton("speedDown");
		speedBtn2.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { algo1.speedDown();}
		});
		speedBtn2.setBounds(1400, 65, 100, 50);
		frame.getContentPane().add(speedBtn2);
		
		/*
		 * Spins
		 */
		// First line
		JButton spinBtn1 = new JButton("spin180");
		spinBtn1.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { algo1.spinBy(180); }
		});
		spinBtn1.setBounds(1300, 130, 80, 50);
		frame.getContentPane().add(spinBtn1);

		JButton spinBtn9 = new JButton("spin135");
		spinBtn9.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) { algo1.spinBy(135); }
		});
		spinBtn9.setBounds(1380, 130, 80, 50);
		frame.getContentPane().add(spinBtn9);

		JButton spinBtn2 = new JButton("spin90");
		spinBtn2.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { algo1.spinBy(90); }
		});
		spinBtn2.setBounds(1460, 130, 80, 50);
		frame.getContentPane().add(spinBtn2);
		//Second line
		JButton spinBtn3 = new JButton("spin60");
		spinBtn3.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  algo1.spinBy(60);
			  }
		});
		spinBtn3.setBounds(1300, 195, 80, 50);
		frame.getContentPane().add(spinBtn3);
		
		JButton spinBtn4 = new JButton("spin45");
		spinBtn4.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  algo1.spinBy(45);
			  }
		});
		spinBtn4.setBounds(1380, 195, 80, 50);
		frame.getContentPane().add(spinBtn4);
		
		JButton spinBtn5 = new JButton("spin30");
		spinBtn5.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  algo1.spinBy(30);
			  }
		});
		spinBtn5.setBounds(1460, 195, 80, 50);
		frame.getContentPane().add(spinBtn5);
		//Third line
		JButton spinBtn6 = new JButton("spin-30");
		spinBtn6.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  algo1.spinBy(-30);
			  }
		});
		spinBtn6.setBounds(1300, 260, 80, 50);
		frame.getContentPane().add(spinBtn6);
		
		JButton spinBtn7 = new JButton("spin-45");
		spinBtn7.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) { algo1.spinBy(-45);}
		});
		spinBtn7.setBounds(1380, 260, 80, 50);
		frame.getContentPane().add(spinBtn7);
		
		JButton spinBtn8 = new JButton("spin-60");
		spinBtn8.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  algo1.spinBy(-60);
			  }
		});
		spinBtn8.setBounds(1460, 260, 80, 50);
		frame.getContentPane().add(spinBtn8);
		
		/*
		 * Toggle real map
		 */
		
		JButton toggleMapBtn = new JButton("toggle Map");
		toggleMapBtn.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  toggleRealMap = !toggleRealMap;
			  }
		});
		toggleMapBtn.setBounds(1300, 325, 100, 50);
		frame.getContentPane().add(toggleMapBtn);
		
		/*
		 * Toggle AI
		 */
		
		JButton toggleAIBtn = new JButton("toggle AI");
		toggleAIBtn.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  toggleAI = !toggleAI;
			  }
		});
		toggleAIBtn.setBounds(1400, 325, 100, 50);
		frame.getContentPane().add(toggleAIBtn);
		
		/*
		 * RETURN TO HOME
		 */
		

		JButton returnBtn = new JButton("Return Home");
		returnBtn.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e) {
				  return_home = !return_home;
				  algo1.calculate_alpha = true;
			  }
		});
		returnBtn.setBounds(1300, 400, 150, 50);
		frame.getContentPane().add(returnBtn);
		
		JButton Graph = new JButton("Open Graph");
		Graph.addActionListener(new ActionListener() {
			  public void actionPerformed(ActionEvent e)
			  {
				  algo1.mGraph.drawGraph();
			  }
		});
		Graph.setBounds(1450, 400, 100, 50);
		frame.getContentPane().add(Graph);
		
		/*
		 * Info label 
		 */

		info_label = new JLabel();
		info_label.setBounds(1300, 475, 300, 200);
		frame.getContentPane().add(info_label);
		
		/*
		 * Info label 
		 */
		
		
		info_label2 = new JLabel();
		info_label2.setBounds(1300, 400, 300, 200);
		frame.getContentPane().add(info_label2);

		infoTime = new JLabel();
		infoTime.setBounds(1300, 550, 300, 200);
		frame.getContentPane().add(infoTime);
		
		main();
	}
	public JLabel info_label2;
	public JLabel infoTime;
	public static boolean toggleRealMap = true;
	public static boolean toggleAI = false;
	
	public static AutoAlgo1 algo1;
	

	public void main() {
		int map_num = 4 ;
		Point[] startPoints = {
				new Point(100,50),
				new Point(50,60),
				new Point(73,68),
				new Point(84,73),
				new Point(92,100)};
		
		Map map = new Map("C:\\Users\\Yosef\\Desktop\\DroneSimulator-master\\Maps\\p1" + map_num + ".png",startPoints[map_num-1]);
		
		algo1 = new AutoAlgo1(map);
		
		Painter painter = new Painter(algo1);
		painter.setBounds(0, 0, 2000, 2000);
		frame.getContentPane().add(painter);
		
		CPU painterCPU = new CPU(200,"painter"); // 60 FPS painter
		painterCPU.addFunction(frame::repaint);
		painterCPU.play();
		
		algo1.play();
		
		CPU updatesCPU = new CPU(60,"updates");
		updatesCPU.addFunction(algo1.drone::update);
		updatesCPU.play();
		
		CPU infoCPU = new CPU(6,"update_info");
		infoCPU.addFunction(this::updateInfo);
		infoCPU.play();
	}
	
	public void updateInfo(int deltaTime) {
		info_label.setText(algo1.drone.getInfoHTML());
		info_label2.setText("<html>" + String.valueOf(algo1.counter) + " <BR>Risky straight:" + String.valueOf(algo1.riskyStraight) +
				" <BR>Risky right:" + String.valueOf(algo1.riskyRight) + " <BR>Risky left:" + String.valueOf(algo1.riskyLeft) +
				"<BR>" + String.valueOf(algo1.risky_dis) + "</html>");
		infoTime.setText("<html>" + "Return Home: " + getReturnHomeMode() +
				"<BR>Time left: " + getTimeLeft() +
				"<BR>Battery: " + getBatteryPercent() +"%" + "</html>");
		
	}

	private String getReturnHomeMode() {
		if(return_home)
			return "ON";
		return "OFF";
	}

	private String getBatteryPercent() {
		if (algo1.startTime > 0){
			long curr = algo1.nowTime - algo1.startTime;
			double batteryLost = curr/480000.0;
			double batteryLeft = (1-batteryLost)*100;
			return String.format("%.2f", batteryLeft);

		}else{
			return "Start AI before";
		}
	}

	private String getTimeLeft() {
		if (algo1.startTime > 0){
			long curr = 480000 - (algo1.nowTime - algo1.startTime);
			int min = (int) curr/60000 ;
			int sec = (int) (curr%60000)/1000;
			return "" + min + ":" + sec;
		}else{
			return "Start AI before";
		}
	}
}
