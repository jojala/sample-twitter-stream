## Goal
Build an application that connects to the Tweets API and processes incoming tweets to compute the following statistics:
* Total number of tweets received
* Average tweets per hour/minute/second
* Top emojis in tweets
* Percent of tweets that contains emojis
* Top hashtags
* Percent of tweets that contain a url
* Percent of tweets that contain a photo url (pic.twitter.com, pbs.twimg.com, or instagram)
* Top domains of urls in tweets

## Stack
* Springboot 2.2.4
* Java 8
* Maven 1.3

## How to run tests
`mvn test`

## How to run application
`mvn clean package spring-boot:run -Dspring-boot.run.arguments=--auth.consumersecret={consumerSecret},--auth.consumerkey={consumerKey},--auth.token={token},--auth.tokensecret={tokenSecret}`

## How to start stream processing
`curl localhost:8080/start`

## How to receive tweet stats
`curl localhost:8080/stats`

*example JSON response:*
```
{
    "totalNumberOfTweetsReceived": 64,
    "currentTweetsPerSecond": 3.1,
    "currentTweetsPerMinute": 1.0666666666666667,
    "currentTweetsPerHour": 1.0666666666666667,
    "topTenEmojis": {
        "1": {
            "value": {
                "unifiedCode": "1f49c",
                "name": "PURPLE HEART",
                "rankKey": "1f49c"
            },
            "rank": 1,
            "count": 2
        },
        "2": {
            "value": {
                "unifiedCode": "1f43e",
                "name": "PAW PRINTS",
                "rankKey": "1f43e"
            },
            "rank": 2,
            "count": 1
        },
        "3": {
            "value": {
                "unifiedCode": "1f9e1",
                "name": "ORANGE HEART",
                "rankKey": "1f9e1"
            },
            "rank": 3,
            "count": 1
        },
        "4": {
            "value": {
                "unifiedCode": "1f3af",
                "name": "DIRECT HIT",
                "rankKey": "1f3af"
            },
            "rank": 4,
            "count": 1
        },
        "5": {
            "value": {
                "unifiedCode": "1f63d",
                "name": "KISSING CAT FACE WITH CLOSED EYES",
                "rankKey": "1f63d"
            },
            "rank": 5,
            "count": 1
        },
        "6": {
            "value": {
                "unifiedCode": "1f49a",
                "name": "GREEN HEART",
                "rankKey": "1f49a"
            },
            "rank": 6,
            "count": 1
        },
        "7": {
            "value": {
                "unifiedCode": "1f618",
                "name": "FACE THROWING A KISS",
                "rankKey": "1f618"
            },
            "rank": 7,
            "count": 1
        },
        "8": {
            "value": {
                "unifiedCode": "1f49b",
                "name": "YELLOW HEART",
                "rankKey": "1f49b"
            },
            "rank": 8,
            "count": 1
        },
        "9": {
            "value": {
                "unifiedCode": "1f499",
                "name": "BLUE HEART",
                "rankKey": "1f499"
            },
            "rank": 9,
            "count": 1
        }
    },
    "percentageOfTweetsThatContainEmojis": 0.109375,
    "topTenHashTags": {
        "1": {
            "value": {
                "hashTag": "BoltonMustTestify",
                "rankKey": "BoltonMustTestify"
            },
            "rank": 1,
            "count": 1
        },
        "2": {
            "value": {
                "hashTag": "TheBatman",
                "rankKey": "TheBatman"
            },
            "rank": 2,
            "count": 1
        },
        "3": {
            "value": {
                "hashTag": "LakeShow",
                "rankKey": "LakeShow"
            },
            "rank": 3,
            "count": 1
        }
    },
    "percentageOfTweetsThatContainAUrl": 0.15625,
    "percentageOfTweetsThatContainAPhotoUrl": 0,
    "topTenDomains": {
        "1": {
            "value": {
                "domain": "twitter.com",
                "rankKey": "twitter.com"
            },
            "rank": 1,
            "count": 8
        },
        "2": {
            "value": {
                "domain": "bit.ly",
                "rankKey": "bit.ly"
            },
            "rank": 2,
            "count": 1
        }
    }
}
```
## Classes of Interest
`IncomingTwitterSampleStream` - Main handler for configuring and recieving tweet stream

`TwitterStatusStreamHandler` - Handler for receive tweet status updates. Would be the entry point for managing application concerns for managing stream flow, but to date this has not been implemented.

`EmojiMaster` - Class to read in emoji.json file and store map of emojis by code

`TweetStatProcessor` - Main container class for processing status updates

## Packages of Interest
`[/test]com.jeffreyojala.twitterstream.processor` - Foldering containing tests for all the tweet processing classes

`[/]com.jeffreyojala.twitterstream.config` - Main application config where a user can available resources to allocate or similar app configuration (keys are NOT in here, injected via environment variables or command line args)

`[/]com.jeffreyojala.twitterstream.http` - HTTP endpoints to control stream processing or query for statistics

`[/]com.jeffreyojala.twitterstream.models` - POJO style models

`[/]com.jeffreyojala.twitterstream.processor` - Group of tweet processors. *MAIN BUSINESS LOGIC*

`[/]com.jeffreyojala.twitterstream.services` - Services to manage stream and other application data

`[/]com.jeffreyojala.twitterstream.statcontainers` - Data containers to store (in memory) statistical data, manage time based data transitions and compute basic statistics


## Scaling Concerns

*“Single Demo App”*

General goal of scaling any request based application or in this case a stream driven application is to accept data as quickly as possible and then process that data asynchronously. For statistical type data the initial consideration one should take is “can it be calculated on the fly on request?” If not, pre-calculating those statistics on a regular cadence so that they are available for query might be necessary. It’s usually a balance / trade-off of how stale you can tolerate the statistics, how fast you need the statistics to be returned on request, expense of scaling compute power vs ability to scale available ram. If these statistics need to be persistent over application cycles the statistics could be stored in a database, relational or no-sql depending on your needs and preferences. Similarly to in memory solution, the persistence statistics would be not be updated on every receive event but rather updated on a regular cadence that matches system performance requirements.

*“Production On Prem”*

If this application were to live in a standard on prem style datacenter, a hardware based solution could allow a properly designed system to scale. At a previous company we used an F5 Big IP “layer 7” load balancer which allowed traffic to be split up based on information in the requests itself. The traffic was processed in hardware making it extremely fast in routing traffic and we could more than likely accept the entire tweet stream and shard the requests to various applications nodes based on volume, location, user or however makes sense for our requirements.

![GitHub Logo](/docs/f5_specs.png)


*“Cloud Solution”*

All else being equal, if it was my choice I would adopt a cloud based solution as I’m a big believer in leveraging the scale of existing at scale solutions and focusing on problems unique to your business. One example could be to use AWS’s Kinesis / Firehose to accept and manage the tweet stream, process the incoming data asynchronously on AWS Lambdas or one of several scalable compute resources (ECS, EKS  etc) and then ultimately store the data in elastic search, red shift, S3 etc. Again, there’s so many options to fit your applications exact need for performance, scale and cost.

![GitHub Logo](/docs/aws_big_data.png)

	
*“Operate-ability”*

From my experience, beyond even picking the right technology stack or right system architecture, the real key to scaling is building the tooling to be able to truly operate a system. Often this means creating or adopting off the shelf tooling for being able to observe system behavior, collect meaningful metrics, have systems in place to handle error conditions, down time etc. Sometimes it’s having the maturity to put in place triage processes for when systems do go down, how to manage client expectations and reports etc. 


## Potential Next Steps
* I started out specing out via test cases concerns around managing stream through disconnects, back off periods etc as the twitter documentation described, but I did not get that far in my time boxed efforts. Right now I’m not handling stream pauses or disconnects at all.
* Another big missing piece in my opinion is lack of monitor performance of processing tweets to drive efficiency. I created an aspect to be able to add annotations to be able to observer the length of time processing takes and did use it in a few cases during testing but ultimately it would be nice to have that adopted throughout the system and be able to monitor performance of the various processing stages over time.
* A lot of the functionality around processing is time based. Specifically the moving averaging testing, which is in the UrlStats object, has poor test coverage because I haven’t quite spent the time to come up with a way to simulate or mock the testing of the data over time. I suppose I could mock a few of the current register states, but I’m not sure I love that approach either. For now I left it with the all to famous `TODO:` comments.
* Lastly, I generally like to handle error conditions using event emit type patterns to collect statistics and tie in with automated reporting. This is especially important and large data processing pipelines where there are various pinch points and general points of failure. As of now there is not a lot of observability to this application.
