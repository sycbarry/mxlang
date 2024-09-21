

package lang;

import lang.modules.*;
import lang.packages.report.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.FileReader;


import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.*;

class KubernetesClientTest {
    @Test
    public void test1() {
        Logger logger = LoggerFactory.getLogger(KubernetesClientTest.class);
        // String kubeconfigPath = "/Users/sebastianbarry/.kube/zingg-prod-kubeconfig.yaml";
        // Config config = new ConfigBuilder().withKubeconfig(kubeconfigPath).build();
        String podName = "zingg-api-6bc8d96d7-6g8s7";
        String namespace = "zingg";
        // String kubeconfigPath = "/Users/sebastianbarry/.kube/zingg-prod-kubeconfig.yaml";
        // Config config = new ConfigBuilder().withKubeconfig(kubeconfigPath).build();
        // KubernetesClient client = new DefaultKubernetesClient(config);
        // try (KubernetesClient client = new KubernetesClientBuilder().build()) {
          //  System.out.println(client);
        //}
        //client.close();
        System.out.println("Log of pod " + podName + " in " + namespace + " is:");
        System.out.println("----------------------------------------------------------------");
        try (
            KubernetesClient client = new KubernetesClientBuilder().build();
            LogWatch ignore = client.pods().inNamespace(namespace).withName(podName).tailingLines(10).watchLog(System.out)) {
            // Thread.sleep(Integer.MAX_VALUE);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }
}
