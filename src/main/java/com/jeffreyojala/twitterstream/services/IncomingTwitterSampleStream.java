package com.jeffreyojala.twitterstream.services;

import com.google.common.collect.Lists;
import com.jeffreyojala.twitterstream.config.AppConfig;
import com.twitter.hbc.ClientBuilder;
import com.twitter.hbc.core.Constants;
import com.twitter.hbc.core.Hosts;
import com.twitter.hbc.core.HttpHosts;
import com.twitter.hbc.core.endpoint.StatusesSampleEndpoint;
import com.twitter.hbc.core.processor.StringDelimitedProcessor;
import com.twitter.hbc.httpclient.BasicClient;
import com.twitter.hbc.httpclient.auth.Authentication;
import com.twitter.hbc.httpclient.auth.OAuth1;
import com.twitter.hbc.twitter4j.Twitter4jStatusClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Main handler for configuring and recieving tweet stream
 */
@Service
public class IncomingTwitterSampleStream {

    @Autowired
    private TwitterStatusStreamHandler statusListener;

    @Autowired
    private AppConfig appConfig;

    @Value("${auth.consumerkey}")
    private String consumerKey;

    @Value("${auth.consumersecret}")
    private String consumerSecret;

    @Value("${auth.token}")
    private String token;

    @Value("${auth.tokensecret}")
    private String tokenSecret;

    /**
     * Configure client and start consuming our sample tweet stream
     */
    public void consume() {

        // this blocking queue is essentially a "ram persistence strategy" where we queue up message and store them
        // in ram in this blocking message queue
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(appConfig.tweetQueueDepth());


        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesSampleEndpoint sampleEndpoint = new StatusesSampleEndpoint();

        Authentication authentication = new OAuth1(consumerKey, consumerSecret, token, tokenSecret);

        BasicClient client = new ClientBuilder()
                                        .hosts(hosts)
                                        .endpoint(sampleEndpoint)
                                        .authentication(authentication)
                                        .processor(new StringDelimitedProcessor(msgQueue))
                                        .build();

        int numProcessingThreads = appConfig.ingestExecutors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessingThreads);

        // The Twitter4jStatusClient is the suggested Java based client by twitter itself
        // and handles concerns such as receiving the main tweet stream, queueing up messages
        // in ram etc. It ensures that the stream can be received and then processed asynchronously
        // by the "client" passed in.
        Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(client, msgQueue, Lists.newArrayList(statusListener), executorService);
        t4jClient.connect();

        // we need to call process once for each processing thread
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            t4jClient.process();
        }
    }
}
