# Drone-Simulator
Copy-Rights for : https://github.com/vection/DroneSimulator


# EX1 - Improve given drone-simuloatr code
### By: Shani Itzhakov, Eden Shkuri and Yosef Schwartz

In our project we take the `ai` function and try to improve it.
We noticed that the build-in function is very naive, and it search for 150px from straight or 50px from the sides, and according that it handle his risks and rotations.

So, we chose to change the concept.
</br>
First, we looked only at the straight risks and handled it by trigonometric logic - we found the right angle that we needed to rotate so the drone will be parallel to the closest wall (right or left).
</br>
Later, we saw it was not enough, we needed one more mode so we forced the drone to take left when it got infinity
</br>
(in the code, the value is >= 300) from the left Lidar and less then infinity from the straight Lidar.

In addition, we thought how to improve the `return home` mode - in the first attemp we just calculated the angle between nodes on the map (there was a node every 100 meter), but it didn't work because sometimes there is a corner between two nodes in the map, 
so we added more nodes to the map (node every 20 instead of 100 meter), such that the drone can return home saftey.
#### we didn't refer the nodes as a graph with edges, but as a list of cordinates, so the calculation to the last node in the 'return home' mode was trigonometric like the The road at the beginning.
