package pl.kbaranski.ldappasschange.utils;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.DirectoryService;
import org.apache.directory.server.core.partition.Partition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmIndex;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.schema.SchemaPartition;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.directory.server.xdbm.Index;
import org.apache.directory.shared.ldap.entry.Entry;
import org.apache.directory.shared.ldap.entry.ServerEntry;
import org.apache.directory.shared.ldap.exception.LdapException;
import org.apache.directory.shared.ldap.exception.LdapInvalidDnException;
import org.apache.directory.shared.ldap.name.DN;
import org.apache.directory.shared.ldap.schema.SchemaManager;
import org.apache.directory.shared.ldap.schema.ldif.extractor.SchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.ldif.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.shared.ldap.schema.loader.ldif.LdifSchemaLoader;
import org.apache.directory.shared.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.shared.ldap.schema.registries.SchemaLoader;
import org.apache.directory.shared.ldap.util.StringTools;

public class LdapServerInstance {

    /** The directory service */
    private DirectoryService service;

    /** The LDAP server */
    private LdapServer server;

    /**
     * Add a new partition to the server
     * 
     * @param partitionId
     *            The partition Id
     * @param partitionDn
     *            The partition DN
     * @return The newly added partition
     * @throws Exception
     *             If the partition can't be added
     */
    public Partition addPartition(String partitionId, String partitionDn) throws Exception {
        // Create a new partition named 'foo'.
        JdbmPartition partition = new JdbmPartition();
        partition.setId(partitionId);
        partition.setPartitionDir(new File(service.getWorkingDirectory(), partitionId));
        partition.setSuffix(partitionDn);
        service.addPartition(partition);

        return partition;
    }

    /**
     * Add a new set of index on the given attributes
     * 
     * @param partition
     *            The partition on which we want to add index
     * @param attrs
     *            The list of attributes to index
     */
    private void addIndex(Partition partition, String... attrs) {
        // Index some attributes on the apache partition
        HashSet<Index<?, ServerEntry, Long>> indexedAttributes = new HashSet<Index<?, ServerEntry, Long>>();

        for (String attribute : attrs) {
            indexedAttributes.add(new JdbmIndex<String, ServerEntry>(attribute));
        }

        ((JdbmPartition) partition).setIndexedAttributes(indexedAttributes);
    }

    /**
     * initialize the schema manager and add the schema partition to diectory
     * service
     * 
     * @throws Exception
     *             if the schema LDIF files are not found on the classpath
     */
    private void initSchemaPartition() throws Exception {
        SchemaPartition schemaPartition = service.getSchemaService().getSchemaPartition();

        // Init the LdifPartition
        LdifPartition ldifPartition = new LdifPartition();
        String workingDirectory = service.getWorkingDirectory().getPath();
        ldifPartition.setWorkingDirectory(workingDirectory + "/schema");

        // Extract the schema on disk (a brand new one) and load the registries
        File schemaRepository = new File(workingDirectory, "schema");
        SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(new File(workingDirectory));
        extractor.extractOrCopy(true);

        schemaPartition.setWrappedPartition(ldifPartition);

        SchemaLoader loader = new LdifSchemaLoader(schemaRepository);
        SchemaManager schemaManager = new DefaultSchemaManager(loader);
        service.setSchemaManager(schemaManager);

        // We have to load the schema now, otherwise we won't be able
        // to initialize the Partitions, as we won't be able to parse
        // and normalize their suffix DN
        schemaManager.loadAllEnabled();

        schemaPartition.setSchemaManager(schemaManager);

        List<Throwable> errors = schemaManager.getErrors();

        if (errors.size() != 0) {
            throw new Exception("Schema load failed : " + errors);
        }
    }

    /**
     * Initialize the server. It creates the partition, adds the index, and
     * injects the context entries for the created partitions.
     * 
     * @param workDir
     *            the directory to be used for storing the data
     * @throws Exception
     *             if there were some problems while initializing the system
     */
    private void initDirectoryService(File workDir) throws Exception {
        // Initialize the LDAP service
        service = new DefaultDirectoryService();
        service.setWorkingDirectory(workDir);
        service.setAccessControlEnabled(false);
        // first load the schema
        initSchemaPartition();

        // then the system partition
        // this is a MANDATORY partition
        Partition systemPartition = addPartition("system", ServerDNConstants.SYSTEM_DN);
        service.setSystemPartition(systemPartition);

        // Disable the ChangeLog system
        service.getChangeLog().setEnabled(false);
        service.setDenormalizeOpAttrsEnabled(true);

        // Now we can create as many partitions as we need
        // Create some new partitions named 'foo', 'bar' and 'apache'.
        Partition partition = addPartition("example", "dc=example,dc=com");

        // Index some attributes on the apache partition
        addIndex(partition, "objectClass", "ou", "uid");

        // And start the service
        service.startup();

        // Inject the apache root entry
        if (!service.getAdminSession().exists(partition.getSuffixDn())) {
            DN rootDn = new DN("dc=example,dc=com");
            ServerEntry entry = service.newEntry(rootDn);
            entry.add("objectClass", "top", "domain", "extensibleObject");
            entry.add("dc", "Apache");
            service.getAdminSession().add(entry);
        }
    }

    public void addUser(String dn, Map<String, String> attributes) throws NamingException, LdapException, Exception {
        DN dn2 = new DN(dn);
        if (!service.getAdminSession().exists(dn2)) {
            ServerEntry entry = service.newEntry(dn2);
            // FIXME: Dodać jeszcze (objectClass=posixAccount)
            entry.add("objectClass", "top", "person", "organizationalPerson", "inetOrgPerson");
            for (String attributeName : attributes.keySet()) {
                if ("userPassword".equals(attributeName)) {
                    entry.add(attributeName, StringTools.getBytesUtf8(attributes.get(attributeName)));
                } else {
                    entry.add(attributeName, attributes.get(attributeName));
                }
            }
            service.getAdminSession().add(entry);
        }
    }

    /**
     * Creates a new instance of EmbeddedADS. It initializes the directory
     * service.
     * 
     * @throws Exception
     *             If something went wrong
     */
    public LdapServerInstance(File workDir) throws Exception {
        initDirectoryService(workDir);
    }

    /**
     * starts the LdapServer
     * 
     * @throws Exception
     */
    public void startServer(int serverPort) throws Exception {
        server = new LdapServer();
        server.setTransports(new TcpTransport(serverPort));
        server.setDirectoryService(service);

        server.start();
    }

    /**
     * Main class.
     * 
     * @param args
     *            Not used.
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        LdapServerInstance ads = null;
        try {
            File workDir = new File(System.getProperty("java.io.tmpdir") + "/server-work");
            workDir.mkdirs();

            // Create the server
            ads = new LdapServerInstance(workDir);

            // Read an entry
            Entry result = ads.service.getAdminSession().lookup(new DN("uid=admin,dc=example,dc=com"));

            // And print it if available
            System.out.println("Found entry : " + result);
        } catch (Exception e) {
            // Ok, we have something wrong going on ...
            e.printStackTrace();
        } finally {
        }
    }

    public void shutdown() throws Exception {
        server.stop();
        service.shutdown();
    }

    public void printDN(String dn) throws LdapInvalidDnException, Exception {
        // Read an entry
        Entry result = service.getAdminSession().lookup(new DN(dn));

        // And print it if available
        System.out.println("Found entry : " + result);
    }
}
