package com.path;
import java.util.LinkedList;
import java.util.List;

import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.zombie.C;
import com.zombie.logic.object.GameObject;

public class Grid {
	public Node[][] grid;
	private int xMax, yMax;
	Heap heap;
	
	/**
	 * Grid is created, Land is generated in either uniform or random fashion, landscape 'Map' is created in printed.
	 * 
	 * 
	 * @param xMax - (int) maximum x coordinate 
	 * @param yMax - (int) maximum y coordinate 
	 * @param layer 
	 * @param xIsland (int) number of islands along x axis
	 * @param yIsland (int) number of islands along y axis
	 * @param uniform (boolean) if true then land is generated in a uniform fashion, if false then land is randomly generated
	 */
	public Grid(int xMax, int yMax, TiledMapTileLayer layer){
		this.xMax = xMax;
		this.yMax = yMax;
		System.out.println(xMax + " x " + yMax);
		
		grid = new Node[this.xMax][this.yMax];
		heap = new Heap();

		for(int x = 0; x < xMax;x++)
			for(int y = 0; y < yMax;y++){
				boolean pass = true;
				if (layer.getCell(x, y) != null)
					pass = false;
				grid[x][y] = new Node(x,y,pass);
			}
	}
	
	public void clearHeap(){
		heap = new Heap();
		for(int x = 0; x < 100;x++)
			for(int y = 0; y < 100;y++){
				getNode(x,y).updateGHFP(0, 0, null);
			}
	}
	/**
	 * returns all adjacent nodes that can be traversed
	 * 
	 * @param node (Node) finds the neighbors of this node
	 * @return (int[][]) list of neighbors that can be traversed
	 */
	public int[][] getNeighbors(Node node){
		int[][] neighbors = new int[8][2];
		int x = node.x;
		int y = node.y;
		boolean d0 = false; //These booleans are for speeding up the adding of nodes.
		boolean d1 = false;
		boolean d2 = false;
		boolean d3 = false;
		
		if (walkable(x,y-1)){
			neighbors[0] = (tmpInt(x,y-1));
			d0 = d1 = true;
		}
		if (walkable(x+1,y)){
			neighbors[1] = (tmpInt(x+1,y));
			d1 = d2 = true;
		}
		if (walkable(x,y+1)){
			neighbors[2] = (tmpInt(x,y+1));
			d2 = d3 = true;
		}
		if (walkable(x-1,y)){
			neighbors[3] = (tmpInt(x-1,y));
			d3 = d0 = true;
		}
		if (d0 && walkable(x-1,y-1)){
			neighbors[4] = (tmpInt(x-1,y-1));
		}
		if (d1 && walkable(x+1,y-1)){
			neighbors[5] = (tmpInt(x+1,y-1));
		}
		if (d2 && walkable(x+1,y+1)){
			neighbors[6] = (tmpInt(x+1,y+1));
		}
		if (d3 && walkable(x-1,y+1)){
			neighbors[7] = (tmpInt(x-1,y+1));
		}
		return neighbors;
	}
	
//---------------------------Passability------------------------------//
	/**
	 * Tests an x,y node's passability 
	 * 
	 * @param x (int) node's x coordinate
	 * @param y (int) node's y coordinate
	 * @return (boolean) true if the node is obstacle free and on the map, false otherwise
	 */
	public boolean walkable(int x, int y){
		try{
			return getNode(x,y).pass;     
		}
		catch (Exception e){
//			System.out.println("awdawd " + x + "  "+ y);
			return false;
		}
	}

//--------------------------HEAP-----------------------------------//	
	/**
	 * Adds a node's (x,y,f) to the heap. The heap is sorted by 'f'.
	 * 
	 * @param node (Node) node to be added to the heap
	 */
	public void heapAdd(Node node){
		float[] tmp = {node.x,node.y,node.f};
		heap.add(tmp);
	}
	
	/**
	 * @return (int) size of the heap
	 */
	public int heapSize(){
		return heap.getSize();
	}
	/**
	 * @return (Node) takes data from popped float[] and returns the correct node
	 */
	public Node heapPopNode(){
		float[] tmp = heap.pop();
		return getNode((int)tmp[0],(int)tmp[1]);
	}

//---------------------------------------------------------//
	/**
	 * Encapsulates x,y in an int[] for returning. A helper method for the jump method
	 * 
	 * @param x (int) point's x coordinate
	 * @param y (int) point's y coordinate
	 * @return ([]int) bundled x,y
	 */
	public int[] tmpInt (int x, int y){
		int[] tmpIntsTmpInt = {x,y};  //create the tmpInt's tmpInt[]
		return tmpIntsTmpInt;         //return it
	}
	
	/**
	 * getFunc - Node at given x, y
	 * 
	 * @param x (int) desired node x coordinate
	 * @param y (int) desired node y coordinate
	 * @return (Node) desired node
	 */
	public Node getNode(int x, int y){
		try{
			return grid[x][y];
		}
		catch(Exception e){
			return null;
		}
	}
	
	public float toPointApprox(float x, float y, int tx, int ty){
		return (float) Math.sqrt(Math.pow(Math.abs(x-tx),2) + Math.pow(Math.abs(y-ty), 2));		
	}
	
	public List<Node> pathCreate(Node node){
		long start = System.currentTimeMillis();
		List<Node> trail = new LinkedList<Node>();
		System.out.println("Tracing Back Path...");
		while (node.parent!=null){
			
			try{
				trail.add(0,node);
			}catch (Exception e){e.printStackTrace();}
			node = node.parent;
		}
		long end = System.currentTimeMillis();
		System.out.println("Path Trace Complete! time consumed " + (end-start));
		return trail;
	}

	public void dispose() {
		heap = null;
		grid = null;
	}
	
	public boolean LOSCheck (GameObject from, GameObject to) {
//		System.out.println("LOS "+ from + " _ " + to);
		int xstart = (int) (from.getX()/C.TILESIZE);
		int ystart = (int) (from.getY()/C.TILESIZE);
		
		int xend = (int) (to.getX()/C.TILESIZE);
		int yend = (int) (to.getY()/C.TILESIZE);
//		System.out.println(xstart + "_"+ystart + "  :  "+ xend+ "_"+yend);
		return LOSCheck(xstart,ystart,xend,yend);
	}

	public boolean LOSCheck (int xstart, int ystart, int xend, int yend) {	
			if (xstart < 0 || xstart > grid.length)
				return false;
			if (ystart < 0 || ystart > grid[0].length)
				return false;
			if (xend < 0 || xend > grid.length)
				return false;
			if (yend < 0 || yend > grid[0].length)
				return false;
			int x, y, dx, dy, incx, incy, pdx, pdy, es, el, err;
			dx = xend - xstart;
			dy = yend - ystart;
			incx = sign(dx);
			incy = sign(dy);
			
			if (dx < 0) dx = -dx;
			if (dy < 0) dy = -dy;
			
			if (dx > dy){
				pdx = incx;
				pdy = 0;
				es = dy;
				el = dx;
			} else {
				pdx = 0;
				pdy = incy;
				es = dx;
				el = dy;
			}
			x = xstart;
			y = ystart;
			err = el/2;
//			try{if (grid[x][y].pass;
//			} catch(Exception e){return false;}
			for (int t = 0; t < el; t++) {
				err -= es;
				if (err < 0) {
					err += el;
					x += incx;//сдвинуть прямую (сместить вверх или вниз, если цикл проходит по иксам)
					y += incy;//или сместить влево-вправо, если цикл проходит по y
				} else {
					x += pdx;//продолжить тянуть прямую дальше, т.е. сдвинуть влево или вправо, если
					y += pdy;//цикл идёт по иксу; сдвинуть вверх или вниз, если по y
				}
				try{if (!grid[x][y].pass) return false;
				} catch(Exception e){return false;}

			}
			return true;
		}
	
    //возвращает 0, если аргумент (x) равен нулю; -1, если x < 0 и 1, если x > 0.
	private int sign (int x) {
        return (x > 0) ? 1 : (x < 0) ? -1 : 0;
 	}
}

