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
		JaroWinkler jw = new JaroWinkler();

		File file = new File("src/xml/cddb_discs.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Discs.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Discs discs = (Discs) jaxbUnmarshaller.unmarshal(file);
		System.out.println("Number of records: " +discs.disc.size());
								
		for (Disc disc :discs.disc ) {			
			for (Disc dup: discs.disc ) {
				if(dup.id == disc.id) continue;
				if(disc.probability == null) disc.probability = new HashMap<Integer,Double>();
				disc.probability.put(dup.id, jw.similarity(disc.dtitle, dup.dtitle));
			}
		}

		HashSet<String> probabilityID = new HashSet<String>();

		try{
		    PrintWriter writer = new PrintWriter("discmatching.dl", "UTF-8");
		    
			for (Disc d :discs.disc ) {					
				writer.println("disc_id("+d.id+").");
				writer.println("disc_cid("+d.id+",\""+d.cid+"\").");
				writer.println("disc_artist("+d.id+",\""+d.artist+"\").");
				writer.println("disc_dtitle("+d.id+",\""+d.dtitle+"\").");
				writer.println("disc_category("+d.id+",\""+d.category+"\").");
				writer.println("disc_tracks("+d.id+",\""+d.tracks+"\").");
				writer.println("");	
				
				for (Map.Entry<Integer, Double> entry : d.probability.entrySet()) {					
				    Integer key = entry.getKey();
				    Double value = entry.getValue();
				   
				    if (value < 0.7) continue;
				    //Generate random, unique id for each probability set
				    String r;
				    while(true){
				    	r = RandomStringUtils.randomAlphabetic(3).toUpperCase();
				    	if(!probabilityID.contains(r)){
				    		probabilityID.add(r);
				    		break;
				    	}
				    }
				       
					writer.println("is_duplicate("+d.id+","+key+",true) ["+r+"=1].");	
					writer.println("is_duplicate("+d.id+","+key+",false) ["+r+"=2].");		    		
					writer.println("@p("+r+"=1) = "+Precision.round(value, 2)+".");
					writer.println("@p("+r+"=2) = "+Precision.round((1-value), 2)+".");
					writer.println("");	
				}			
			}

			
			//Rules!
			writer.println("disc(Id,Cid,Artist,Dtitle,Category,Tracks) :-"+
									"\n\tdisc_id(Id),"+
									"\n\tdisc_cid(Id,Cid),"+
									"\n\tdisc_artist(Id,Artist),"+
									"\n\tdisc_dtitle(Id,Dtitle),"+
									"\n\tdisc_category(Id,Category),"+
									"\n\tdisc_tracks(Id,Tracks).");
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
