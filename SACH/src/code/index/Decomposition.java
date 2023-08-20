package code.index;

import code.online.MetaPath;

import java.util.Map;

/**
 * @author fangyixiang
 * @date 15 Oct. 2018
 * an interface for different decomposition methods
 */
public interface Decomposition {
	public Map<Integer, Integer> decompose(MetaPath queryMPath);//return the core number of each vertex
	public int[] getReverseOrderArr();//return the vertex array, where vertices are sorted reversely
}
