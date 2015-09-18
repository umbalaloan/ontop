package org.semanticweb.ontop.obda;

import org.junit.Before;
import org.junit.Test;
import org.semanticweb.ontop.owlrefplatform.core.QuestPreferences;
import org.semanticweb.ontop.owlrefplatform.owlapi3.*;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.SimpleConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Class to test if CONCAT in mapping is working properly.
 * rdfs:label is a concat of literal and variables
 *

 */
public class ConcatMappingTest {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private OWLOntology ontology;

    final String owlFile = "src/test/resources/northwind/mapping-northwind.owl";
    final String obdaFile = "src/test/resources/northwind/mapping-northwind.obda";

    @Before
    public void setUp() throws Exception {
        // Loading the OWL file
        OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
        ontology = manager.loadOntologyFromOntologyDocument((new File(owlFile)));
    }


    @Test
    public void testConcatQuery() throws Exception {

        Properties p = new Properties();

//
        String queryBind = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>  \n" +
                "\n" +
                "SELECT  ?f ?y " +
                "WHERE {?f rdfs:label ?y .} \n";



        int results = runTestQuery(p, queryBind);
        assertEquals(9, results);
    }


    private int runTestQuery(Properties p, String query) throws Exception {

        // Creating a new instance of the reasoner
        QuestOWLFactory factory = new QuestOWLFactory(new File(obdaFile), new QuestPreferences(p));

        QuestOWL reasoner = factory.createReasoner(ontology, new SimpleConfiguration());

        // Now we are ready for querying
        QuestOWLConnection conn = reasoner.getConnection();
        QuestOWLStatement st = conn.createStatement();


                log.debug("Executing query: ");
                log.debug("Query: \n{}", query);

                long start = System.nanoTime();
                QuestOWLResultSet res = st.executeTuple(query);
                long end = System.nanoTime();

                double time = (end - start) / 1000;

                int count = 0;
                while (res.nextRow()) {
                    count += 1;
                    for (int i = 1; i <= res.getColumnCount(); i++) {
                         log.debug(res.getSignature().get(i-1) + "=" + res.getOWLObject(i));

                      }
                }
                log.debug("Total result: {}", count);

                assertFalse(count == 0);

                log.debug("Elapsed time: {} ms", time);

        return count;



    }


}

