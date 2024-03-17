# Dryuf BigIo

Java IO framework, managing limitless 64-bit IO, lock-free access, comittable IO, etc.


## FlatBuffer

The project implements stateless buffers, called FlatBuffer. This is similar to
Java Nio ByteBuffer except two important things:

- They don't maintain any state, such as position or limit.
- They can address 64-bit memory area and map the 64-bit size files.

```java
FileChannel channel = FileChannel.open(Paths.get(myHugeFile));
FlatBuffer buffer = MappedFlatBuffer.from(channel, FileChannel.MapMode.READ_ONLY, 0, -1);
byte byteAt5G = buffer.getByte(5_000_000_000L);
```


## FlatChannel

The interface FlatChannel provides reads and writes methods from arbitrary
position.

This should have been part of original JDK but unfortunately only methods
reached the FileChannel class, making it difficult to implement virtual
channels in standardized way.

```java
channel = FlatChannels.fromFile(FileChannel.open(file));
channel = FlatChannels.fromBytes(new byte[1_000_000]);
// can be run in parallel from multiple threads, not sharing the position
CompletableFuture.runAsync(() -> channel.read(buffer1, 0));
CompletableFuture.runAsync(() -> channel.read(buffer2, 100));
```


## Committable, CommittableOutputStream

Committable interface allows output objects to be marked as completed, so it's clear to the client whether it can safely
consume the output or it should result into exception.  This is typically common with the try-with-resources statement
where `close()` method always successfully closes the resource, no matter whether it's consistent.  In HTTP stream for
example, it can result into successfully closing the stream and pretend it's complete while it could have been actually
closed because of fatal error.  `Committable` interface adds `commitable(boolean committable)` method which allows 
marking the object as complete or incomplete.

```java
try (CommittableOutputStream output = new SocketCommittableOutputStream(channel)) {
	output.write("Hello\n".getBytes(StandardCharsets.UTF_8));
	output.committable(true);
    // do other stuff
    // mark as non-committable during partial message
    output.committable(false);
    output.write("Bye, ".getBytes(StandardCharsets.UTF_8));
    output.write("World\n".getBytes(StandardCharsets.UTF_8));
    // mark as committable again, once the message is completed
    output.committable(true);
}
```


## FilenameVersionComparators

Filename comparators, depending on path, version, etc.

```java
// this returns -1 despite both path and filename are greater - but the version in file is lower:
FilenameVersionComparators.FILENAME_ONLY_COMPARATOR.compare("c/d/hello-1.txt", "a/b/bye-2.txt");

// this returns -1 despite both filename version are greater - but the version in path is lower:
FilenameVersionComparators.PATH_COMPARATOR.compare("abc-1/hello-20-1", "xyz-2/world-20");
```


## Usage

### Release

```
<dependency>
	<groupId>net.dryuf</groupId>
	<artifactId>dryuf-bigio</artifactId>
	<version>1.2.1</version>
</dependency>
```

## License

The code is released under version 2.0 of the [Apache License][].


## Stay in Touch

Author: Zbynek Vyskovsky

Feel free to contact me at kvr000@gmail.com and http://github.com/kvr000/ and http://github.com/dryuf/dryuf-bigio/ and https://www.linkedin.com/in/zbynek-vyskovsky/

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
