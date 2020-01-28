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
import org.springframework.stereotype.Service;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Service
public class IncomingTwitterSampleStream {

    @Autowired
    private TwitterStatusStreamHandler statusListener;

    @Autowired
    private AppConfig appConfig;

    public void consume() {

        // this blocking queue is essentially a "ram persistence strategy" where we queue up message and store them
        // in ram in this blocking message queue
        BlockingQueue<String> msgQueue = new LinkedBlockingQueue<String>(appConfig.tweetQueueDepth());


        Hosts hosts = new HttpHosts(Constants.STREAM_HOST);
        StatusesSampleEndpoint sampleEndpoint = new StatusesSampleEndpoint();

        // TODO: These secrets should be read from a config file
        Authentication authentication = new OAuth1("ah7gGfnU3bO9r05uamt0gyczl", "xWcflkEE9S1VtmsW8kZC36w25qvVFe5oRuZCS2xEpzjVeVSvoe", "1219783949066690560-9TgO1iBT0QwGQ4FwfGZ6INHVImJGOH", "YDEnq3KDRqtWeqSdBXTqFdDPuu1ic9U01F4JpIkdbfuFP");

        BasicClient client = new ClientBuilder()
                                        .hosts(hosts)
                                        .endpoint(sampleEndpoint)
                                        .authentication(authentication)
                                        .processor(new StringDelimitedProcessor(msgQueue))
                                        .build();

        int numProcessingThreads = appConfig.ingestExecutors();
        ExecutorService executorService = Executors.newFixedThreadPool(numProcessingThreads);

        Twitter4jStatusClient t4jClient = new Twitter4jStatusClient(client, msgQueue, Lists.newArrayList(statusListener), executorService);
        t4jClient.connect();

        System.out.println("Woot woot");
        for (int threads = 0; threads < numProcessingThreads; threads++) {
            System.out.println("hells yeah");
            t4jClient.process();
        }
    }
}
