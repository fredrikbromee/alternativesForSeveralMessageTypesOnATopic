# More than one messages type in a schema in protobuf

This is an example project that allows developers to see alternatives on how to declare protobuf schemas that contain
more than one message type. See e.g.
 https://www.confluent.io/blog/put-several-event-types-kafka-topic/ for a discussion on why this sometimes is a good
 idea.

The alternatives shown are:

1. Using Any: The Any concept allows for a generic top-level envelope definition where the actual message type is implicitly declared
in an [Any](https://developers.google.com/protocol-buffers/docs/proto3#any) field.

2. Using OneOf: Declaring all messages types in a OneOf gives an explicit schema for all message types.

Compare the protobuf schemas using [Any](src/main/proto/any/usingany.proto) and using [OneOf](src/main/proto/oneof/usingoneof.proto) 
here.

There are other alternatives for achieving this. Confluent schema registry recommends a version of 
[OneOf](https://docs.confluent.io/current/schema-registry/serdes-develop/serdes-protobuf.html#multiple-event-types-in-the-same-topic)
, and you could also manually build a union of all types, and I'm sure there are other solutions as well. 

## Prerequisites
gradle

## Building
To run the examples:
```
    gradle test
```

## License
This project uses the following license: [<CC0 1.0>](<https://creativecommons.org/publicdomain/zero/1.0/>).