package org.cloudfoundry.samples.music.web;

import com.microsoft.applicationinsights.TelemetryClient;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.microsoft.applicationinsights.telemetry.Duration;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import io.micrometer.core.annotation.Timed;

@RestController
@RequestMapping("/hello")
public class HelloController {
    private final Logger log = LoggerFactory.getLogger(HelloController.class);

    @Autowired
    TelemetryClient telemetryClient;

    private CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

    @GetMapping("/trackHttp")
    public int trackDependencyAutomatically() throws IOException {
        HttpGet httpGet = new HttpGet("https://www.google.com");
        int status;
        try (CloseableHttpResponse response = closeableHttpClient.execute(httpGet)) {
            status = response.getStatusLine().getStatusCode();
        }
        return status;
    }

    @GetMapping("/track")
    public String hellotrace() {

        // track a custom event
        //telemetryClient.trackEvent("Sending a custom event...");
        // trace a custom trace
        //telemetryClient.trackTrace("Sending a custom trace....");

        double drand = new Random().nextDouble();
        int rand = new Random().nextInt(10);
        Map<String, String> props = new HashMap<String,String>();
        props.put("team", "team" + rand);
        props.put("client", "client" + rand);

        // track a custom metric
        telemetryClient.trackMetric("CustomMetric", drand, 2, 0.0, 1.0, null, props);
        
        // track a custom dependency
        telemetryClient.trackDependency("Custom Service", "getClient", new Duration(0, 0, 1, rand, 1), true);

        return "hellotrace";
    }

    @GetMapping("/trackdim")
    public String hellodimension() {
        
        int rand = new Random().nextInt(10);
        // track custom dimension
        RequestTelemetry telemetry = new RequestTelemetry();
        telemetry.getProperties().put("team", "team" + rand);
        telemetry.getProperties().put("client", "client" + rand);

        telemetryClient.track(telemetry);

        return "hello";
    }

    @GetMapping("/trackex")
    public String trackHandledExeption() {
        try {
            throw new NullPointerException();
        } catch (Exception ex) {
            //telemetryClient.trackException(ex);
            ex.printStackTrace();
        }
        return "hello";
    }

    @GetMapping("/trackunhandledEx")
    public String unhandled() {
        throw new NullPointerException();
    }

   
     @GetMapping("/slow")
     @Timed("slowcall")
     public String slow() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            // e.printStackTrace();
        }
         return "hello slow";
     }

     @GetMapping("/hello")
     public String hello() {
        log.info("info logback hello");
        return "hello";
     }
 }