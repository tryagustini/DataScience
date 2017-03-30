import java.util.List;
import java.util.ArrayList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;


@XmlRootElement (name="discs")
class Discs{
	@XmlElement
	public List<Disc> disc = new ArrayList<Disc> (); 
}

class Disc{
	@XmlElement
	private int id;
	
	@XmlElement
	private String cid;
	
	@XmlElement
	private String artist;
	
	@XmlElement
	private String dtitle;
	
	@XmlElement
	private String category;
	
	@XmlElement
	private Tracks tracks;
	
	private int getID() {
		return id;
	}

	private void setID(int id) {
		this.id = id;
	}

	private String getCID() {
		return cid;
	}

	private void setCID(String cid) {
		this.cid = cid;
	}
	
	private String getArtist() {
		return artist;
	}

	private void setArtist(String artist) {
		this.artist = artist;
	}

	private String getDtitle() {
		return dtitle;
	}
	
	private void setDtitle(String dtitle) {
		this.dtitle = dtitle;
	}

	private String getCategory() {
		return category;
	}
	
	private void setCategory(String category) {
		this.category = category;
	}
	
	private Tracks getTracks() {
		return tracks;
	}
	
	private void setTracks(Tracks tracks) {
		this.tracks = tracks;
	}
	
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

public class Main {
	public static void main(String[] args) {

	 try {

		File file = new File("src/xml/cddb_discs.xml");
		JAXBContext jaxbContext = JAXBContext.newInstance(Discs.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
		Discs discs = (Discs) jaxbUnmarshaller.unmarshal(file);
		System.out.println(discs.disc);
		System.out.println("Number of records: " +discs.disc.size());

	  } catch (JAXBException e) {
		e.printStackTrace();
	  }

	}
}
