import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement (name="discs")
public class Discs{
	@XmlElement
	public List<Disc> disc = new ArrayList<Disc> (); 
}

class Disc{
	@XmlElement
	public int id;
	
	@XmlElement
	public String cid;
	
	@XmlElement
	public String artist;
	
	@XmlElement
	public String dtitle;
	
	
	@XmlElement
	public String category;
	
	@XmlElement
	public Tracks tracks;
	
	public HashMap<Integer,Double> probability;	

	@Override
	public String toString() {
		return "Disc [ID=" +id +", cid=" +cid +", artist=" +artist +", dititle=" +dtitle 
				+", category=" +category +", tracks=" +tracks +"]\n";

	}

}

@XmlType
class Tracks {
    @XmlElement
    List<String> title;
    
    public String toString(){
    	return title.toString();
    }
}
