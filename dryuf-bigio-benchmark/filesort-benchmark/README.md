# Comparison of sort functions

<!--- benchmark:table:sort_offsets:key=method-benchmark_run&compare=java&order=java&order=bytes&order=flat:: --->

|Benchmark|Mode|Units|    java|    bytes|     flat|java%|bytes%|flat%|
|:--------|:---|:----|-------:|--------:|--------:|----:|-----:|----:|
|sort     |avgt|ms/op|8842.437|29711.553|24373.360|   +0|  +236| +175|


# Raw data

<!--- benchmark:data:sort_offsets:all: --->
```
Benchmark                     Mode  Cnt      Score        Error  Units
FileSortBenchmark.sort_bytes  avgt    3  26696.112 ? 102112.639  ms/op
FileSortBenchmark.sort_flat   avgt    3  24630.286 ? 102608.779  ms/op
FileSortBenchmark.sort_java   avgt    3   9161.291 ?  32531.669  ms/op
```

<!--- benchmark:data:sort_noMultiplication:all: --->
```
Benchmark                    Mode  Cnt      Score        Error  Units
FileSortBenchmark.sort_flat  avgt    3  35301.273 ? 139843.791  ms/op
```

<!--- benchmark:data:sort_tcoBigger:all: --->
```
Benchmark                    Mode  Cnt      Score        Error  Units
FileSortBenchmark.sort_flat  avgt    3  37088.169 ? 152381.869  ms/op
```

(Measured on Intel 1185G7 4-core 8-thread 32 GB RAM)
