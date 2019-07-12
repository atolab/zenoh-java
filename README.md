# Zenoh Java Client API

The Java API for Zenoh, based on the zenoh-c API via JNI.

## Building
Requirements:
 - Java >= 8
 - Apache Maven >= 3.6.0
 - cmake, make, gcc (for zenoh-c compilation)

Optional for cross-compilation:
 - Docker

To build for your current platform:
```mvn clean install```

If zenoh-c is found in the same directory than zenoh-java, the build will copy its sources and compile it.
Otherwise, the build will clone the [zenoh-c](https://github.com/atolab/zenoh-c) repository and compile it.

Note that this Maven build offers profiles in addition of the default one:

 - ```mvn -Pdebug clean install```

    - compiles zenoh-c with debug logs active

 - ```mvn -Prelease clean install```

   - compiles zenoh-c in release mode (without logs)
   - cross-compiles zenoh-c on all supported platforms (incl. MacOS if this is your current host) using [dockross](https://github.com/dockcross/dockcross).
   - generates the Javadoc
   - generate a ZIP file for release in assembly/target


## Examples
See examples/README.md
