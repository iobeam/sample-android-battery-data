# Sample iobeam Android app
*Using the iobeam Java Client Library*

This is a basic sample app to illustrate how to send data to **iobeam** using the Java client library.

Here's what it looks like:

<img alt="Screenshot of the app" width="360" src="http://i.imgur.com/xw5T0tS.png" />

This app tracks the current battery level on your phone. Every time the battery level changes by more than 1%, the app uploads the timestamp and current level to **iobeam**.

All **iobeam** client library related code is in `MainActivity.java`

## Before you start ##

First, you need a `project_id`, and `project_token` (with write-access) from a valid **iobeam** project. You can get these using our [Command-line interface](http://github.com/iobeam/iobeam).

Next, find these lines in `MainActivity.java`, and update with your `project_id` and `project_token`. You also need to pick a `device_id` for your phone (must be **globally unique** and **at least 16 characters long**). E.g., "nerf-herder-1138".

```java
/** Iobeam Parameters **/
private static final long PROJECT_ID = -1; // Your PROJECT_ID
private static final String PROJECT_TOKEN = null; // PROJECT_TOKEN (w/ write-access)
private static final String DEVICE_ID = null; // Specify your DEVICE_ID
```

*(Note: We specify the `device_id` in this example for simplicity, but you can also allow the library to auto-generate the `device_id` for you. Please see our [Java / Android client library docs](https://github.com/iobeam/iobeam-client-java) for more.)*

Compile, build, and run the app!

## Where's the iobeam-specific code? ##

It's all in `MainActivity.java`.

Initialize the iobeam client library:

```java
iobeam = new Iobeam.Builder(PROJECT_ID, PROJECT_TOKEN).saveIdToPath(path)
                .setDeviceId(DEVICE_ID).build();
```

Define the data schema: 

```java
String[] columns = new String[]{"battery_level", "temperature", "voltage"};
iobeamBatch = new DataBatch(columns);
iobeam.trackDataBatch(iobeamBatch);
```

Capture a data point:

```java
Map<String, Object> values = new HashMap<String, Object>();
values.put("battery_level", currentBatteryLevel);
values.put("temperature", ((double)temperature/10));
values.put("voltage", voltage);
iobeamBatch.add(values);
```

Send to the Iobeam API:

```java
iobeam.sendAsync();
```

That's it!

*Enjoy!*
