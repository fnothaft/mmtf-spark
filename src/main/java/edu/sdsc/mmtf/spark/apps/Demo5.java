/**
 * 
 */
package edu.sdsc.mmtf.spark.apps;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.rcsb.mmtf.api.StructureDataInterface;

import edu.sdsc.mmtf.spark.filters.ExperimentalMethodsFilter;
import edu.sdsc.mmtf.spark.filters.Resolution;
import edu.sdsc.mmtf.spark.filters.Rfree;
import edu.sdsc.mmtf.spark.io.MmtfSequenceFileReader;
import edu.sdsc.mmtf.spark.io.MmtfSequenceFileWriter;

/**
 * @author peter
 *
 */
public class Demo5 {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

	    if (args.length != 1) {
	        System.err.println("Usage: " + Demo5.class.getSimpleName() + " <hadoop sequence file>");
	        System.exit(1);
	    }
	    
	    long start = System.nanoTime();
	    
	    SparkConf conf = new SparkConf().setMaster("local[*]").setAppName(Demo5.class.getSimpleName());
	    JavaSparkContext sc = new JavaSparkContext(conf);
		 
	    // read PDB in MMTF format
	    JavaPairRDD<String, StructureDataInterface> pdb = MmtfSequenceFileReader.read(args[0],  sc);

	    // count number of atoms
	    pdb = pdb
	    		.filter(new ExperimentalMethodsFilter("X-RAY DIFFRACTION"))
	    		.filter(new Resolution(0, 2))
	    		.filter(new Rfree(0, 0.25));
	    
	    MmtfSequenceFileWriter.write(args[0]+"_xray", sc, pdb);
	    
	    System.out.println("# structures: " + pdb.count());
	  
	    long end = System.nanoTime();
	    
	    System.out.println("Time:     " + (end-start)/1E9 + "sec.");
	    
	    sc.close();
	}

}