package com.jeffreyojala.twitterstream;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TwitterStreamApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void handleDisconnect() {

		/*
		If a client reaches its upper threshold of its time between reconnects, it should
		send you notifications so you can triage the issues affecting your connection.
		*/
	}

	@Test
	void handleBackOff() {
		/*
			Back off linearly for TCP/IP level network errors. These problems are generally temporary and
			tend to clear quickly. Increase the delay in reconnects by 250ms each attempt, up to 16 seconds.

			Back off exponentially for HTTP errors for which reconnecting would be appropriate. Start with a
			5 second wait, doubling each attempt, up to 320 seconds.

			Back off exponentially for HTTP 420 errors. Start with a 1 minute wait and double each attempt.
			Note that every HTTP 420 received increases the time you must wait until rate limiting will no longer will be in effect for your account.
		 */

		/*
			A good way to test a backoff implementation is to use invalid authorization credentials and
			examine the reconnect attempts. A good implementation will not get any 420 responses.
		 */
	}

	@Test
	void handleRateLimit() {


	}

	@Test
	void handleStalls() {
		/*
			Set a timer, either a 90 second TCP level socket timeout, or a 90 second application level timer on
			the receipt of new data. If 90 seconds pass with no data received, including newlines, disconnect and
			reconnect immediately according to the backoff strategies in the next section. The Streaming API will
			send a keep-alive newline every 30 seconds to prevent your application from timing out the connection.
			You should wait at least 3 cycles to prevent spurious reconnects in the event of network congestion,
			local CPU starvation, local GC pauses, etc.
		 */
	}

	@Test
	void handleDnsChange() {
		/*
		Test that your client process honors the DNS Time To live (TTL). Some stacks will cache a resolved address for the duration of the process and will not pick up DNS changes within the proscribed TTL.

		Such aggressive caching will lead to service disruptions on your client as Twitter shifts load between IP addresses.
		 */
	}
}
