package code.util;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

/**无向图
 * 5
 * A B C D E
 * B A E
 * C A D
 * D A C
 * E A B
 *
 *
 * A : 0.3193032728467021
 * B : 0.170178554419658
 * C : 0.170178554419658
 * D : 0.17017823716469735
 * E : 0.17017823716469735
 *
 */
public class GenerateWeightByPageRank {
    public static void main(String[] args) {
        List<PageNode> graph =PageRank.build();
        PageRank.Cal(graph);
//        for(PageNode node:graph){
//            System.out.println(node.getNodeName()+" : "+node.getScore());
//        }
        try {
//            FileWriter write=new FileWriter("E:\\SourceCode\\HINData\\test\\weight.txt");
//            FileWriter write=new FileWriter("E:\\SourceCode\\HINData\\dblp\\weight.txt");
//            FileWriter write=new FileWriter("E:\\SourceCode\\HINData\\imdb\\weight.txt");
            FileWriter write=new FileWriter("E:\\SourceCode\\HINData\\PubMed\\weight.txt");
            BufferedWriter bw=new BufferedWriter(write);
            for(PageNode node:graph){
                bw.write(node.getNodeName() + " " + node.getScore() + "\n");
            }
            bw.close();
            write.close();
        }catch(IOException e){
            e.printStackTrace();
        }
        System.out.println("Done!");
    }
}