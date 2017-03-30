import java.util.ArrayList;
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
