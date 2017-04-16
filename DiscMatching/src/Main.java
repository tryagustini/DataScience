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
	public static double MAX_TRESHOLD = 1;
	public static ArrayList<String> probabilityId = new ArrayList<>();

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
			for (Disc dup: discs.disc ) {
				if(dup.id == disc.id) continue;
				double similarity = L.similarity(disc.dtitle, dup.dtitle);				
				if(similarity > MIN_TRESHOLD && similarity < MAX_TRESHOLD){
					if(consumedId.indexOf(dup.id+disc.id) > -1) continue;
					consumedId.add(dup.id+disc.id);

					//Save the pair into array
					ArrayList<Disc> pairContainer = new ArrayList<>();
					dup.probability = similarity;
					pairContainer.add(disc);					
					pairContainer.add(dup);	
					pairList.add(pairContainer);
				}
			}
		}
		
		System.out.println("Number of duplicates: " +pairList.size());

		
		try{
		    PrintWriter writer = new PrintWriter("discmatching.dl", "UTF-8");		    

		    
		    writer.println("%Probalistically integrate 2 data");
		    writer.println("%Gilang Charismadiptya - S1779524");
		    writer.println("%Try Agustini - ");
		    writer.println("");
		    
		    //Print id, Dtitle
		    for (ArrayList<Disc> pairs : pairList){
		    	
				String partition = generatePartition();
				String partition2 = generatePartition();

				writer.println("final_disc_id("+pairs.get(0).id+") ["+partition+"=1 or "+partition+"=2].");						
				writer.println("final_disc_id("+pairs.get(1).id+") ["+partition+"=2].");										
				
				writer.println("final_disc_dtitle("+pairs.get(0).id+",\""+pairs.get(0).dtitle+"\")[("+partition+"="+1+" and "+partition2+"=1) or "+partition+"=2].");
				writer.println("final_disc_dtitle("+pairs.get(0).id+",\""+pairs.get(1).dtitle+"\")["+partition+"="+1+" and "+partition2+"=2].");
				writer.println("final_disc_dtitle("+pairs.get(1).id+",\""+pairs.get(1).dtitle+"\")["+partition+"=2].");
				
				writer.println("final_disc_cid("+pairs.get(0).id+",\""+pairs.get(0).cid+"\")[("+partition+"="+1+" and "+partition2+"=1) or "+partition+"=2].");
				writer.println("final_disc_cid("+pairs.get(0).id+",\""+pairs.get(1).cid+"\")["+partition+"="+1+" and "+partition2+"=2].");
				writer.println("final_disc_cid("+pairs.get(1).id+",\""+pairs.get(1).cid+"\")["+partition+"=2].");

				writer.println("final_disc_artist("+pairs.get(0).id+",\""+pairs.get(0).artist+"\")[("+partition+"="+1+" and "+partition2+"=1) or "+partition+"=2].");
				writer.println("final_disc_artist("+pairs.get(0).id+",\""+pairs.get(1).artist+"\")["+partition+"="+1+" and "+partition2+"=2].");
				writer.println("final_disc_artist("+pairs.get(1).id+",\""+pairs.get(1).artist+"\")["+partition+"=2].");

				writer.println("final_disc_category("+pairs.get(0).id+",\""+pairs.get(0).category+"\")[("+partition+"="+1+" and "+partition2+"=1) or "+partition+"=2].");
				writer.println("final_disc_category("+pairs.get(0).id+",\""+pairs.get(1).category+"\")["+partition+"="+1+" and "+partition2+"=2].");
				writer.println("final_disc_category("+pairs.get(1).id+",\""+pairs.get(1).category+"\")["+partition+"=2].");

				writer.println("final_disc_tracks("+pairs.get(0).id+",\""+pairs.get(0).tracks+"\")[("+partition+"="+1+" and "+partition2+"=1) or "+partition+"=2].");
				writer.println("final_disc_tracks("+pairs.get(0).id+",\""+pairs.get(1).tracks+"\")["+partition+"="+1+" and "+partition2+"=2].");
				writer.println("final_disc_tracks("+pairs.get(1).id+",\""+pairs.get(1).tracks+"\")["+partition+"=2].");

				writer.println("@p("+partition+"=1)="+(Precision.round(pairs.get(1).probability,2))+".");
				writer.println("@p("+partition+"=2)="+(Precision.round(1-pairs.get(1).probability,2))+".");
				writer.println("@uniform "+partition2+".");
				writer.println("");
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
	
	public static String  generatePartition(){
		String uniquePartition;

		//Generate ID and make sure the ID is unique
		while(true){
			uniquePartition = RandomStringUtils.randomAlphabetic(3).toUpperCase();
			if(probabilityId.indexOf(uniquePartition) == -1){
				probabilityId.add(uniquePartition);
				break;
			}
		}
		return uniquePartition;
	}
	
	
}
