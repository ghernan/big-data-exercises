package nearsoft.academy.bigdata.recommendation;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.ThresholdUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.UserBasedRecommender;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

public class MovieRecommender {
    String path = "";
    CSVParser parser;

    public MovieRecommender(String path){
        this.path = path;
        parser = new CSVParser("movies.txt");
        try{

            parser.doParse(this.path);

        }catch(IOException e){System.out.println(e.getMessage());}




    }
    public static void main(String args[]){




    }
    public List<String> getRecommendationsForUser(String userId){

        return parser.getRecommendations(userId);
    }

    public long getTotalReviews(){
        System.out.println("Getting Reviews...");
        return parser.reviewsQty();
    }
    public long getTotalProducts(){
        System.out.println("Getting Products...");
        return parser.productQty();
    }
    public long getTotalUsers(){
        System.out.println("Getting Users...");
        return parser.userQty();
    }






}