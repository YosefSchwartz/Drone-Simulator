import java.awt.*;
import java.time.Clock;
import java.util.ArrayList;


public class AutoAlgo1 {

	String mode = "";
	int spin_by = 0;

	Clock clock = Clock.systemDefaultZone();
	long startTime = 0;
	long nowTime;

	int map_size = 3000;
	enum PixelState {blocked,explored,unexplored,visited}
	PixelState map[][];
	Drone drone;
	Point droneStartingPoint;

	ArrayList<Point> points;


	int isRotating;
	ArrayList<Double> degrees_left;
	ArrayList<Func> degrees_left_func;

	boolean isSpeedUp = false;

	Graph mGraph = new Graph();

	CPU ai_cpu;
	public AutoAlgo1(Map realMap) {
		degrees_left = new ArrayList<>();
		degrees_left_func =  new ArrayList<>();
		points = new ArrayList<Point>();

		drone = new Drone(realMap);
		drone.addLidar(0); // Straight
		drone.addLidar(90); // Right
		drone.addLidar(-90); // Left


		initMap();

		isRotating = 0;
		ai_cpu = new CPU(200,"Auto_AI");
		ai_cpu.addFunction(this::update);
	}

	public void initMap() {
		map = new PixelState[map_size][map_size];
		for(int i=0;i<map_size;i++) {
			for(int j=0;j<map_size;j++) {
				map[i][j] = PixelState.unexplored;
			}
		}

		droneStartingPoint = new Point(map_size/2,map_size/2);
	}

	public void play() {
		drone.play();
		ai_cpu.play();
	}

	public void update(int deltaTime) {
		nowTime = clock.millis();
		if(startTime > 0) {
			if (nowTime - startTime > 200000) {
				SimulationWindow.return_home = true;
			}
			if (nowTime - startTime > 480000) {
				CPU.stopAllCPUS();
			}
		}

		updateVisited();
		updateMapByLidars();

		ai(deltaTime);


		if(isRotating != 0) {
			updateRotating(deltaTime);
		}
		if(isSpeedUp) {
			drone.speedUp(deltaTime);
		} else {
			drone.slowDown(deltaTime);
		}

	}

	public void speedUp() {
		isSpeedUp = true;
	}

	public void speedDown() { isSpeedUp = false;}

	public void updateMapByLidars() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);

		for(int i=0;i<drone.lidars.size();i++) {
			Lidar lidar = drone.lidars.get(i);
			double rotation = drone.getGyroRotation() + lidar.degrees;
			for(int distanceInCM=0;distanceInCM < lidar.current_distance;distanceInCM++) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, distanceInCM);
				setPixel(p.x,p.y,PixelState.explored);
			}

			if(lidar.current_distance > 0 && lidar.current_distance < WorldParams.lidarLimit - WorldParams.lidarNoise) {
				Point p = Tools.getPointByDistance(fromPoint, rotation, lidar.current_distance);
				setPixel(p.x,p.y,PixelState.blocked);
				//fineEdges((int)p.x,(int)p.y);
			}
		}
	}

	public void updateVisited() {
		Point dronePoint = drone.getOpticalSensorLocation();
		Point fromPoint = new Point(dronePoint.x + droneStartingPoint.x,dronePoint.y + droneStartingPoint.y);

		setPixel(fromPoint.x,fromPoint.y,PixelState.visited);

	}

	public void setPixel(double x, double y,PixelState state) {
		int xi = (int)x;
		int yi = (int)y;

		if(state == PixelState.visited) {
			map[xi][yi] = state;
			return;
		}

		if(map[xi][yi] == PixelState.unexplored) {
			map[xi][yi] = state;
		}
	}
	public void paintBlindMap(Graphics g) {
		Color c = g.getColor();

		int i = (int)droneStartingPoint.y - (int)drone.startPoint.x;
		int startY = i;
		for(;i<map_size;i++) {
			int j = (int)droneStartingPoint.x - (int)drone.startPoint.y;
			int startX = j;
			for(;j<map_size;j++) {
				if(map[i][j] != PixelState.unexplored)  {
					if(map[i][j] == PixelState.blocked) {
						g.setColor(Color.RED);
					}
					else if(map[i][j] == PixelState.explored) {
						g.setColor(Color.YELLOW);
					}
					else if(map[i][j] == PixelState.visited) {
						g.setColor(Color.BLUE);
					}
					g.drawLine(i-startY, j-startX, i-startY, j-startX);
				}
			}
		}
		g.setColor(c);
	}

	public void paintPoints(Graphics g) {
		for(int i=0;i<points.size();i++) {
			if (i % 5 == 0) {
				Point p = points.get(i);
				g.drawOval((int) p.x + (int) drone.startPoint.x - 10, (int) p.y + (int) drone.startPoint.y - 10, 20, 20);
			}
		}
	}

	public void paint(Graphics g) {
		if(SimulationWindow.toggleRealMap) {
			drone.realMap.paint(g);
		}

		paintBlindMap(g);
		paintPoints(g);

		drone.paint(g);


	}

	boolean is_init = true;

	boolean riskyStraight = false;
	boolean riskyRight = false;
	boolean riskyLeft = false;

	int max_risky_distance = 150;
	boolean try_to_escape = false;
	double  risky_dis = 0;

	double max_distance_between_points = 20;

	boolean calculate_alpha = false;

	Point init_point;

	public void ai(int deltaTime) {
		if(!SimulationWindow.toggleAI) {
			return;
		}

		if(is_init) {
			startTime = clock.millis();
			speedUp();
			Point dronePoint = drone.getOpticalSensorLocation();
			init_point = new Point(dronePoint);
			points.add(dronePoint);
			mGraph.addVertex(dronePoint);
			is_init = false;
		}
		
		Point dronePoint = drone.getOpticalSensorLocation();
		if (SimulationWindow.return_home) {
			if (points.size() > 0) {
				Point last = points.get(points.size() - 1);
				if(Math.abs(Tools.getDistanceBetweenPoints(last, dronePoint)) < 5){
					removeLastPoint();
					System.out.println("calculate_alpha");
					calculate_alpha = true;
				}
			}else {
				CPU.stopAllCPUS();
			}
		}else {
			if (Tools.getDistanceBetweenPoints(getLastPoint(), dronePoint) >= max_distance_between_points) {
				points.add(dronePoint);
				mGraph.addVertex(dronePoint);
			}
		}
		//Straight
		Lidar lidar = drone.lidars.get(0);
		//Right
		Lidar lidar1 = drone.lidars.get(1);
		//Left
		Lidar lidar2 = drone.lidars.get(2);
		
		if(!riskyStraight) {
			if (SimulationWindow.return_home && calculate_alpha) {
				calculate_alpha = false;
				Point last = getLastPoint();
			
				double A = last.y - dronePoint.y;
				double B = last.x - dronePoint.x;
				double alpha = Math.toDegrees(Math.atan2(A, B));
				
				if (alpha < 0) { alpha += 360;}

				double spin_deg = alpha - drone.getRotation();
				if (spin_deg>130){
					spin_deg = -1*(360-spin_deg);
				}
				speedDown();
				spinBy(spin_deg%360, false, new Func() {
						@Override
						public void method() {
//							System.out.println("RETURN HOME # The angle is: " + spin_deg);
							try_to_escape = false;
							riskyStraight = false;
							riskyRight = false;
							riskyLeft = false;
							speedUp();
						}
				});


			} else if(!SimulationWindow.return_home){
				if (lidar.current_distance < 300 && lidar.current_distance > 0 && lidar2.current_distance >= 300 ){
					calculate_alpha = true;
					spin_by = -90;
					spinBy(spin_by%360, true, new Func() {
						@Override
						public void method() {
							System.out.println("Mode is :" + mode);
							System.out.println("The angle is: " + spin_by);
							try_to_escape = false;
							riskyStraight = false;
							riskyRight = false;
							riskyLeft = false;
						}
					});
				}
			}
			if(lidar.current_distance <= max_risky_distance && lidar.current_distance > 0 ) {
				riskyStraight = true;
				risky_dis = lidar.current_distance;
			}

		} else { //Risky on straight
			if(SimulationWindow.return_home && calculate_alpha){
				calculate_alpha = false;
				Point last = getLastPoint();

				double A = last.y - dronePoint.y;
				double B = last.x - dronePoint.x;
				double alpha = Math.toDegrees(Math.atan2(A, B));

				if (alpha < 0) {
				    alpha += 360;
				}

				double spin_deg = alpha - drone.getRotation();

				speedDown();
				spinBy(spin_deg%360, false, new Func() {
						@Override
						public void method() {
//							System.out.println("RETURN HOME # The angle is: " + spin_deg);
							try_to_escape = false;
							riskyStraight = false;
							riskyRight = false;
							riskyLeft = false;
							speedUp();
						}
				});
				return;
			}else if (SimulationWindow.return_home){
				return;
			}

			if(!try_to_escape) { //Doesn't try to escape
				try_to_escape = true;
				Lidar lidar0 = drone.lidars.get(0);
				double straightDistance = lidar0.current_distance;

				double rightDistance = lidar1.current_distance;

				double leftDistance = lidar2.current_distance;

				if (Math.min(rightDistance,leftDistance) < 300){
					if (leftDistance < rightDistance) {
						spin_by = (int) (90-Math.toDegrees(Math.atan(straightDistance/leftDistance)));
					}else{
						spin_by = (int) -(90-Math.toDegrees(Math.atan(straightDistance/rightDistance)));
					}
				} else {
					if(straightDistance < 50) {
							spin_by = -90;
					}
				}
				spinBy(spin_by%360,true,new Func() {
					@Override
					public void method() {
						try_to_escape = false;
						riskyStraight = false;
						riskyRight = false;
						riskyLeft = false;
						calculate_alpha=true;
					}
				});
			}
		}
	}

	int counter = 0;

	double lastGyroRotation = 0;
	public void updateRotating(int deltaTime) {
		if(degrees_left.size() == 0) {
			return;
		}

		double degrees_left_to_rotate = degrees_left.get(0);
		boolean isLeft = true;
		if(degrees_left_to_rotate > 0) {
			isLeft = false;
		}

		double curr =  drone.getGyroRotation();
		double just_rotated;

		if (isLeft) {

			just_rotated = curr - lastGyroRotation;
			if(just_rotated > 0) {
				just_rotated = -(360 - just_rotated);
			}
		} else {
			just_rotated = curr - lastGyroRotation;
			if(just_rotated < 0) {
				just_rotated = 360 + just_rotated;
			}
		}



		lastGyroRotation = curr;
		degrees_left_to_rotate-=just_rotated;
		degrees_left.remove(0);
		degrees_left.add(0,degrees_left_to_rotate);

		if((isLeft && degrees_left_to_rotate >= 0) || (!isLeft && degrees_left_to_rotate <= 0)) {
			degrees_left.remove(0);

			Func func = degrees_left_func.get(0);
			if(func != null) {
				func.method();
			}
			degrees_left_func.remove(0);

			if(degrees_left.size() == 0) {
				isRotating = 0;
			}
			return;
		}

		int direction = (int)(degrees_left_to_rotate / Math.abs(degrees_left_to_rotate));
		drone.rotateLeft(deltaTime * direction);

	}

	public void spinBy(double degrees,boolean isFirst,Func func) {
		lastGyroRotation = drone.getGyroRotation();
		if(isRotating == 0) {
			if (isFirst) {
				degrees_left.add(0, degrees);
				degrees_left_func.add(0, func);
			} else {
				degrees_left.add(degrees);
				degrees_left_func.add(func);
			}
			isRotating =1;
		}
	}

	public void spinBy(double degrees,boolean isFirst) {
		lastGyroRotation = drone.getGyroRotation();
		if(isFirst) {
			degrees_left.add(0,degrees);
			degrees_left_func.add(0,null);


		} else {
			degrees_left.add(degrees);
			degrees_left_func.add(null);
		}

		isRotating =1;
	}

	public void spinBy(double degrees) {
		lastGyroRotation = drone.getGyroRotation();

		degrees_left.add(degrees);
		degrees_left_func.add(null);
		isRotating = 1;
	}

	public Point getLastPoint() {
		if(points.size() == 0) {
			return init_point;
		}
		return points.get(points.size()-1);
	}

	public Point removeLastPoint() {
		if(points.isEmpty()) {
			return init_point;
		}

		return points.remove(points.size()-1);
	}


	public Point getAvgLastPoint() {
		if(points.size() < 2) {
			return init_point;
		}

		Point p1 = points.get(points.size()-1);
		Point p2 = points.get(points.size()-2);
		return new Point((p1.x + p2.x) /2, (p1.y + p2.y) /2);
	}


}
