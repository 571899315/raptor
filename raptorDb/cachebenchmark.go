package main

import (
	"flag"
	"fmt"
	"math/rand"
	"time"
)

var typ, server, operation string

var total, valueSize, threads, keyspacelen, pipelen int64

func int() {

	flag.StringVar(&typ, "type", "redis", "cache server type")

	flag.StringVar(&server, "h", "localhost", "cache server address")

	flag.StringVar(&operation, "t", "set", "test set,could be get/set")

	flag.Int64Var(&total, "n", 1000, "total number of request")

	flag.Int64Var(&valueSize, "d", 1000, "data size of set/get value in bytes")

	flag.Int64Var(&threads, "c", 1, "number of parallel of connection")

	flag.Int64Var(&keyspacelen, "r", 0,
		"keyspacelen,use random keys from 0 to keyspacelen-1 ")

	flag.Int64Var(&pipelen, "p", 1, "pipeline length")

	flag.Parse()

	fmt.Println("type is:", typ)
	fmt.Println("server is:", server)

	fmt.Println("total is:", total)

	fmt.Println("datasize is:", valueSize)

	fmt.Println("we hava", threads, "connection")

	fmt.Println("operation is:", operation)

	fmt.Println("keyspacelen is:", keyspacelen)

	fmt.Println("pipeline length is:", pipelen)

	rand.Seed(time.Now().UnixNano())

}

func main() {
	ch := make(chan *result, threads)
}

type statistic struct {
	count int64
	time  time.Duration
}

type result struct {
	getCount  int64
	missCount int64
	setCount  int64

	statBuckets []statistic
}

func (r *result) addStatistic(bucket int64, statistic statistic) {

}

func (r *result) addDuration(bucket int64, statistic statistic) {

}
