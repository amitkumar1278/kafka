# Before Creating Kafka Twitter producer we need to generate key to access twitter account.

Below is the step mentioned to do that:

### Step 1:

=>	open URL: http://apps.twitter.com/   or https://developer.twitter.com/en/apps
=>	Select Apply 	
=> 	Select "Apply for a developer account" 
=> 	Select "Which best describe you?" 
=>	Submit a standard application for access to the Twitter developer platform. click "Get Started" 
=>	Fill basic personal detail => next
=>	Fill intention of use => next
=>	Review => next
=> 	Submit the Agreement
=> 	Check your email and confirm it. 

Once verification is done you are welcome and you can start to create an app, this confirmation may take day or 2 based on your answer for use.

=>	select "Create an App" => App Name, description, give any URL of preference, explain how you are gona use.
=>	"Review Developer Terms" => create
=>	Select "Keys and tokens" 
=> 	Seelct "Regenerate" under Consumer Api Keys. => Access Token & Access token secret is generated.


### Step 2:
=> 	search "github twitter java" and search for application hbc. or go to "https://github.com/twitter/hbc"  it's basically a java client to consume twitter
=> 	we need to add below dependency.
	    <dependency>
	      <groupId>com.twitter</groupId>
	      <artifactId>hbc-core</artifactId> <!-- or hbc-twitter4j -->
	      <version>2.2.0</version> <!-- or whatever the latest version is -->
	    </dependency>










