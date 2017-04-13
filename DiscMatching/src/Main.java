import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.math3.util.Precision;

import info.debatty.java.stringsimilarity.*;

public class Main {
	
	public static double MIN_TRESHOLD = 0.8;
	public static double MAX_TRESHOLD = 0.90;

	public static void main(String[] args) {

	 try {
		 
		/*
		 * Setup comparison engine
		 * Jaro-Winkler computes the similarity between 2 strings,
		 * and the returned value lies in the interval [0.0, 1.0]. 
		 * It is (roughly) a variation of Damerau-Levenshtein, where the substitution of 2 close characters is 
		 * considered less important then the substitution of 2 characters that a far from each other.
		 * https://github.com/tdebatty/java-string-similarity
		 */
		 JaroWinkler L = new JaroWinkler();

		File file = new File("src/xml/cddb_discs.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Discs.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Discs discs = (Discs) jaxbUnmarshaller.unmarshal(file);
		System.out.println("Number of records: " +discs.disc.size());
								
		ArrayList<Integer> consumedId = new ArrayList<>();
		ArrayList<ArrayList<Disc>> pairList = new ArrayList<>();
		
		for (Disc disc :discs.disc ) {
			if(consumedId.indexOf(disc.id) > -1) continue;

			ArrayList<Disc> pairContainer = new ArrayList<>();
			consumedId.add(disc.id);
			pairContainer.add(disc);
			
			for (Disc dup: discs.disc ) {
				if(consumedId.indexOf(dup.id) > -1) continue;
				
				double similarity = L.similarity(disc.dtitle, dup.dtitle);
				
				if(similarity > MIN_TRESHOLD){
					consumedId.add(dup.id);
					dup.probability = similarity;
					pairContainer.add(dup);	
				}
			}
			pairList.add(pairContainer);
		}
		
		
		ArrayList<String> probabilityId = new ArrayList<>();
		String uniquePartition;
		String uniquePartition2;
		int partitionNumber;
		
		try{
		    PrintWriter writer = new PrintWriter("discmatching.dl", "UTF-8");		    
		    
		    for (ArrayList<Disc> pairs : pairList){
		    	
				//Generate ID and make sure the ID is unique
				while(true){
					uniquePartition = RandomStringUtils.randomAlphabetic(3).toUpperCase();
					if(probabilityId.indexOf(uniquePartition) == -1){
						probabilityId.add(uniquePartition);
						break;
					}
				}
				partitionNumber = 1;
				Boolean isFirstRecord = true; 
				
				for (Disc d :pairs ) {				
					
					if(isFirstRecord){
						isFirstRecord = false;
						String prob = "["+uniquePartition+"="+partitionNumber;
						for (int i = 1; i < pairs.size();i++){
							prob+= " or "+uniquePartition+"="+(i+1);
						}
						prob+="]";
						writer.println("final_disc_id("+d.id+")"+prob+".");						
					}else{
						writer.println("final_disc_id("+d.id+")["+uniquePartition+"="+partitionNumber+"].");						
					}
					
					
					partitionNumber++;
				}
				
				
				//Generate ID and make sure the ID is unique
				while(true){
					uniquePartition2 = RandomStringUtils.randomAlphabetic(3).toUpperCase();
					if(probabilityId.indexOf(uniquePartition2) == -1){
						probabilityId.add(uniquePartition2);
						break;
					}
				}

				
				isFirstRecord = true; 
				int partnum = 1;
				int  id= 0;
				for (Disc d :pairs ) {					
					if(isFirstRecord){
						isFirstRecord = false;
						String proba = "["+uniquePartition+"=1";
						proba+= " or "+uniquePartition2+"="+1;
						proba+="]";
						id = d.id;
						writer.println("final_disc_dtitle("+d.id+",\""+d.dtitle+"\")"+proba+".");
					}else{
						writer.println("final_disc_dtitle("+id+",\""+d.dtitle+"\")["+uniquePartition+"="+1+" and "+uniquePartition2+"="+(partnum+1)+"].");
						partnum++;
					}
				}
				
				isFirstRecord = true; 
				partnum = 1;
				for (Disc d :pairs ) {					
					if(isFirstRecord){
						isFirstRecord = false;
						continue;
					}
					writer.println("final_disc_dtitle("+d.id+",\""+d.dtitle+"\")["+uniquePartition+"="+(partnum+1)+"].");
					partnum++;					
				}
				
					//Write Partition
					if(pairs.size() == 2){
						int i = 1;
						for (Disc d :pairs ) {		
							if(i==1 && pairs.size()>1){
								writer.println("@p("+uniquePartition+"="+i+")="+ (Precision.round(pairs.get(i).probability,2))+"." );
							}else{
								writer.println("@p("+uniquePartition+"="+i+")="+Precision.round(1-d.probability, 2)+".");
							}
							i++;
						}
					}else{
						writer.println("@uniform "+uniquePartition+".");	
					}
					
					if(pairs.size() > 1){
						writer.println("@uniform "+uniquePartition2+".");
					}
					
				writer.println("");	

		    }
		    
		    
		    		    for (ArrayList<Disc> pairs : pairList){
			    //Print the rest of the data
				for (Disc d :pairs ) {					
					writer.println("final_disc_cid("+d.id+",\""+d.cid+"\").");
					writer.println("final_disc_artist("+d.id+",\""+d.artist+"\").");
					writer.println("final_disc_category("+d.id+",\""+d.category+"\").");
					writer.println("final_disc_tracks("+d.id+",\""+d.tracks+"\").");
					writer.println("");	
				}
		    }
		    
		    //Print the rules
			writer.println("final_disc(Id,Cid,Artist,Dtitle,Category,Tracks) :-"+
					"\n\tfinal_disc_id(Id),"+
					"\n\tfinal_disc_cid(Id,Cid),"+
					"\n\tfinal_disc_artist(Id,Artist),"+
					"\n\tfinal_disc_dtitle(Id,Dtitle),"+
					"\n\tfinal_disc_category(Id,Category),"+
					"\n\tfinal_disc_tracks(Id,Tracks).");

		    
		    
		    writer.close();
		    
		} catch (IOException e) {
		   // do something
			System.out.println(e.getMessage());
		}
				
	  } catch (JAXBException e) {
		e.printStackTrace();
	  }

	}
}
