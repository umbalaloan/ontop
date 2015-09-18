package org.semanticweb.ontop.owlrefplatform.owlapi3.example;

/*
 * #%L
 * ontop-quest-owlapi3
 * %%
 * Copyright (C) 2009 - 2014 Free University of Bozen-Bolzano
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.io.*;

import com.google.inject.Guice;
import com.google.inject.Injector;
import org.semanticweb.ontop.injection.NativeQueryLanguageComponentFactory;
import org.semanticweb.ontop.injection.OBDACoreModule;
import org.semanticweb.ontop.injection.OBDAProperties;
import org.semanticweb.ontop.mapping.MappingParser;
import org.semanticweb.ontop.model.OBDAModel;
import org.semanticweb.ontop.owlapi3.QuestOWLIndividualAxiomIterator;
import org.semanticweb.ontop.owlrefplatform.owlapi3.OWLAPI3Materializer;
import org.semanticweb.owlapi.model.OWLIndividualAxiom;

/**
 * A very simple example that shows how to generate triples in an N-Triple file,
 * using the data of a database and a set of mappings from the OBDA file.
 * 
 * NOTE: This process does not involve reasoning since no ontology is given. The
 * result are the DIRECT triples generated by the mappings.
 */
public class ABoxMaterializerExample {

	/*
	 * Use the sample database using H2 from
	 * https://babbage.inf.unibz.it/trac/obdapublic/wiki/InstallingTutorialDatabases
	 */
	final String inputFile = "src/main/resources/example/exampleBooks.obda";
	final String outputFile = "src/main/resources/example/exampleBooks.txt";
	
	public void generateTriples() throws Exception {

        /**
         * Factory initialization
         */
        Injector injector = Guice.createInjector(new OBDACoreModule(new OBDAProperties()));
        NativeQueryLanguageComponentFactory factory = injector.getInstance(NativeQueryLanguageComponentFactory.class);

        /*
         * Load the OBDA model from an external .obda file
         */
        MappingParser mappingParser = factory.create(new FileReader(inputFile));
        OBDAModel obdaModel = mappingParser.getOBDAModel();

		/*
		 * Start materializing data from database to triples.
		 */

		// TODO: try the streaming mode.
		OWLAPI3Materializer materializer = new OWLAPI3Materializer(obdaModel, false);
		
		long numberOfTriples = materializer.getTriplesCount();
		System.out.println("Generated triples: " + numberOfTriples);

		/*
		 * Obtain the triples iterator
		 */
		QuestOWLIndividualAxiomIterator triplesIter = materializer.getIterator();
		
		/*
		 * Print the triples into an external file.
		 */
		File fout = new File(outputFile);
		if (fout.exists()) {
			fout.delete(); // clean any existing output file.
		}
		
		PrintWriter out = null;
		try {
		    out = new PrintWriter(new BufferedWriter(new FileWriter(fout, true)));
			while (triplesIter.hasNext()) {
				OWLIndividualAxiom individual = triplesIter.next();
				out.println(individual.toString());
			}
			out.flush();
		} finally {
		    if (out != null) {
		    	out.close();
		    }
		    materializer.disconnect();
		}
	}

	public static void main(String[] args) {
		try {
			ABoxMaterializerExample example = new ABoxMaterializerExample();
			example.generateTriples();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
