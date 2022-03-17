import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
public class MyBNInferencer {
	//AIMA Fig. 14.9
	public double EnumA(BayesianNetwork b, List<RandomVariable> l, Assignment a){
		ArrayList<RandomVariable> v = new ArrayList<RandomVariable>();
		for(int i = 0;i < l.size(); i++) v.add(l.get(i));
		if(v.size() == 0) return 1.0;
		RandomVariable f = v.get(0);
		v.remove(f);
		if(a.containsKey(f)){
			return b.getProbability(f, a) * EnumA(b,v,a);
		}else{
			double ret = 0.0;
			for(int i = 0; i < f.d.size(); i++){
				Assignment assign = new Assignment(a);
				assign.set(f, f.d.get(i));
				ret += (b.getProbability(f, assign) * EnumA(b, v, assign));
			}
			return ret;
		}
	}
	public Distribution ask(BayesianNetwork b, RandomVariable r, Assignment a){
		Distribution dis = new Distribution(r);
		for(int i = 0; i < r.d.size(); i++){
			Assignment assign = new Assignment(a);
			assign.set(r, r.d.get(i));
			dis.put(r.d.get(i),EnumA(b, b.getVariableListTopologicallySorted(), assign));
		}
		dis.normalize();
		return dis;
	}
	public static void Main(String[] args) {
		// file
		String f = args[0];
		// query
		String q = "";
		if(args.length >= 2) q = args[1];
		// # of evidence
		int n = (args.length - 2)/2;
		// evi name
		String[] eN = new String[n];
		// evi val
		String[] eV = new String[n];
		//init 
		for(int i=0; i<n; i++){
			eN[i] = args[2+i*2];
			eV[i] = args[3+i*2];
		}
		BayesianNetwork b = new BayesianNetwork();
		//either xml or bif
		if(f.substring(f.length()-3).equals("xml")){
			XMLBIFParser x = new XMLBIFParser();
			try{
				b = x.readNetworkFromFile(f);
			}catch(IOException e) {
				System.out.println("xml IO: " + e.getMessage());
			}catch(SAXException e){
				System.out.println("xml SAX: " + e.getMessage());
			}catch(ParserConfigurationException e){
				System.out.println("xml parser: " + e.getMessage());
			}
		}else{
			try{
				FileInputStream i = new FileInputStream(f);
				BIFLexer l = new BIFLexer(i);
				BIFParser p = new BIFParser(l);
				b = p.parseNetwork();
			}catch(IOException e){
				System.out.println("bif IO:" + e.getMessage());
			}
		}
		RandomVariable r = b.getVariableByName(q);
		Assignment a = new Assignment();
		for(int i = 0; i < n; i++){
			a.set(b.getVariableByName(eN[i]), eV[i]);
		}
		MyBNInferencer m = new MyBNInferencer();
		Distribution d = m.ask(b, r, a);
		System.out.println(d.toString());
	}
	public static void main(String[] args){
		Main(args);
	}
}