
# Overview

mxlang is a custom built scripting language that assists in unit testing IBM Maximo integrations.

The aim of this solution was to allow consultants, programmers, solutions engineers and integration engineers the ability to
consistenly test their integrations with IBM Maximo without user interaction from the frontend.

The core code-base is based on the book, Crafting Interpreters by Bob Nystrom.

For additional documentation, please refer to: [this link here](https://sebastianbarry.info/docs/mxlang)

# Instructions

#### ./build_jar.sh
builds the mxlang binary for us in the target/ dir.


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


