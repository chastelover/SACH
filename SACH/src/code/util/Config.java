package code.util;

import javax.swing.filechooser.FileSystemView;
import java.io.File;


/**
 * @author fangyixiang
 * @date 2018-09-10
 * Global parameters
 */
public class Config {
	//the root of date files
	public static String root = "dataset/HINData";

	//the root of output files
	public static String outRoot = "Influential_CS_over_HINs/output";
			//"/home/liuyanghao/HIN-JAVA/Influential_CS_over_HINs/output";

	// /home/yf711/jiajt/SourceCode/HINData
	// /home/liuyanghao/HIN-JAVA/dataset
	{
		FileSystemView fsv = FileSystemView.getFileSystemView();
		File com=fsv.getHomeDirectory();
		root = com.getPath();//automatically obtain the path of Desktop
	}

	//IndexTreeName
	public static String indexTreeRoot = "IndexTreeRoot";
	public static String indexMPNode = "IndexMPNode";
	public static String indexKNode = "IndexKNode";
	public static String indexTreeNode = "IndexTreeNode";
	public static String indexTreeEnd = "IndexTreeRoot";

	//SmallDBLP
	public static String smallDBLPRoot = root + "/test/";
	public static String smallDBLPGraph = smallDBLPRoot + "graph.txt";
	public static String smallDBLPVertex = smallDBLPRoot + "vertex.txt";
	public static String smallDBLPEdge = smallDBLPRoot + "edge.txt";
	public static String smallDBLPVertexWeight = smallDBLPRoot + "weight.txt";
	public static String smallDBLPReadme = smallDBLPRoot + "readme.txt";

	//LikeDBLPFromPaper
	public static String likeDBLPRoot = root + "/test1/";
	public static String likeDBLPGraph = likeDBLPRoot + "graph.txt";
	public static String likeDBLPVertex = likeDBLPRoot + "vertex.txt";
	public static String likeDBLPEdge = likeDBLPRoot + "edge.txt";
	public static String likeDBLPVertexWeight = likeDBLPRoot + "weight.txt";
	public static String likeDBLPReadme = likeDBLPRoot + "readme.txt";

	//LikeDBLPFromPaper1
	public static String likeDBLPRoot2 = root + "/test2/";
	public static String likeDBLPGraph2 = likeDBLPRoot2 + "graph.txt";
	public static String likeDBLPVertex2 = likeDBLPRoot2 + "vertex.txt";
	public static String likeDBLPEdge2 = likeDBLPRoot2 + "edge.txt";
	public static String likeDBLPVertexWeight2 = likeDBLPRoot2 + "weight.txt";
	public static String likeDBLPReadme2 = likeDBLPRoot2 + "readme.txt";

	//DBLP
	public static String dblpRoot = root + "/dblp/";
	public static String dblpGraph = dblpRoot + "graph.txt";
	public static String dblpVertex = dblpRoot + "vertex.txt";
	public static String dblpEdge = dblpRoot + "edge.txt";
	public static String dblpVertexWeight = dblpRoot + "weight.txt";
	public static String dblpReadme = dblpRoot + "readme.txt";

	//PubMed
	public static String PubMedRoot = root + "/PubMed/";
	public static String PubMedGraph = PubMedRoot + "graph.txt";
	public static String PubMedVertex = PubMedRoot + "vertex.txt";
	public static String PubMedEdge = PubMedRoot + "edge.txt";
	public static String PubMedVertexWeight = PubMedRoot + "weight.txt";
	public static String PubMedReadme = PubMedRoot + "readme.txt";

	//Foursquare
	public static String FsqRoot = root + "/foursquare/";
	public static String FsqGraph = FsqRoot + "graph.txt";
	public static String FsqVertex = FsqRoot + "vertex.txt";
	public static String FsqEdge = FsqRoot + "edge.txt";
	public static String FsqVertexWeight = FsqRoot + "weight.txt";
	public static String FsqReadme = FsqRoot + "readme.txt";

	//IMDB
	public static String IMDBRoot = root + "/imdb/";
	public static String IMDBGraph = IMDBRoot + "graph.txt";
	public static String IMDBVertex = IMDBRoot + "vertex.txt";
	public static String IMDBEdge = IMDBRoot + "edge.txt";
	public static String IMDBVertexWeight = IMDBRoot + "weight.txt";
	public static String IMDBReadme = IMDBRoot + "readme.txt";

	public static String machineName = "liuyanghao";
//	public static String logFinalResultFile = Config.root + "/outdata/" + machineName;//our final experimental result data
public static String logFinalResultFile = "./log";//our final experimental result data
	public static String logPartResultFile = Config.root + "/outdata/" + machineName + "-part";//intermediate result

	public static int k = 6;

}
