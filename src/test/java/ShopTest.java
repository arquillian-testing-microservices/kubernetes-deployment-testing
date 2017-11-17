import io.fabric8.kubernetes.api.model.v2_6.Service;
import java.io.IOException;
import java.net.URL;
import org.arquillian.cube.kubernetes.annotations.Named;
import org.arquillian.cube.kubernetes.annotations.PortForward;
import org.arquillian.cube.kubernetes.impl.requirement.RequiresKubernetes;
import org.arquillian.cube.requirement.ArquillianConditionalRunner;
import org.jboss.arquillian.test.api.ArquillianResource;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.assertFalse;
import static org.junit.Assert.assertNotNull;

@RunWith(ArquillianConditionalRunner.class)
@RequiresKubernetes
public class ShopTest {

    @Named("redis-master")
    @ArquillianResource
    Service redis;

    @Named("redis-master")
    @PortForward
    @ArquillianResource
    URL url;

    @Test
    public void shouldFindServiceInstance() throws IOException {
        assertNotNull(redis);
        assertNotNull(redis.getSpec());
        assertNotNull(redis.getSpec().getPorts());
        assertFalse(redis.getSpec().getPorts().isEmpty());
    }
}