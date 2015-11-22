# PiCalculator

## Build and Run

    $ gradle asD

    $ gradle iD


## Optimise

1. This app use BigDecimal to do the division. However BigDecimal is not designed for computing task because it's immutable. Many temporary objects are generated during the computing process and cause the GC frequently. The better way is to write a customized helper class to store the value in byte[] and do the calculation in place. I made a draft in PiCalculator class but it need more time to test.

2. This app use ExecutorService to run the calculation at background in a single thread. This can be improved to multi-thread. It need more time to take care of the stop and resume function.

3. It's using a callback update listener to update the value to UI. It also can be done by a Handler and use message to do the communication.
