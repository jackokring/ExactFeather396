# Data Tools

The main purpose so far is to read and write various compressed streams and provide a standardized interface to `Components` or more exactly some types used by them such as `BufferedImage`.

It does involve getting a stream `getInputStream`, and then either getting the component `readComponent` or reading the stream `readStream` with a mangling adapter, which often does nothing but can do with built-in types such as `*.PNG`.

## TODO

* Audio
* Other multi-part container components.
* Maybe even time evolving components.
* Lower level file container or temp to `tar`.
* A better sign/verify mechanism based on git repositories.
* 