package nearsoft.academy.bigdata.recommendation;

import com.google.common.collect.HashBiMap;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by antoniohernandez on 4/1/17.
 */
public class CSVParser {
    private String path;
    private HashMap<String, Integer> userMap;
    private HashBiMap<String, Integer> productMap;
    private ArrayList<String> outputList;
    private List<String> recommendations;
    private String outPutPath;
    public static void main(String args []){
        CSVParser parser = new CSVParser("movies.txt");
        try{
            parser.ioAction();
            parser.writeRecommend();
            System.out.println(parser.reviewsQty());
            System.out.println(parser.productQty());
            System.out.println(parser.userQty());

        }
            catch(IOException e){System.out.println(e.getMessage());}

    }

    public CSVParser(String path){
        this.path = path;
        userMap = new HashMap<>();
        productMap = HashBiMap.create();
        outputList = new ArrayList<>();
        recommendations = new ArrayList<>();

    }

    private void ioAction() throws IOException{
        String productID = "";
        String userID = "";
        String score = "";
        List<String> list = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(path))) {
            System.out.println("Reading Info...");
            for(String line; (line = br.readLine()) != null; ) {

                if (line.contains("product/productId")){
                    String pID = line.split(": ")[1];
                    //System.out.println(pID);
                    //productID = productMap.containsKey(pID) ? String.valueOf(productMap.get(pID)) : String.valueOf(productMap.size() + 1);
                    if (!productMap.containsKey(pID))
                        productMap.put(pID, productMap.size());
                    productID = productMap.get(pID).toString();
                }
                else if (line.contains("review/userId")){
                    String uID = line.split(": ")[1];
                    //System.out.println(uID);
                    //userID = userMap.containsKey(uID) ? String.valueOf(userMap.get(uID)) : String.valueOf(userMap.size() + 1);
                    if (!userMap.containsKey(uID))
                        userMap.put(uID, userMap.size());
                    userID = userMap.get(uID).toString();
                }
                else if (line.contains("review/score")){
                    score = line.split(": ")[1];
                    //System.out.println(score);
                    outputList.add(String.format("%s,%s,%s", userID,productID,score));
                }
            }
            writeRecommend();
        }


    }
    private void writeRecommend() throws  IOException{
        System.out.println("Writing info...");
        File file = File.createTempFile("movies", ".csv");
        outPutPath = file.getAbsolutePath();
        Writer writer = Channels.newWriter(new FileOutputStream(file.getAbsoluteFile()).getChannel(), "UTF-8");
        for (String outputReview : outputList) {
                //System.out.println(outputReview);
                writer.append(outputReview+"\n");
        }

        writer.flush();
        System.out.println(outPutPath);






    }

    private void findRecommendations(String userId){
        try {
            DataModel model = new FileDataModel(new File(outPutPath));
            UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
            UserNeighborhood neighborhood = new ThresholdUserNeighborhood(0.1, similarity, model);
            UserBasedRecommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
            List<RecommendedItem> recommends = recommender.recommend(userMap.get(userId), 3);
            for (RecommendedItem recommendation : recommends) {
                System.out.println(recommendation.getItemID());
                recommendations.add(productMap.inverse().get((int) recommendation.getItemID()));
                /*for (Map.Entry<String, Integer> entry : productMap.entrySet()) {
                    if (recommendation.getItemID() == entry.getValue().longValue()) {
                        System.out.println("entre");
                        recommendations.add(entry.getKey());
                    }
                }
                    recommendations = Stream.concat(recommendations.stream(), productMap.entrySet()
                            .stream()
                            .filter(entry -> Objects.equals(entry.getValue(), recommendation.getItemID()))
                            .map(Map.Entry::getKey).collect(Collectors.toList()).stream()).collect(Collectors.toList());
                    System.out.println(recommendations);*/
            }



        }
        catch(IOException|TasteException e){System.out.println(e.toString());}
    }

    public void doParse(String oFile) throws IOException{
        ioAction();

    }

    public long reviewsQty(){

        return outputList.size();
    }
    public long productQty(){

        return productMap.size();
    }
    public long userQty(){

        return userMap.size();
    }
    public List<String> getRecommendations(String userId){
        findRecommendations(userId);

        return recommendations;
    }



}
