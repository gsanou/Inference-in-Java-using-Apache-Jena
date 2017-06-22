import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.log4j.varia.NullAppender;

import java.util.Iterator;

/**
 * Created by Zamora on 19/06/2017.
 */
public class MainPizza {

    public static void main(String args[]) {
        org.apache.log4j.BasicConfigurator.configure(new NullAppender());

        System.out.println("Inferencias en la Ontología de Pizza");

        OntModel base = ModelFactory.createOntologyModel();     // crea una ontología en memoria
        base.read("./src/assets/pizza.owl", "RDF/XML");         // carga el xml a la ontología

        //create a pizza example by TEC for this example
        String pizza_NS = "http://www.co-ode.org/ontologies/pizza/pizza.owl#";  // NameSpace, vocabulario que usa el razonador

        /*
        URI, Identificador de la clase
        Parámetros: URI de la clase existente
         */
        OntClass pizza = base.getOntClass(pizza_NS + "Pizza");

        /*
         Se crea una entidad, individuo, instancia de la clase
         Parámetros: (URI del nuevo individuo, URI de la clase)
         */
        Individual pizzaTec = base.createIndividual(pizza_NS + "Pizza_a_lo_TEC", pizza);

        OntClass thinAndCrispyBase = base.getOntClass(pizza_NS + "ThinAndCrispyBase");
        Individual baseTec = base.createIndividual(pizza_NS + "base_delgada", thinAndCrispyBase);

        OntClass fourCheesesTopping = base.getOntClass(pizza_NS + "FourCheesesTopping");
        Individual toppingTec = base.createIndividual(pizza_NS + "cuatro_quesos", fourCheesesTopping);

        /*
        Pasar a memoria un object property existente en la ontología
        Parámetros: URI del object property
         */
        ObjectProperty hasBase = base.getObjectProperty(pizza_NS + "hasBase");
        ObjectProperty hasTopping = base.getObjectProperty(pizza_NS + "hasTopping");

        /*
        Agregar al individuo de pizza un object property y el otro individuo con el que se relaciona por medio del object property
        Parámetros: (ObjectProperty, otroIndividuo)
         */
        pizzaTec.addProperty(hasBase, baseTec);
        pizzaTec.addProperty(hasTopping, toppingTec);

        /*
        Create the reasoning model using the base
        Crea un nuevo modelo y le da una instrucción con un razonador más potente con base en un modelo anterior
        Parámetros: (Configuración del modelo, modelo anterior)
        OWL_MEM_MICRO_RULE_INF =
            Una especificación para los modelos de OWL que se almacenan en memoria y utilizan el micro motor
            de inferencia de las reglas de OWL para las implicaciones adicionales
         */
        OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, base);

        /*
        Se quiere inferir información del individuo de pizza
         */
        pizzaTec = inf.getIndividual(pizza_NS + "Pizza_a_lo_TEC");
        listTypes(pizzaTec, true);
        listProperty(pizzaTec, true);
    }


    /**
     * Enlista e imprime las propiedades (relaciones) que posee un individuo
     * @param resource = Un objeto Resource que representa el identificador del recurso
     * @param stripUri Un valor Boolean que indica si desea ver las URI acortadas
     */
    public static void listProperty(Resource resource, boolean stripUri) {
        Statement statement;
        System.out.println("====== Properties of " + resource.getLocalName() + " ======");
        for (StmtIterator i = resource.listProperties(); i.hasNext();) {
            statement = i.next();
            if (stripUri) {
                if (statement.getObject().isLiteral())
                    System.out.println("resource: " + statement.getPredicate().getLocalName() + " => " + statement.getObject().asLiteral().toString());
                else if (statement.getObject().isResource() && statement.getObject().asResource().getLocalName() != null)
                    System.out.println("resource: " + statement.getPredicate().getLocalName() + " => " + statement.getObject().asResource().getLocalName());
            }
            else
                System.out.println(statement);
        }
        System.out.println("==================================================================");
    }


    /**
     * Enlista e imprime los tipos que posee un individuo
     * @param individual Un objeto Individual que representa el identificador del individuo
     * @param stripUri Un valor Boolean que indica si desea ver las URI acortadas
     */
    public static void listTypes(Individual individual, boolean stripUri) {
        Resource resource;
        System.out.println("====== Types of " + individual.getLocalName() + " ======");
        for (Iterator<Resource> i = individual.listRDFTypes(true); i.hasNext();) {
            resource = i.next();
            if (resource.getLocalName() == null)
                continue;
            if (stripUri)
                System.out.println("Tipo: " + resource.getLocalName());
            else
                System.out.println("Tipo: " + resource);
        }
        System.out.println("==================================================================");
    }
}
