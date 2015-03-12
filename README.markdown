# Dryuf BigIo

## IO Buffers

The project implements stateless buffers, called FlatBuffer. This is similar to
Java Nio ByteBuffer except two important things:

- They don't maintain any state, such as position or limit.
- They can address 64-bit memory area and map the 64-bit size files.


## FlatChannel

The interface FlatChannel provides reads and writes methods from arbitrary
position.

This should have been part of original JDK but unfortunately only methods
reached the FileChannel class, making it difficult to implement virtual
channels in standardized way.


## License

The code is released under version 2.0 of the [Apache License][].


## Stay in Touch

Zbynek Vyskovsky

Feel free to contact me at kvr000@gmail.com and http://github.com/kvr000

[Apache License]: http://www.apache.org/licenses/LICENSE-2.0
