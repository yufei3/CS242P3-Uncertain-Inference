import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
class Weight {
	public double w;
	public Assignment a;
}
public class MyBNApproxInferencer {
	public Assignment pre(BayesianNetwork b){
		Assignment a = new Assignment();
		for(RandomVariable r : b.getVariableListTopologicallySorted()){
			Distribution d = new Distribution();
			for(int i = 0; i < r.d.size(); i++){
				Assignment assign = new Assignment(a); //or maybe include builder
				assign.set(r, r.d.get(i));
				d.put(r.d.get(i), b.getProbability(r, assign));
			}
			d.normalize();
			double z = Math.random();
			double q = 0.0;
			for(int i=0; i < r.d.size(); i++){
				q += d.get(r.d.get(i));
				if(z <= q){
					a.set(r, r.d.get(i));
					break;
				}
			}
		}
		return a;
	}
	//rejection sampling
	public Distribution R(BayesianNetwork b, RandomVariable r, Assignment a,int n){
		Distribution d = new Distribution(r);
		int[] counts = new int[r.d.size()];
		for(int j = 1; j <= n; j++){
			Assignment assign = pre(b);
			boolean consistent = true;
			for(RandomVariable r1: a.keySet()){
				for(RandomVariable r2 : assign.keySet()){
					if((r1.getN()).equals(r2.getN()) && !(a.get(r1)).equals(assign.get(r2))){
						consistent = false;
					}
				}
			}
			if(consistent){
				for(int i = 0; i < r.d.size(); i++){
					if(assign.get(r).equals(r.d.get(i))){
						counts[i]++;
					}
				}
			}
		}
		for(int i=0;i<r.d.size();i++){
			d.put(r.d.get(i), counts[i]);
		}
		d.normalize();
		return d;
	}
	public Weight W(BayesianNetwork b, Assignment a){
		Weight wei = new Weight();
		wei.a = new Assignment();
		wei.w = 1;
		for(RandomVariable r : b.getVariableListTopologicallySorted()){
			if(a.containsKey(r)){
				wei.a.put(r, a.get(r));
				wei.w *= b.getProbability(r, wei.a);
			}else{
				Distribution d = new Distribution();
				for(int i = 0; i <r.d.size(); i++){
					Assignment assign = new Assignment(wei.a);
					assign.set(r, r.d.get(i));
					d.put(r.d.get(i), b.getProbability(r, assign));
				}
				d.normalize();
				double v = Math.random();
				double q = 0.0;
				for(int i=0; i < r.d.size(); i++){
					q += d.get(r.d.get(i));
					if(v <= q){
						wei.a.set(r, r.d.get(i));
						break;
					}
				}
			}
		}
		return wei;
	}
	//likelihood weighting
	public Distribution L(BayesianNetwork b, RandomVariable r, Assignment a, int n){
		Distribution d = new Distribution(r);
		for(int i=0;i<r.d.size();i++){
			d.put(r.d.get(i), 0);
		}
		for(int j=1;j<=n;j++){
			Weight ws = W(b,a);
			d.put(ws.a.get(r), d.get(ws.a.get(r)) + ws.w);
		}
		d.normalize();
		return d;
	}
	public static Set<RandomVariable> getParents(RandomVariable r, BayesianNetwork b){
		Set<RandomVariable> s = new ArraySet<RandomVariable>();
		 for(RandomVariable rv : b.getVarList()) {
			 if(b.getChildren(rv).contains(r)) {
				 s.add(rv);
			 }
		 }
		s.add(r);
		return s;
	}
	public static Assignment getAssignment(Set<RandomVariable> s, Assignment a) {
		Assignment assign = new Assignment();
		for(Entry<RandomVariable, Object> e : a.entrySet()) {
			for(RandomVariable r : s) {
				if(e.getKey().equals(r)) {
					assign.put(r, e.getValue());
				}
			}
		}
		return assign;
	}
	//Gibbs
	public Distribution G(BayesianNetwork b, RandomVariable r, Assignment a, int n) {
		Random ra = new Random();
		Distribution d = new Distribution();
		for(Object o : r.getDomain()) {
			d.put(o, 0);
		}
		ArrayList<RandomVariable> v = (ArrayList<RandomVariable>) b.getVariableListTopologicallySorted();
		ArrayList<RandomVariable> q = new ArrayList<RandomVariable>(); 
		for(RandomVariable rv : v) {
			if(!a.variableSet().contains(rv)) {
				q.add(rv);
			}
		}
		Assignment assign = a.copy();
		for(RandomVariable rv : q) {
			Domain dom = rv.getDomain();
			assign.put(rv, dom.get(ra.nextInt(dom.size())));
		}
		for(int i = 0; i < n; i++) {
			for(RandomVariable rv : q) {
				Distribution dis = new Distribution();
				for(Object o : rv.getDomain()) {
					assign.put(rv, o);
					Set<RandomVariable> p = getParents(rv, b);
					Assignment z = getAssignment(p, assign);
					double w = b.getProbability(rv, z);
					for(RandomVariable c : b.getChildren(rv)) {
						Set<RandomVariable> pp = getParents(c, b);
						Assignment x = getAssignment(pp, assign);
						w *= b.getProbability(c, x);
					}
					dis.put(o, w);
				}
				dis.normalize();
				double ran = ra.nextDouble();
				double e = 0;
				for(Object oo : rv.getDomain()) {
					double f = dis.get(oo);
					e += f;
					if(ran <= e) {
						assign.put(rv, oo);
						break;
					}
				}
				Object obj = assign.get(r);
				d.put(obj, d.get(obj) + 1);
			}
		}
		d.normalize();
		return d;
	}
	public static void Main(String[] args) {
		// # sample
		int n = Integer.parseInt(args[0]);
		// file
	    String f = args[1];
	    // query
		String q = args[2];
		// # evi
		int nEvi = (args.length-3)/2;
		String[] eN = new String[nEvi];
		String[] eV = new String[nEvi];
		for(int i=0;i<nEvi;i++){
			eN[i] = args[i*2 + 3];
			eV[i] = args[i*2 + 4];
		}
		BayesianNetwork b = new BayesianNetwork();
		if(f.substring(f.length()-3).equals("xml")){
			XMLBIFParser x = new XMLBIFParser();
			try{
				b = x.readNetworkFromFile(f);
			}catch(IOException e) {
				System.out.println("xml IO: " + e.getMessage());
			}catch(SAXException e) {
				System.out.println("xml SAX: " + e.getMessage());
			}catch(ParserConfigurationException e){
				System.out.println("xml Par: " + e.getMessage());
			}
		}else{
			try{
				FileInputStream in = new FileInputStream(f);
				BIFLexer l = new BIFLexer(in);
				BIFParser p = new BIFParser(l);
				b = p.parseNetwork();
			}catch(IOException e){
				System.out.println("bif IO: " + e.getMessage());
			}
		}
		RandomVariable r = b.getVariableByName(q);
		Assignment a = new Assignment();
		for(int i = 0; i < nEvi; i++){
			a.set(b.getVariableByName(eN[i]), eV[i]);
		}
		MyBNApproxInferencer tester = new MyBNApproxInferencer();
		System.out.println("Rejection Sampling: ");
		Distribution d = tester.R(b,r,a,n);
		System.out.println(d.toString());
		System.out.println("Likelihood Weighting:");
		d = tester.L(b,r,a,n);
		System.out.println(d.toString());
		System.out.println("Gibbs Sampling:");
		d = tester.G(b,r,a,n);
		System.out.println(d.toString());
		
	}
	public static void main(String[] args){
		Main(args);
	}
}