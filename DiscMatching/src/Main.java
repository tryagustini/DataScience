import java.io.File;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

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
