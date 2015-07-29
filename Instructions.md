# Getting Started #

## Processing ##
**prototile** is built using [Processing](http://www.processing.org).  To run or compile **prototile**, you must download Processing from http://www.processing.org/download, install it, and locate the core.jar.  If you are planning to checkout the **prototile** source code, you will need to check the processing core.jar into your local Maven repository.  (I haven't found it in an existing repo.)

```
mvn install:install-file -DgroupId=org.processing -DartifactId=processing-core -Dversion=1.0.9 -Dpackaging=jar -Dfile=core.jar -DgeneratePom=true -DcreateChecksum=true
```

## Prototile ##
(For now,) a current **prototile** jar can be grabbed from the [repository](http://code.google.com/p/prototile/source/browse/trunk/target/prototile-core-0.0.1-SNAPSHOT.jar).  I will consider making a jar available that contains the processing core jar.

Usage is
```
java -Xmx1024m -Xms1024m -cp prototile-core-0.0.1-SNAPSHOT.jar:processing-core-1.0.9.jar prototile.core.Ascension [-compact|-split]
```

Additional information can be found in the [README](http://code.google.com/p/prototile/source/browse/trunk/README.txt).