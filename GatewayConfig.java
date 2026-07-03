import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * GatewayConfig - Singleton configuration manager for PesaLink
 * Uses lazy initialization for efficient resource usage
 */
public final class GatewayConfig {
    // Private static instance - initially null (lazy initialization)
    private static GatewayConfig instance;
    
    // Configuration properties
    private String gatewayHost;
    private int gatewayPort;
    private String apiKey;
    private String smsProvider;
    private String emailProvider;
    private String whatsappApiKey;
    private int timeoutMilliseconds;
    private boolean sslEnabled;
    
    // Private constructor prevents external instantiation
    private GatewayConfig() {
        // Load default configuration
        loadDefaults();
        // Try to load from file
        loadFromFile("config.properties");
    }
    
    // Lazy initialization - creates instance only when needed
    public static synchronized GatewayConfig getInstance() {
        if (instance == null) {
            instance = new GatewayConfig();
            System.out.println("🔧 GatewayConfig initialized (lazy loading)");
        }
        return instance;
    }
    
    // Load default configuration values
    private void loadDefaults() {
        this.gatewayHost = "api.pesalink.co.ug";
        this.gatewayPort = 443;
        this.apiKey = "default-api-key-please-change";
        this.smsProvider = "SMSGateway";
        this.emailProvider = "SendGrid";
        this.whatsappApiKey = "default-whatsapp-key";
        this.timeoutMilliseconds = 30000;
        this.sslEnabled = true;
    }
    
    // Load configuration from properties file
    private void loadFromFile(String filename) {
        Properties props = new Properties();
        try (FileInputStream input = new FileInputStream(filename)) {
            props.load(input);
            
            // Load properties if present in file
            this.gatewayHost = props.getProperty("gateway.host", gatewayHost);
            this.gatewayPort = Integer.parseInt(props.getProperty("gateway.port", String.valueOf(gatewayPort)));
            this.apiKey = props.getProperty("gateway.api.key", apiKey);
            this.smsProvider = props.getProperty("sms.provider", smsProvider);
            this.emailProvider = props.getProperty("email.provider", emailProvider);
            this.whatsappApiKey = props.getProperty("whatsapp.api.key", whatsappApiKey);
            this.timeoutMilliseconds = Integer.parseInt(props.getProperty("timeout.ms", String.valueOf(timeoutMilliseconds)));
            this.sslEnabled = Boolean.parseBoolean(props.getProperty("ssl.enabled", String.valueOf(sslEnabled)));
            
            System.out.println(" Configuration loaded from: " + filename);
        } catch (IOException e) {
            System.out.println("ℹ Using default configuration (no config file found)");
        }
    }
    
    // Getters - no setters to maintain immutability
    public String getGatewayHost() {
        return gatewayHost;
    }
    
    public int getGatewayPort() {
        return gatewayPort;
    }
    
    public String getApiKey() {
        return apiKey;
    }
    
    public String getSmsProvider() {
        return smsProvider;
    }
    
    public String getEmailProvider() {
        return emailProvider;
    }
    
    public String getWhatsappApiKey() {
        return whatsappApiKey;
    }
    
    public int getTimeoutMilliseconds() {
        return timeoutMilliseconds;
    }
    
    public boolean isSslEnabled() {
        return sslEnabled;
    }
    
    // Utility methods
    public String getFullGatewayUrl() {
        String protocol = sslEnabled ? "https" : "http";
        return protocol + "://" + gatewayHost + ":" + gatewayPort;
    }
    
    public boolean isProduction() {
        return !gatewayHost.contains("staging") && !gatewayHost.contains("test");
    }
    
    @Override
    public String toString() {
        return "GatewayConfig{" +
                "gatewayHost='" + gatewayHost + '\'' +
                ", gatewayPort=" + gatewayPort +
                ", smsProvider='" + smsProvider + '\'' +
                ", emailProvider='" + emailProvider + '\'' +
                ", timeoutMs=" + timeoutMilliseconds +
                ", sslEnabled=" + sslEnabled +
                ", environment=" + (isProduction() ? "PRODUCTION" : "STAGING") +
                '}';
    }
    
    // Thread-safe alternative: Double-checked locking
    public static GatewayConfig getInstanceThreadSafe() {
        if (instance == null) {
            synchronized (GatewayConfig.class) {
                if (instance == null) {
                    instance = new GatewayConfig();
                }
            }
        }
        return instance;
    }
}

// Demonstration
class GatewayConfigDemo {
    public static void main(String[] args) {
        System.out.println("=== PesaLink Gateway Configuration ===\n");
        
        // Get singleton instance
        GatewayConfig config1 = GatewayConfig.getInstance();
        GatewayConfig config2 = GatewayConfig.getInstance();
        
        // Verify it's the same instance
        System.out.println("config1 == config2: " + (config1 == config2));
        System.out.println("config1 hash: " + System.identityHashCode(config1));
        System.out.println("config2 hash: " + System.identityHashCode(config2));
        
        System.out.println("\n=== Configuration Details ===");
        System.out.println("Host: " + config1.getGatewayHost());
        System.out.println("Port: " + config1.getGatewayPort());
        System.out.println("Full URL: " + config1.getFullGatewayUrl());
        System.out.println("SMS Provider: " + config1.getSmsProvider());
        System.out.println("Email Provider: " + config1.getEmailProvider());
        System.out.println("Timeout: " + config1.getTimeoutMilliseconds() + "ms");
        System.out.println("SSL Enabled: " + config1.isSslEnabled());
        System.out.println("Environment: " + (config1.isProduction() ? "PRODUCTION" : "STAGING"));
        
        System.out.println("\n=== Config toString ===");
        System.out.println(config1);
        
        // Test with multiple threads
        System.out.println("\n=== Thread-Safety Test ===");
        Runnable task = () -> {
            GatewayConfig config = GatewayConfig.getInstance();
            System.out.println(Thread.currentThread().getName() + 
                             " - instance hash: " + System.identityHashCode(config));
        };
        
        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");
        
        t1.start();
        t2.start();
        t3.start();
    }
}