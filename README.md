
# Overview

mxlang is a custom built scripting language that assists in unit testing IBM Maximo integrations.

The aim of this solution was to allow consultants, programmers, solutions engineers and integration engineers the ability to
consistenly test their integrations with IBM Maximo without user interaction from the frontend.

The core code-base is based on the book, Crafting Interpreters by Bob Nystrom.

For additional documentation, please refer to: [this link here](https://sebastianbarry.info/docs/mxlang)

# Instructions

#### ./build_jar.sh
builds the mxlang binary for us in the target/ dir.

# Example Snippet

> Simple example of report building.
```javascript
use "http";
use "report";

var response = http.get("https://google.com");
print response.getStatusCode();

@test
fun test_function() {
    print true;
    pass();
}

var r = report.init(
    $("title", "value"),
    $("path", "./output.pdf")
);

r.build();
```

Another one:
```javascript
use "utils";
use "http";
use "mxtest";
use "report";
use "mas";

/// http.get("https://google.com");


var a = array.new(1, 2, 3, 5);

a.getAt(0);

// a.listAll();
a.append(1);
a.setAt(1, 10);
// a.size();


var d = dictionary.new(
    $(6 , 3),
    $(1 , 4)
);

/// d.put(1, 2);

/// print d.containsKey(1);

/// d.put(1, 2);
/// d.getKey(1);
/// var keys = d.keys();


var b = array.copyFrom(a);

var d = 4;

/// print size("asdflk");


// @test
fun test_this() {
    var return_value = http.ping("https://youtube.com").getStatusCode();
    print(return_value);
}


/// print typeof(int(100));


var testr = mxtest.init
    (
        $("apikey", "alk3jlkjlkjlkj"),
        $("apiendpoint","https://webhook.site/f4038894-47eb-4314-b9f8-fe8896663971"),
        $("oslcendpoint", "https://jsonplaceholder.typicode.com/posts/1/comments")
    );

// https://webhook.site/67c967f5-2868-478a-8415-5f0dc95c593f

// print testr.getApiEndpoint();
// print testr.getApiKey();

// @test
fun test_ping() {
    var responseCode = testr.oslc.ping().getStatusCode();
    if( responseCode != 200 ) {
        fail( "this failed" );
    } else {
        pass();
    }
}

// @test
fun test_get() {
    var response = testr.oslc.getjson().getResponseBody();
    print(response);
    var email = response.getFromPath("array.0.name.value");
    print(email);
}



// @test
fun test_post() {
    var json = "
    { 'test': [ 'a', 'b' ] }
    ";
    var response = testr.api.postjson(json);
    print(response);
    if( response.getStatusCode() != 200) {
        fail("status code is not 200");
    } else {
        pass();
    }
    assertEquals("MAX", "MAX");
    // print(response.getResponseBody());
}

// @test
fun test_usernamepassword() {
    var mxtestapi = mxtest.init();
    mxtestapi.setUsername("username");
    mxtestapi.setPassword("password");
    mxtestapi.setOslcEndpoint("https://webhook.site/f4038894-47eb-4314-b9f8-fe8896663971");
    var response = mxtestapi.oslc.ping();
    print response.getStatusCode();
}


// @test
fun test_post_post() {
    var mxtestapi = mxtest.init (
        $("apikey", "alk3jlkjlkjlkj"),
        $("apiendpoint",  "https://jsonplaceholder.typicode.com/posts/1/comments"),
        $("oslcendpoint", "https://jsonplaceholder.typicode.com/posts/1/comments")
    );
    var endpoint = mxtestapi.getApiEndpoint();
    var r = report.init("the title", "description", "test.pdf");
    r.addSection("yes", "yes", "yes", "type=text");
    r.build();
}

@test
fun test_mas() {
    var logger = mas.init().logging().fromOpenShift();
    var path = "/Users/sebastianbarry/.kube/zingg-prod-kubeconfig.yaml";
    logger.setNamespace("zingg");
    logger.setPodName("zingg-api-6bc8d96d7-m456l");
    logger.setOpenShiftConfig(path);
    logger.dumpToConsole();
}


// @test
fun test_mas_2() {
    var logger = mas.logging().fromApi();
    logger.setConfigFile("file");
    logger.setStanza("stanza");
    logger.generateLogs();
}



// .

```

# In Dev

### Features

[x] JSON String? or file read?
[x] POST data
[x] apiendpoint instance
[x] report builder
[x] json parser bug fixes.
[x] add junit testing
[x] report builder, manually add sections (title, [value], type="json","text",... )


### In Progress

[] fix http headers in the mxtest instance classes.
[] refine http instace methods.
[] mxtest bug fixes.


### Future

[] mxtest SQL driver
[] mxtest SOAP stuff.


# Copyright Information

This project is licensed under the MIT License. Please refer to the LICENSE file for more information.
All corresponding jar files and dependencies are owned by their respective owners.

This library is not affiliated with or a validated solution by IBM or any of its subsidiaries.
It is a custom built solution by Sebastian Barry.


