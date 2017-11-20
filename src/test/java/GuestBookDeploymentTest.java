import io.fabric8.kubernetes.api.model.IntOrString;
import io.fabric8.kubernetes.api.model.Service;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.client.KubernetesClient;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.arquillian.cube.kubernetes.annotations.Named;
import org.arquillian.cube.kubernetes.annotations.PortForward;
import org.arquillian.cube.kubernetes.api.Session;
import org.arquillian.cube.kubernetes.impl.requirement.RequiresKubernetes;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static io.fabric8.kubernetes.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(ArquillianConditionalRunner.class)
@RequiresKubernetes
public class GuestBookDeploymentTest {

    @ArquillianResource
    KubernetesClient client;

    @ArquillianResource
    Session session;

    @Named("frontend")
    @ArquillianResource
    Service frontend;

    @Named("redis-master")
    @ArquillianResource
    Service redisMaster;

    @Named("redis-slave")
    @ArquillianResource
    Service redisSlave;

    @Named("frontend")
    @PortForward
    @ArquillianResource
    URL url;

    @Test
    public void should_verify_redis_master_service_instance_is_started() throws IOException {
        ServicePort servicePort = getServicePort(6379);

        assertThat(client.services().withName("redis-master").get())
            .isNotNull();
        assertThat(client.services().withName("redis-master").get().getSpec())
            .isNotNull()
            .hasPorts(servicePort)
            .hasType("ClusterIP");
    }

    @Test
    public void should_verify_one_redis_master_pod_instance_is_started() throws IOException {
        assertThat(client).podsForService(redisMaster)
            .runningStatus()
            .filterNamespace(session.getNamespace())
            .hasSize(1);
    }

    @Test
    public void should_verify_redis_slave_service_instance_is_started() throws IOException {
        ServicePort servicePort = getServicePort(6379);

        assertThat(client.services().withName("redis-slave").get())
            .isNotNull();
        assertThat(client.services().withName("redis-slave").get().getSpec())
            .isNotNull()
            .hasPorts(servicePort)
            .hasType("ClusterIP");
    }

    @Test
    public void should_verify_two_redis_slave_pods_instances_are_started() throws IOException {
        assertThat(client).podsForService(redisSlave)
            .runningStatus()
            .filterNamespace(session.getNamespace())
            .hasSize(2);
    }

    @Test
    public void should_verify_guestbook_frontend_service_instance_is_started() throws IOException {
        final Integer nodePort = frontend.getSpec().getPorts().stream().map(ServicePort::getNodePort).findFirst().get();
        ServicePort servicePort = getServicePort(80);
        servicePort.setNodePort(nodePort);

        Map<String, String> labels = new HashMap<>();
        labels.put("tier", "frontend");
        labels.put("app", "guestbook");

        assertThat(client.services().withName("frontend").get())
            .isNotNull();
        assertThat(client.services().withName("frontend").get().getSpec())
            .isNotNull()
            .hasPorts(servicePort)
            .hasType("NodePort");
        assertThat(client.services().withName("frontend").get().getMetadata())
            .hasName("frontend")
            .hasLabels(labels);
    }

    @Test
    public void should_verify_three_guestbook_frontend_pods_instances_are_started() throws IOException {
        assertThat(client).podsForService(frontend)
            .runningStatus()
            .filterNamespace(session.getNamespace())
            .hasSize(3);
    }

    @Test
    public void should_verify_guestbook_frontend_is_exposed() throws IOException {
        assertNotNull(url);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();

        Response response = okHttpClient.newCall(request).execute();

        assertNotNull(response);
        assertEquals(200, response.code());
        assertTrue(response.body().string().contains("Guestbook"));
    }

    private ServicePort getServicePort(Integer port) {
        ServicePort servicePort = new ServicePort();
        servicePort.setPort(port);
        servicePort.setProtocol("TCP");
        servicePort.setTargetPort(new IntOrString(port, null, null, Collections.EMPTY_MAP));
        return servicePort;
    }
}
