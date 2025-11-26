## Usage ##
- Steps to run the Mock Proxy Object Client Extension in a local Liferay using http://localhost:8080
  - Update the properties in \client-extensions\mock-proxy-object\src\main\resources\application.properties if not using http://localhost:8080
- Start Liferay 2025.Q3.10
- Within Liferay go to Control Panel > Instance Settings > Feature Flags > Release, search for 'Proxy Object' and enable the Proxy Object (LPS-135430) Feature Flag.
	- If using a Liferay DXP version before 2025.Q2 then the Feature Flag will be under Beta rather than Release.
- Build the client extension module
- Copy the CX LUFFA (i.e. client-extensions\mock-proxy-object\dist\mock-proxy-object.zip) to the liferay/client-extensions folder
- Copy the Spring Boot JAR (i.e. client-extensions\mock-proxy-object\build\libs\mock-proxy-object.jar) outside of the Liferay Workspace and run it e.g. with java -jar mock-proxy-object.jar
- Within Liferay go to Control Panel > Objects and click the + icon to create a new Custom Object.
- On the 'New Custom Object' dialog populate the mandatory fields and in the 'Storage Type' dropdown select 'Mock Object Entry Manager' and Save.
- Add some fields, leave Scope as Company and set Panel Link to Objects.
- Publish the Object.
- It is now available to use...

## Non-persistent Data ##
- The Object Records created when using the 'Mock Object Entry Manager' are not persisted. This data will be lost when the Spring Boot application is restarted.
- To retain the data use the headless API e.g. get...Page GET endpoint (with a pageSize sufficient to get all records) to extract the JSON.
- After restarting the environment load the data back in e.g. using the post...Batch POST endpoint using the JSON from above.

## Notes ##
- The Liferay Proxy Objects feature is a Beta feature before DXP 2025.Q2.
- The Liferay Workspace is using Liferay DXP 2025.Q3.10 and JDK 21 is expected for compile time and runtime.
- Liferay DXP 2025.Q3 and onwards use Jakarta...